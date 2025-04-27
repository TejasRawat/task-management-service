package com.example.taskmanagement.adapters.in.graphql.mutation;

import com.example.taskmanagement.adapters.in.graphql.mapper.TaskGQLMapper;
import com.example.taskmanagement.codegen.types.CreateTaskDto;
import com.example.taskmanagement.codegen.types.TaskDto;
import com.example.taskmanagement.codegen.types.UpdateTaskDto;
import com.example.taskmanagement.domain.model.Task;
import com.example.taskmanagement.domain.port.in.TaskService;
import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsMutation;
import com.netflix.graphql.dgs.InputArgument;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@DgsComponent
@RequiredArgsConstructor
public class TaskMutation {

  private final TaskService taskService;

  @DgsMutation
  public TaskDto createTask(@InputArgument CreateTaskDto input) {
    Task task = taskService.createTask(TaskGQLMapper.toCreateTask(input));
    return TaskGQLMapper.toTaskDto(task);
  }

  @DgsMutation
  public Boolean deleteTaskById(@InputArgument String id) {
    return taskService.deleteTaskById(id);
  }

  @DgsMutation
  public TaskDto updateTask(@InputArgument UpdateTaskDto input) {
    Task task = taskService.updateTask(TaskGQLMapper.toUpdateTask(input));
    return TaskGQLMapper.toTaskDto(task);
  }

}
