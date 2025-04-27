package com.example.taskmanagement.adapters.in.graphql.query;


import com.example.taskmanagement.adapters.in.graphql.mapper.TaskGQLMapper;
import com.example.taskmanagement.codegen.types.TaskDto;
import com.example.taskmanagement.codegen.types.TaskFilterInput;
import com.example.taskmanagement.domain.model.Task;
import com.example.taskmanagement.domain.port.in.TaskService;
import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsQuery;
import com.netflix.graphql.dgs.InputArgument;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@DgsComponent
@RequiredArgsConstructor
public class TaskDataFetcher {

  private final TaskService taskService;

  @DgsQuery
  public List<TaskDto> getTasksByFilter(@InputArgument TaskFilterInput filter) {
    List<Task> tasks = taskService.getTasksByFilter(TaskGQLMapper.toTaskFilter(filter));
    return tasks.stream().map(task -> TaskGQLMapper.toTaskDto(task)).toList();
  }

}
