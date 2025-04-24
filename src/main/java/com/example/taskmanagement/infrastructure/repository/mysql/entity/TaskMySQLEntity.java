package com.example.taskmanagement.infrastructure.repository.mysql.entity;

import com.example.taskmanagement.domain.model.TaskPriority;
import com.example.taskmanagement.domain.model.TaskStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.Instant;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Task")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskMySQLEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(nullable = false)
  private String title;

  @Column(columnDefinition = "TEXT", nullable = false)
  private String description;

  @Column(nullable = false, length = 10)
  private String priority = TaskPriority.NONE.name();

  @Column(nullable = false, length = 15)
  private String status = TaskStatus.TODO.name();

  @Column(name = "due_date")
  private LocalDate dueDate;

  @Column(name = "created_date", nullable = false, updatable = false)
  private Instant createdDate;

  @PrePersist
  protected void onCreate() {
    this.createdDate = Instant.now();
  }

}
