package com.todolist.controller;

import com.todolist.common.ApiResponse;
import com.todolist.dto.BindEmailRequest;
import com.todolist.dto.BindPhoneRequest;
import com.todolist.dto.ChangePasswordRequest;
import com.todolist.dto.UpdateProfileRequest;
import com.todolist.dto.UserProfileResponse;
import com.todolist.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 个人中心接口。
 */
@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    public ApiResponse<UserProfileResponse> profile() {
        return ApiResponse.ok(userService.getProfile());
    }

    @PutMapping("/profile")
    public ApiResponse<UserProfileResponse> updateProfile(@Valid @RequestBody UpdateProfileRequest request) {
        return ApiResponse.ok(userService.updateProfile(request.getNickname(), request.getAvatar()));
    }

    @PutMapping("/phone")
    public ApiResponse<UserProfileResponse> bindPhone(@Valid @RequestBody BindPhoneRequest request) {
        return ApiResponse.ok(userService.bindPhone(request.getPhone()));
    }

    @PutMapping("/email")
    public ApiResponse<UserProfileResponse> bindEmail(@Valid @RequestBody BindEmailRequest request) {
        return ApiResponse.ok(userService.bindEmail(request.getEmail()));
    }

    @PutMapping("/password")
    public ApiResponse<Void> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        userService.changePassword(request.getPhone(), request.getNewPassword());
        return ApiResponse.ok();
    }
}
