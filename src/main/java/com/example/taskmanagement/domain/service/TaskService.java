package com.example.taskmanagement.domain.service;

import com.example.taskmanagement.domain.model.CreateTask;
import com.example.taskmanagement.domain.model.Task;
import com.example.taskmanagement.domain.model.TaskFilter;
import com.example.taskmanagement.domain.model.UpdateTask;
import com.example.taskmanagement.domain.port.in.ITaskService;
import com.example.taskmanagement.infrastructure.repository.elasticsearch.repository.TaskESRepository;
import com.example.taskmanagement.infrastructure.repository.mysql.adapter.TaskMySQLAdapter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TaskService implements ITaskService {

 // private final TaskMySQLAdapter dbAdapter;

  private final TaskESRepository dbAdapter;

  @Override
  public Task createTask(CreateTask task) {
    return dbAdapter.saveTask(task);
  }

  @Override
  public Boolean deleteTaskById(String id) {
    return dbAdapter.deleteTaskById(id);
  }

  @Override
  public Task updateTask(UpdateTask updateTask) {
    return dbAdapter.updateTask(updateTask);
  }

  @Override
  public List<Task> getTasksByFilter(TaskFilter taskFilter) {
    return dbAdapter.getTasksByFilter(taskFilter);
  }
}
