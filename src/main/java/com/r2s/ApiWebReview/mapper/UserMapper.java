package com.r2s.ApiWebReview.mapper;

import com.r2s.ApiWebReview.dto.UserResponse;
import com.r2s.ApiWebReview.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserResponse toResponse(User user);

    default UserResponse toResponseWithRole(User user) {
        UserResponse dto = toResponse(user);
        if (user.getRole() != null) {
            dto.setRole(user.getRole().getName());
        } else {
            dto.setRole(null);
        }
        return dto;
    }
//        @Mapping(target = "role", source = "role.name")
//        UserResponse toResponseWithRole(User user);

}


