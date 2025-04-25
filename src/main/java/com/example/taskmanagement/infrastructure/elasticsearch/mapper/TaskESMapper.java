package com.example.taskmanagement.infrastructure.elasticsearch.mapper;

import com.example.taskmanagement.domain.model.CreateTask;
import com.example.taskmanagement.domain.model.Task;
import com.example.taskmanagement.domain.model.TaskPriority;
import com.example.taskmanagement.domain.model.TaskStatus;
import com.example.taskmanagement.domain.model.UpdateTask;
import com.example.taskmanagement.infrastructure.elasticsearch.entity.TaskESDoc;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public class TaskESMapper {

  public static Task toDomain(TaskESDoc taskDoc) {
    return Task.builder()
        .id(taskDoc.getId())
        .title(taskDoc.getTitle())
        .description(taskDoc.getDescription())
        .priority(Optional.ofNullable(taskDoc.getPriority()).map(TaskPriority::valueOf).orElse(null))
        .status(Optional.ofNullable(taskDoc.getStatus()).map(TaskStatus::valueOf).orElse(null))
        .dueDate(taskDoc.getDueDate())
        .createdDate(taskDoc.getCreatedDate())
        .build();
  }

  public static TaskESDoc toDoc(CreateTask createTask) {
    TaskESDoc doc = new TaskESDoc();

    doc.setId(UUID.randomUUID().toString());
    doc.setTitle(createTask.getTitle());
    doc.setDescription(createTask.getDescription());
    doc.setPriority(createTask.getPriority().name());
    doc.setCreatedDate(Instant.now());

    return doc;
  }


  public static TaskESDoc applyUpdates(TaskESDoc original, UpdateTask update) {
    if (update.getTitle() != null) {
      original.setTitle(update.getTitle());
    }

    if (update.getDescription() != null) {
      original.setDescription(update.getDescription());
    }

    if (update.getPriority() != null) {
      original.setPriority(update.getPriority().name());
    }

    if (update.getStatus() != null) {
      original.setStatus(update.getStatus().name());
    }

    if (update.getDueDate() != null) {
      original.setDueDate(update.getDueDate());
    }

    return original;
  }
}
