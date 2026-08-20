package com.todolist.dto;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDate;

/**
 * 创建待办请求。
 */
@Data
public class TodoRequest {

    @NotBlank(message = "标题不能为空")
    @Size(max = 200, message = "标题最长 200 字符")
    private String title;

    @Size(max = 2000, message = "描述最长 2000 字符")
    private String description;

    @Min(value = 0, message = "优先级取值 0-2")
    @Max(value = 2, message = "优先级取值 0-2")
    private Integer priority = 1;

    private LocalDate dueDate;
}
