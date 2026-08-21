package com.todolist.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 绑定邮箱请求。
 */
@Data
public class BindEmailRequest {

    @NotBlank(message = "邮箱不能为空")
    private String email;
}
