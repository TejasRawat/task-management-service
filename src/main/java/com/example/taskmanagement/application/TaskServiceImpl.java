package com.example.taskmanagement.application;

import com.example.taskmanagement.domain.model.CreateTask;
import com.example.taskmanagement.domain.model.Task;
import com.example.taskmanagement.domain.model.TaskFilter;
import com.example.taskmanagement.domain.model.UpdateTask;
import com.example.taskmanagement.domain.port.in.TaskService;
import com.example.taskmanagement.adapters.out.repository.elasticsearch.repository.TaskESRepository;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

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
