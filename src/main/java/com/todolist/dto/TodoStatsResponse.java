package com.todolist.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 待办统计。
 */
@Data
@AllArgsConstructor
public class TodoStatsResponse {

    /** 总数 */
    private long total;

    /** 进行中 */
    private long active;

    /** 已完成 */
    private long completed;

    /** 高优先级 */
    private long highPriority;
}
