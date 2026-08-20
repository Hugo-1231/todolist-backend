package com.todolist.repository;

import com.todolist.entity.Todo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TodoRepository extends JpaRepository<Todo, Long>, JpaSpecificationExecutor<Todo> {

    long countByUserId(Long userId);

    long countByUserIdAndCompleted(Long userId, boolean completed);

    long countByUserIdAndPriority(Long userId, Integer priority);

    @Query("select t.dueDate, count(t) from Todo t where t.user.id = :userId and t.dueDate is not null group by t.dueDate")
    List<Object[]> countGroupByDueDate(@Param("userId") Long userId);
}
