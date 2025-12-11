package com.bookmanagement.bookmanagementbackend.service.impl;

import com.bookmanagement.bookmanagementbackend.dto.request.AuthenticationRequest;
import com.bookmanagement.bookmanagementbackend.dto.request.ChangePasswordRequest;
import com.bookmanagement.bookmanagementbackend.dto.request.IntrospectRequest;
import com.bookmanagement.bookmanagementbackend.dto.request.RegisterRequest;
import com.bookmanagement.bookmanagementbackend.dto.response.AuthenticationResponse;
import com.bookmanagement.bookmanagementbackend.dto.response.IntrospectResponse;
import com.bookmanagement.bookmanagementbackend.dto.response.UserResponse;
import com.bookmanagement.bookmanagementbackend.entity.User;
import com.bookmanagement.bookmanagementbackend.exception.BusinessException;
import com.bookmanagement.bookmanagementbackend.exception.ErrorCodeConstant;
import com.bookmanagement.bookmanagementbackend.repository.UserRepository;
import com.bookmanagement.bookmanagementbackend.service.AuthenticationService;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    @Autowired
    private UserRepository userRepository;
    @NonFinal
    @Value("${jwt.signerKey}")
    private String SIGNER_KEY;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
    public IntrospectResponse introspect(IntrospectRequest request) throws JOSEException, ParseException {
        String token = request.getToken();
        JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());
        SignedJWT signedJWT = SignedJWT.parse(token);
        Date expirationDate = signedJWT.getJWTClaimsSet().getExpirationTime();

        boolean verified = signedJWT.verify(verifier);
        return IntrospectResponse.builder()
                        .valid(verified && expirationDate.after(new Date()))
                        .build();

    }
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(()-> new BusinessException("Invalid email", ErrorCodeConstant.USER_NOT_FOUND));
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        boolean authenticated = passwordEncoder.matches(request.getPassword(), user.getPassword());
        if (!authenticated) {
            throw new BusinessException("Invalid password", ErrorCodeConstant.INVALID_PASSWORD);
        }
        String token = generateToken(request.getEmail());
        UserResponse userResponse = UserResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .role(user.getRole().name())   // tuỳ bạn lưu role kiểu gì
                .build();
        return AuthenticationResponse.builder()
                .token(token)
                .authenticated(true)
                .user(userResponse)
                .build();
    }
    private String generateToken(String email) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

        JWTClaimsSet jwtClaimsSet= new JWTClaimsSet.Builder()
                .subject(email)
                .issuer("book-management-system")
                .issueTime(new Date())
                .expirationTime(new Date(
                        Instant.now().plus(1, ChronoUnit.HOURS).toEpochMilli()
                ))
                .claim("role", "USER")
                .build();
        Payload payload = new Payload(jwtClaimsSet.toJSONObject());
        JWSObject jwsObject = new JWSObject(header, payload);
        try {
            jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            log.error("Error while sign the token", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public AuthenticationResponse register(RegisterRequest request) {
        // 1. Validate trùng username
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BusinessException("Username already exists", ErrorCodeConstant.USERNAME_ALREADY_EXIST);
        }

        // 2. Validate trùng email (rất nên làm, vì login dùng email)
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new BusinessException("Email already exists", ErrorCodeConstant.EMAIL_ALREADY_EXIST);
        }

        // 3. Mã hoá mật khẩu
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);

        // 4. Tạo user mới
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setRole(User.Role.USER); // default USER, tuỳ bạn

        userRepository.save(user);

        // 5. Tạo JWT sử dụng hàm generateToken(String email) bạn đã viết
        String token = generateToken(user.getEmail());

        // 6. Build UserResponse giống authenticate()
        UserResponse userResponse = UserResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();

        // 7. Trả về AuthenticationResponse cùng format với login
        return AuthenticationResponse.builder()
                .token(token)
                .authenticated(true)
                .user(userResponse)
                .build();
    }
    @Override
    public void changePassword(ChangePasswordRequest request) {
        // 1. Tìm user theo email
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BusinessException(
                        "Email không tồn tại",
                        ErrorCodeConstant.USER_NOT_FOUND
                ));

        // 2. Kiểm tra mật khẩu cũ
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new BusinessException(
                    "Mật khẩu hiện tại không đúng",
                    ErrorCodeConstant.INVALID_PASSWORD
            );
        }

        // 3. Không cho trùng mật khẩu cũ (optional)
        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            throw new BusinessException(
                    "Mật khẩu mới phải khác mật khẩu hiện tại",
                    ErrorCodeConstant.INVALID_REQUEST
            );
        }

        // 4. Encode và lưu mật khẩu mới
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    // Implement token generation logic (e.g., JWT)
}
