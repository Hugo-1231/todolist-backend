package com.todolist.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 用户资料响应（含头像、手机号、邮箱）。
 */
@Data
@AllArgsConstructor
public class UserProfileResponse {

    private Long id;

    private String username;

    private String nickname;

    private String avatar;

    private String phone;

    private String email;
}
