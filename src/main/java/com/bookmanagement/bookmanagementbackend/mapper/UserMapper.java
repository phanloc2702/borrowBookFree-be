package com.bookmanagement.bookmanagementbackend.mapper;

import com.bookmanagement.bookmanagementbackend.dto.request.RegisterRequest;
import com.bookmanagement.bookmanagementbackend.dto.request.UserCreationRequest;
import com.bookmanagement.bookmanagementbackend.dto.response.UserResponse;
import com.bookmanagement.bookmanagementbackend.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "role", ignore = true)
    User toUser(UserCreationRequest request);

    @Mapping(target = "password", ignore = true)
    @Mapping(target = "role", ignore = true)
    User toUser(RegisterRequest request);

    UserResponse toUserResponse(User user);
    default Page<UserResponse> toUserResponsePage(Page<User> users) {
        return users.map(this::toUserResponse);
    }
}

