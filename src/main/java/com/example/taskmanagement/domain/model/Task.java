package com.example.taskmanagement.domain.model;


import java.time.Instant;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Task {

  private final String id;
  private final String title;
  private final String description;
  private final TaskPriority priority;
  private final TaskStatus status;
  private final LocalDate dueDate;
  private final Instant createdDate;

}
