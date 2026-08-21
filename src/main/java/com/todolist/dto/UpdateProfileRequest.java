package com.todolist.dto;

import lombok.Data;

import javax.validation.constraints.Size;

/**
 * 更新资料请求（昵称、头像均可选）。
 */
@Data
public class UpdateProfileRequest {

    @Size(max = 50, message = "昵称最长 50 字符")
    private String nickname;

    private String avatar;
}
