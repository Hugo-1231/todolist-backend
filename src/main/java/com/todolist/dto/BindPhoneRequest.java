package com.todolist.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 绑定手机号请求。
 */
@Data
public class BindPhoneRequest {

    @NotBlank(message = "手机号不能为空")
    private String phone;
}
