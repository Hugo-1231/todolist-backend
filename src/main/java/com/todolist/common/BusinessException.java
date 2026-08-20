package com.todolist.common;

import lombok.Getter;

/**
 * 业务异常，携带 HTTP 状态码与提示信息。
 */
@Getter
public class BusinessException extends RuntimeException {

    private final int code;

    public BusinessException(String message) {
        this(400, message);
    }

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }
}
