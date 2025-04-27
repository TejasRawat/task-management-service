package com.example.taskmanagement.adapters.in.rest;

import com.example.taskmanagement.adapters.in.rest.dto.TaskRestUpdateRequest;
import com.example.taskmanagement.domain.model.Task;
import com.example.taskmanagement.domain.model.TaskFilter;
import com.example.taskmanagement.domain.model.TaskPriority;
import com.example.taskmanagement.domain.model.TaskStatus;
import com.example.taskmanagement.application.TaskServiceImpl;
import com.example.taskmanagement.adapters.in.rest.dto.TaskRestCreateRequest;
import com.example.taskmanagement.adapters.in.rest.dto.TaskRestResponse;
import com.example.taskmanagement.adapters.in.rest.mapper.TaskRestMapper;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {

  private final TaskServiceImpl taskServiceImpl;

  @PostMapping
  public ResponseEntity<TaskRestResponse> createTask(@RequestBody TaskRestCreateRequest request) {
    Task task = taskServiceImpl.createTask(TaskRestMapper.toCreateTask(request));
    return ResponseEntity.ok(TaskRestMapper.toTaskResponse(task));
  }

  @GetMapping
  public ResponseEntity<List<TaskRestResponse>> getTasks(
      @RequestParam(required = false) TaskPriority priority,
      @RequestParam(required = false) TaskStatus status,
      @RequestParam(required = false) LocalDate dueDateBefore) {

    TaskFilter filter = TaskFilter.builder()
        .priority(priority)
        .status(status)
        .dueDateBefore(dueDateBefore)
        .build();

    List<Task> tasks = taskServiceImpl.getTasksByFilter(filter);
    List<TaskRestResponse> responses = tasks.stream()
        .map(TaskRestMapper::toTaskResponse)
        .toList();

    return ResponseEntity.ok(responses);
  }

  @PutMapping("/{id}")
  public ResponseEntity<TaskRestResponse> updateTask(
      @PathVariable String id,
      @RequestBody TaskRestUpdateRequest taskRestUpdateRequest) {

    Task task = taskServiceImpl.updateTask(TaskRestMapper.toUpdateTask(id, taskRestUpdateRequest));
    return ResponseEntity.ok(TaskRestMapper.toTaskResponse(task));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteTask(@PathVariable String id) {
    taskServiceImpl.deleteTaskById(id);
    return ResponseEntity.ok().build();
  }
} 