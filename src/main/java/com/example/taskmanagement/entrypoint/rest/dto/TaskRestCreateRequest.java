package com.example.taskmanagement.entrypoint.rest.dto;

import com.example.taskmanagement.domain.model.TaskPriority;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TaskRestCreateRequest {
    private String title;
    private String description;
    private TaskPriority priority;
    private LocalDate dueDate;
} 