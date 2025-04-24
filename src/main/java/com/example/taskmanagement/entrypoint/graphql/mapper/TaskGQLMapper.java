package com.example.taskmanagement.entrypoint.graphql.mapper;

import com.example.taskmanagement.codegen.types.CreateTaskDto;
import com.example.taskmanagement.codegen.types.TaskDto;
import com.example.taskmanagement.codegen.types.TaskFilterInput;
import com.example.taskmanagement.codegen.types.TaskPriorityDto;
import com.example.taskmanagement.codegen.types.TaskStatusDto;
import com.example.taskmanagement.codegen.types.UpdateTaskDto;
import com.example.taskmanagement.domain.model.CreateTask;
import com.example.taskmanagement.domain.model.Task;
import com.example.taskmanagement.domain.model.TaskFilter;
import com.example.taskmanagement.domain.model.TaskPriority;
import com.example.taskmanagement.domain.model.TaskStatus;
import com.example.taskmanagement.domain.model.UpdateTask;
import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;

public class TaskGQLMapper {

  private static final Map<TaskPriorityDto, TaskPriority> TASK_PRIORITY_MAP_BY_TASK_PRIORITY_DTO = new EnumMap<>(Map.of(
      TaskPriorityDto.NONE, TaskPriority.NONE,
      TaskPriorityDto.LOW, TaskPriority.LOW,
      TaskPriorityDto.MEDIUM, TaskPriority.MEDIUM,
      TaskPriorityDto.HIGH, TaskPriority.HIGH
  ));

  private static final Map<TaskPriority, TaskPriorityDto> TASK_PRIORITY_DTO_MAP_BY_TASK_PRIORITY = new EnumMap<>(Map.of(
      TaskPriority.NONE, TaskPriorityDto.NONE,
      TaskPriority.LOW, TaskPriorityDto.LOW,
      TaskPriority.MEDIUM, TaskPriorityDto.MEDIUM,
      TaskPriority.HIGH, TaskPriorityDto.HIGH
  ));

  private static final Map<TaskStatusDto, TaskStatus> TASK_STATUS_MAP_BY_TASK_STATUS_DTO = new EnumMap<>(Map.of(
      TaskStatusDto.CREATED, TaskStatus.CREATED,
      TaskStatusDto.TODO, TaskStatus.TODO,
      TaskStatusDto.IN_PROGRESS, TaskStatus.IN_PROGRESS,
      TaskStatusDto.COMPLETED, TaskStatus.COMPLETED
  ));

  private static final Map<TaskStatus, TaskStatusDto> TASK_STATUS_DTO_MAP_BY_TASK_STATUS = new EnumMap<>(Map.of(
      TaskStatus.CREATED, TaskStatusDto.CREATED,
      TaskStatus.TODO, TaskStatusDto.TODO,
      TaskStatus.IN_PROGRESS, TaskStatusDto.IN_PROGRESS,
      TaskStatus.COMPLETED, TaskStatusDto.COMPLETED
  ));

  public static CreateTask toCreateTask(CreateTaskDto createTaskDto) {
    return CreateTask.builder()
        .title(createTaskDto.getTitle())
        .description(createTaskDto.getDescription())
        .priority(Optional.ofNullable(TASK_PRIORITY_MAP_BY_TASK_PRIORITY_DTO.get(createTaskDto.getPriority()))
            .orElse(TaskPriority.NONE))
        .dueDate(createTaskDto.getDueDate())
        .build();
  }

  public static TaskDto toTaskDto(Task task) {
    return TaskDto.newBuilder()
        .id(task.getId())
        .title(task.getTitle())
        .description(task.getDescription())
        .priority(TASK_PRIORITY_DTO_MAP_BY_TASK_PRIORITY.get(task.getPriority()))
        .status(TASK_STATUS_DTO_MAP_BY_TASK_STATUS.get(task.getStatus()))
        .dueDate(task.getDueDate())
        .createDate(task.getCreatedDate())
        .build();
  }

  public static UpdateTask toUpdateTask(UpdateTaskDto updateTaskDto) {
    return UpdateTask
        .builder()
        .id(updateTaskDto.getId())
        .title(updateTaskDto.getTitle())
        .description(updateTaskDto.getDescription())
        .priority(TASK_PRIORITY_MAP_BY_TASK_PRIORITY_DTO.get(updateTaskDto.getPriority()))
        .status(TASK_STATUS_MAP_BY_TASK_STATUS_DTO.get(updateTaskDto.getStatus()))
        .dueDate(updateTaskDto.getDueDate())
        .build();
  }

  public static TaskFilter toTaskFilter(TaskFilterInput filter) {
    if (filter == null) {
      return TaskFilter.builder().build();
    }
    
    return TaskFilter.builder()
        .priority(TASK_PRIORITY_MAP_BY_TASK_PRIORITY_DTO.get(filter.getPriority()))
        .status(TASK_STATUS_MAP_BY_TASK_STATUS_DTO.get(filter.getStatus()))
        .dueDateBefore(filter.getDueDateBefore())
        .build();
  }
}