package com.todolist.controller;

import com.todolist.common.ApiResponse;
import com.todolist.dto.TodoRequest;
import com.todolist.dto.TodoResponse;
import com.todolist.dto.TodoStatsResponse;
import com.todolist.dto.TodoUpdateRequest;
import com.todolist.service.TodoService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 待办事项接口。
 */
@RestController
@RequestMapping("/api/todos")
public class TodoController {

    private final TodoService todoService;

    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    @GetMapping
    public ApiResponse<List<TodoResponse>> list(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Integer priority,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dueDate) {
        return ApiResponse.ok(todoService.list(status, priority, keyword, dueDate));
    }

    @GetMapping("/stats")
    public ApiResponse<TodoStatsResponse> stats() {
        return ApiResponse.ok(todoService.stats());
    }

    @GetMapping("/dates")
    public ApiResponse<Map<String, Long>> dates() {
        return ApiResponse.ok(todoService.dates());
    }

    @PostMapping
    public ApiResponse<TodoResponse> create(@Valid @RequestBody TodoRequest request) {
        return ApiResponse.ok(todoService.create(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<TodoResponse> update(@PathVariable Long id,
                                            @RequestBody TodoUpdateRequest request) {
        return ApiResponse.ok(todoService.update(id, request));
    }

    @PatchMapping("/{id}/toggle")
    public ApiResponse<TodoResponse> toggle(@PathVariable Long id) {
        return ApiResponse.ok(todoService.toggle(id));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        todoService.delete(id);
        return ApiResponse.ok();
    }
}
