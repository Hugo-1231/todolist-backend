package com.todolist.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 用户信息（脱敏，不含密码）。
 */
@Data
@AllArgsConstructor
public class UserInfo {

    private Long id;

    private String username;

    private String nickname;
}
