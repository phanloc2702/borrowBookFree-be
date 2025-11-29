package com.bookmanagement.bookmanagementbackend.service.impl;

import com.bookmanagement.bookmanagementbackend.dto.request.FilterUserRequest;
import com.bookmanagement.bookmanagementbackend.dto.request.UserCreationRequest;
import com.bookmanagement.bookmanagementbackend.dto.request.UserUpdateRequest;
import com.bookmanagement.bookmanagementbackend.dto.response.UserResponse;
import com.bookmanagement.bookmanagementbackend.entity.User;
import com.bookmanagement.bookmanagementbackend.exception.BusinessException;
import com.bookmanagement.bookmanagementbackend.exception.ErrorCodeConstant;
import com.bookmanagement.bookmanagementbackend.mapper.UserMapper;
import com.bookmanagement.bookmanagementbackend.repository.UserRepository;
import com.bookmanagement.bookmanagementbackend.repository.specification.UserSpecification;
import com.bookmanagement.bookmanagementbackend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    public User createUser(UserCreationRequest request) {
        if(userRepository.findByUsername(request.getUsername()).isPresent()){
            throw new BusinessException("User already exists", ErrorCodeConstant.USERNAME_ALREADY_EXIST);
        }
        if(userRepository.findByEmail(request.getEmail()).isPresent()){
            throw new BusinessException("Email already exists", ErrorCodeConstant.EMAIL_ALREADY_EXIST);
        }
        User user = userMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole() != null ? request.getRole() : User.Role.USER);
        return userRepository.save(user);
    }

    public List<User> getAllUsers(){
        return userRepository.findAll();
    }
    public Page<UserResponse> getUsers(FilterUserRequest filterUserRequest){
        Specification<User> spec = UserSpecification.keywordContains(filterUserRequest.getKeyword());
        Pageable pageable  = PageRequest.of(filterUserRequest.getPageNumber(), filterUserRequest.getPageSize(), Sort.by("createdAt").descending());
        Page<User> users = userRepository.findAll(spec, pageable);
        return userMapper.toUserResponsePage(users);
    }
    public UserResponse getUserById(Long id){
        User user = userRepository.findById(id)
                .orElseThrow(()-> new BusinessException("User not found", ErrorCodeConstant.USER_NOT_FOUND));
        return userMapper.toUserResponse(user);
    }
    public UserResponse updateUser(Long id, UserUpdateRequest request){
        User user = userRepository.findById(id)
                .orElseThrow(()-> new BusinessException("User not found", ErrorCodeConstant.USER_NOT_FOUND));
        // 🔹 Kiểm tra username trùng (nếu user nhập mới)
        if (request.getUsername() != null
                && !request.getUsername().equals(user.getUsername())) {
            if (userRepository.findByUsername(request.getUsername()).isPresent()) {
                throw new BusinessException("Username already exists", ErrorCodeConstant.USERNAME_ALREADY_EXIST);
            }
            user.setUsername(request.getUsername());
        }

        // 🔹 Kiểm tra email trùng (nếu user nhập mới)
        if (request.getEmail() != null
                && !request.getEmail().equals(user.getEmail())) {
            if (userRepository.findByEmail(request.getEmail()).isPresent()) {
                throw new BusinessException("Email already exists", ErrorCodeConstant.EMAIL_ALREADY_EXIST);
            }
            user.setEmail(request.getEmail());
        }

        if (request.getFullName() != null) {
            user.setFullName(request.getFullName());
        }

        if (request.getRole() != null) {
            user.setRole(request.getRole());
        }

        // 🔹 Nếu có password thì mã hóa lại
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        userRepository.save(user);

        return userMapper.toUserResponse(user);
    }
    public void deleteUser(Long id){
        User user = userRepository.findById(id)
                .orElseThrow(()-> new BusinessException("User not found", ErrorCodeConstant.USER_NOT_FOUND));
        userRepository.delete(user);
    }

}
