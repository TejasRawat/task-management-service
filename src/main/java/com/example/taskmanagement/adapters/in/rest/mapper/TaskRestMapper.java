package com.example.taskmanagement.adapters.in.rest.mapper;

import com.example.taskmanagement.adapters.in.rest.dto.TaskRestCreateRequest;
import com.example.taskmanagement.adapters.in.rest.dto.TaskRestResponse;
import com.example.taskmanagement.adapters.in.rest.dto.TaskRestUpdateRequest;
import com.example.taskmanagement.domain.model.CreateTask;
import com.example.taskmanagement.domain.model.Task;
import com.example.taskmanagement.domain.model.UpdateTask;

public class TaskRestMapper {

    public static CreateTask toCreateTask(TaskRestCreateRequest request) {
        return CreateTask.builder()
            .title(request.getTitle())
            .description(request.getDescription())
            .priority(request.getPriority())
            .dueDate(request.getDueDate())
            .build();
    }

    public static UpdateTask toUpdateTask(String id, TaskRestUpdateRequest request) {
        return UpdateTask.builder()
            .id(id)
            .title(request.getTitle())
            .description(request.getDescription())
            .priority(request.getPriority())
            .status(request.getStatus())
            .dueDate(request.getDueDate())
            .build();
    }

    public static TaskRestResponse toTaskResponse(Task task) {
        return TaskRestResponse.builder()
            .id(task.getId())
            .title(task.getTitle())
            .description(task.getDescription())
            .priority(task.getPriority())
            .status(task.getStatus())
            .dueDate(task.getDueDate())
            .createDate(task.getCreatedDate())
            .build();
    }
} 