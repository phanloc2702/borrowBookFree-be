package com.bookmanagement.bookmanagementbackend.service.impl;

import com.bookmanagement.bookmanagementbackend.dto.request.AuthenticationRequest;
import com.bookmanagement.bookmanagementbackend.dto.request.IntrospectRequest;
import com.bookmanagement.bookmanagementbackend.dto.response.AuthenticationResponse;
import com.bookmanagement.bookmanagementbackend.dto.response.IntrospectResponse;
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
        return AuthenticationResponse.builder()
                .token(token)
                .authenticated(true)
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
        // Implement token generation logic (e.g., JWT)
}
