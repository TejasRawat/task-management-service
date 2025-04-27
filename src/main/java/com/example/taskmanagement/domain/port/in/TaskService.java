package com.example.taskmanagement.domain.port.in;

import com.example.taskmanagement.domain.model.CreateTask;
import com.example.taskmanagement.domain.model.Task;
import com.example.taskmanagement.domain.model.TaskFilter;
import com.example.taskmanagement.domain.model.UpdateTask;
import java.util.List;

public interface TaskService {

  Task createTask(CreateTask createTask);

  Boolean deleteTaskById(String id);

  Task updateTask(UpdateTask updateTask);

  List<Task> getTasksByFilter(TaskFilter taskFilter);
}

