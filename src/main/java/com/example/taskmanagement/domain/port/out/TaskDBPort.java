package com.example.taskmanagement.domain.port.out;

import com.example.taskmanagement.domain.model.CreateTask;
import com.example.taskmanagement.domain.model.Task;
import com.example.taskmanagement.domain.model.TaskFilter;
import com.example.taskmanagement.domain.model.UpdateTask;
import java.util.List;

public interface TaskDBPort {

  Task saveTask(CreateTask task);

  Boolean deleteTaskById(String id);

  Task updateTask(UpdateTask updateTask);

  List<Task> getTasksByFilter(TaskFilter taskFilter);
}
