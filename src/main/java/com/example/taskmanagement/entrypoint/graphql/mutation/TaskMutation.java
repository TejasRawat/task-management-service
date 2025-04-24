package com.example.taskmanagement.entrypoint.graphql.mutation;

import static com.example.taskmanagement.entrypoint.graphql.mapper.TaskGQLMapper.toCreateTask;
import static com.example.taskmanagement.entrypoint.graphql.mapper.TaskGQLMapper.toTaskDto;
import static com.example.taskmanagement.entrypoint.graphql.mapper.TaskGQLMapper.toUpdateTask;

import com.example.taskmanagement.codegen.types.CreateTaskDto;
import com.example.taskmanagement.codegen.types.TaskDto;
import com.example.taskmanagement.codegen.types.UpdateTaskDto;
import com.example.taskmanagement.domain.model.Task;
import com.example.taskmanagement.domain.port.in.ITaskService;
import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsMutation;
import com.netflix.graphql.dgs.InputArgument;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@DgsComponent
@RequiredArgsConstructor
public class TaskMutation {

  private final ITaskService taskService;

  @DgsMutation
  public TaskDto createTask(@InputArgument CreateTaskDto input) {
    Task task = taskService.createTask(toCreateTask(input));
    return toTaskDto(task);
  }

  @DgsMutation
  public Boolean deleteTaskById(@InputArgument String id) {
    return taskService.deleteTaskById(id);
  }

  @DgsMutation
  public TaskDto updateTask(@InputArgument UpdateTaskDto input) {
    Task task = taskService.updateTask(toUpdateTask(input));
    return toTaskDto(task);
  }

}
