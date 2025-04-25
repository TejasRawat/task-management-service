package com.example.taskmanagement.infrastructure.mysql.specification;

import com.example.taskmanagement.domain.model.TaskPriority;
import com.example.taskmanagement.domain.model.TaskStatus;
import com.example.taskmanagement.infrastructure.mysql.entity.TaskMySQLEntity;
import java.time.LocalDate;
import org.springframework.data.jpa.domain.Specification;

public class TaskSpecifications {

  public static Specification<TaskMySQLEntity> hasStatus(TaskStatus status) {
    return (root, query, cb) -> cb.equal(root.get("status"), status.name());
  }

  public static Specification<TaskMySQLEntity> hasPriority(TaskPriority priority) {
    return (root, query, cb) -> cb.equal(root.get("priority"), priority.name());
  }

  public static Specification<TaskMySQLEntity> dueDateBefore(LocalDate dueDate) {
    return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("dueDate"), dueDate);
  }

}