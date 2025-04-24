package com.example.taskmanagement.domain.model;


import java.time.LocalDate;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class CreateTask {

  private String title;
  private String description;
  private TaskPriority priority;
  private LocalDate dueDate;

}
