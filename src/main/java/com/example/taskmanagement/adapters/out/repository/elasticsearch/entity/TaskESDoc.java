package com.example.taskmanagement.adapters.out.repository.elasticsearch.entity;

import com.example.taskmanagement.domain.model.TaskPriority;
import com.example.taskmanagement.domain.model.TaskStatus;
import java.time.Instant;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskESDoc {

  @Id
  private String id;

  private String title;
  private String description;
  private String priority = TaskPriority.NONE.name();
  private String status = TaskStatus.TODO.name();
  private LocalDate dueDate;
  private Instant createdDate = Instant.now();

}
