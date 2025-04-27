package com.example.taskmanagement.adapters.out.repository.mysql.mapper;

import com.example.taskmanagement.domain.model.CreateTask;
import com.example.taskmanagement.domain.model.Task;
import com.example.taskmanagement.domain.model.TaskPriority;
import com.example.taskmanagement.domain.model.TaskStatus;
import com.example.taskmanagement.adapters.out.repository.mysql.entity.TaskMySQLEntity;
import java.util.Optional;

public class TaskMySQLMapper {

  public static Task toDomain(TaskMySQLEntity entity) {
    return Task.builder()
        .id(String.valueOf(entity.getId()))
        .title(entity.getTitle())
        .description(entity.getDescription())
        .priority(Optional.ofNullable(entity.getPriority()).map(TaskPriority::valueOf).orElse(null))
        .status(Optional.ofNullable(entity.getStatus()).map(TaskStatus::valueOf).orElse(null))
        .dueDate(entity.getDueDate())
        .createdDate(entity.getCreatedDate())
        .build();
  }

  public static TaskMySQLEntity toEntity(CreateTask createTask) {
    return TaskMySQLEntity.builder()
        .title(createTask.getTitle())
        .description(createTask.getDescription())
        .priority(Optional.ofNullable(createTask.getPriority())
            .map(Enum::name)
            .orElse(TaskPriority.NONE.name()))
        .status(TaskStatus.CREATED.name())
        .dueDate(createTask.getDueDate())
        .build();
  }

}
