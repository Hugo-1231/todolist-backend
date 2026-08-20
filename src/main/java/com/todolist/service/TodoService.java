package com.todolist.service;

import com.todolist.common.BusinessException;
import com.todolist.dto.TodoRequest;
import com.todolist.dto.TodoResponse;
import com.todolist.dto.TodoStatsResponse;
import com.todolist.dto.TodoUpdateRequest;
import com.todolist.entity.Todo;
import com.todolist.entity.User;
import com.todolist.repository.TodoRepository;
import com.todolist.repository.UserRepository;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.Predicate;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 待办事项业务。
 */
@Service
public class TodoService {

    private final TodoRepository todoRepository;
    private final UserRepository userRepository;

    public TodoService(TodoRepository todoRepository, UserRepository userRepository) {
        this.todoRepository = todoRepository;
        this.userRepository = userRepository;
    }

    /**
     * 列表查询，支持按状态 / 优先级 / 关键字筛选。
     */
    public List<TodoResponse> list(String status, Integer priority, String keyword, LocalDate dueDate) {
        Long userId = currentUserId();
        Specification<Todo> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("user").get("id"), userId));
            if ("active".equals(status)) {
                predicates.add(cb.isFalse(root.get("completed")));
            } else if ("completed".equals(status)) {
                predicates.add(cb.isTrue(root.get("completed")));
            }
            if (priority != null) {
                predicates.add(cb.equal(root.get("priority"), priority));
            }
            if (StringUtils.hasText(keyword)) {
                String pattern = "%" + keyword.trim() + "%";
                predicates.add(cb.or(
                        cb.like(root.get("title"), pattern),
                        cb.like(root.get("description"), pattern)));
            }
            if (dueDate != null) {
                predicates.add(cb.equal(root.get("dueDate"), dueDate));
            }
            query.orderBy(cb.desc(root.get("createdAt")));
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        return todoRepository.findAll(spec).stream()
                .map(TodoResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public TodoResponse create(TodoRequest request) {
        Todo todo = new Todo();
        todo.setUser(userRepository.getReferenceById(currentUserId()));
        todo.setTitle(request.getTitle());
        todo.setDescription(request.getDescription());
        todo.setPriority(request.getPriority() == null ? 1 : request.getPriority());
        todo.setDueDate(request.getDueDate());
        todo.setCompleted(false);
        LocalDateTime now = LocalDateTime.now();
        todo.setCreatedAt(now);
        todo.setUpdatedAt(now);
        return TodoResponse.from(todoRepository.save(todo));
    }

    @Transactional
    public TodoResponse update(Long id, TodoUpdateRequest request) {
        Todo todo = findOwned(id);
        if (request.getTitle() != null) {
            todo.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            todo.setDescription(request.getDescription());
        }
        if (request.getCompleted() != null) {
            todo.setCompleted(request.getCompleted());
        }
        if (request.getPriority() != null) {
            todo.setPriority(request.getPriority());
        }
        if (request.getDueDate() != null) {
            todo.setDueDate(request.getDueDate());
        }
        todo.setUpdatedAt(LocalDateTime.now());
        return TodoResponse.from(todoRepository.save(todo));
    }

    @Transactional
    public TodoResponse toggle(Long id) {
        Todo todo = findOwned(id);
        todo.setCompleted(!todo.isCompleted());
        todo.setUpdatedAt(LocalDateTime.now());
        return TodoResponse.from(todoRepository.save(todo));
    }

    @Transactional
    public void delete(Long id) {
        Todo todo = findOwned(id);
        todoRepository.delete(todo);
    }

    public TodoStatsResponse stats() {
        Long userId = currentUserId();
        long total = todoRepository.countByUserId(userId);
        long active = todoRepository.countByUserIdAndCompleted(userId, false);
        long completed = todoRepository.countByUserIdAndCompleted(userId, true);
        long highPriority = todoRepository.countByUserIdAndPriority(userId, 2);
        return new TodoStatsResponse(total, active, completed, highPriority);
    }

    /**
     * 各截止日期对应的待办数量，用于前端日历标记。
     */
    public Map<String, Long> dates() {
        Long userId = currentUserId();
        List<Object[]> rows = todoRepository.countGroupByDueDate(userId);
        Map<String, Long> result = new HashMap<>();
        for (Object[] row : rows) {
            LocalDate date = (LocalDate) row[0];
            Long count = (Long) row[1];
            result.put(date.toString(), count);
        }
        return result;
    }

    /**
     * 查找当前用户拥有的待办，否则抛 404。
     */
    private Todo findOwned(Long id) {
        Long userId = currentUserId();
        return todoRepository.findById(id)
                .filter(t -> t.getUser().getId().equals(userId))
                .orElseThrow(() -> new BusinessException(404, "待办不存在"));
    }

    /**
     * 当前登录用户 ID。
     */
    private Long currentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            return ((User) authentication.getPrincipal()).getId();
        }
        throw new BusinessException(401, "未登录");
    }
}
