package com.todolist.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * 手机号修改密码请求。
 */
@Data
public class ChangePasswordRequest {

    @NotBlank(message = "手机号不能为空")
    private String phone;

    @NotBlank(message = "新密码不能为空")
    @Size(min = 6, max = 100, message = "密码长度需在 6-100 之间")
    private String newPassword;
}
