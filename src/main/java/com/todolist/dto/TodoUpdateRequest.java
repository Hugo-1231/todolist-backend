package com.todolist.dto;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import java.time.LocalDate;

/**
 * 更新待办请求（所有字段可选）。
 */
@Data
public class TodoUpdateRequest {

    @Size(max = 200, message = "标题最长 200 字符")
    private String title;

    @Size(max = 2000, message = "描述最长 2000 字符")
    private String description;

    private Boolean completed;

    @Min(value = 0, message = "优先级取值 0-2")
    @Max(value = 2, message = "优先级取值 0-2")
    private Integer priority;

    private LocalDate dueDate;
}
