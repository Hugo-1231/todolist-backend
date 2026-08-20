package com.todolist.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 认证成功响应。
 */
@Data
@AllArgsConstructor
public class AuthResponse {

    private String token;

    private UserInfo user;
}
