package com.example.taskmanagement.adapters.out.repository.mysql.adapter;

import com.example.taskmanagement.adapters.out.repository.mysql.entity.TaskMySQLEntity;
import com.example.taskmanagement.adapters.out.repository.mysql.mapper.TaskMySQLMapper;
import com.example.taskmanagement.adapters.out.repository.mysql.repository.TaskMySQLRepository;
import com.example.taskmanagement.adapters.out.repository.mysql.specification.TaskSpecifications;
import com.example.taskmanagement.domain.exception.TaskNotFoundException;
import com.example.taskmanagement.domain.model.CreateTask;
import com.example.taskmanagement.domain.model.Task;
import com.example.taskmanagement.domain.model.TaskFilter;
import com.example.taskmanagement.domain.model.UpdateTask;
import com.example.taskmanagement.domain.port.out.TaskDBPort;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TaskMySQLAdapter implements TaskDBPort {

  private final TaskMySQLRepository taskMySQLRepository;

  @Override
  public Task saveTask(CreateTask createTask) {
    TaskMySQLEntity entity = TaskMySQLMapper.toEntity(createTask);
    TaskMySQLEntity taskMySQLEntity = taskMySQLRepository.save(entity);
    return TaskMySQLMapper.toDomain(taskMySQLEntity);
  }

  @Override
  public Boolean deleteTaskById(String id) {
    taskMySQLRepository.deleteById(Integer.valueOf(id));
    return true;
  }

  @Override
  public Task updateTask(UpdateTask updateTask) {
    TaskMySQLEntity task =
        taskMySQLRepository.findById(Integer.valueOf(updateTask.getId()))
            .orElseThrow(() -> new TaskNotFoundException("Missing Task ID"));

    if (updateTask.getTitle() != null) {
      task.setTitle(updateTask.getTitle());
    }
    if (updateTask.getDescription() != null) {
      task.setDescription(updateTask.getDescription());
    }
    if (updateTask.getPriority() != null) {
      task.setPriority(updateTask.getPriority().name());
    }
    if (updateTask.getStatus() != null) {
      task.setStatus(updateTask.getStatus().name());
    }
    if (updateTask.getDueDate() != null) {
      task.setDueDate(updateTask.getDueDate());
    }

    return TaskMySQLMapper.toDomain(taskMySQLRepository.save(task));
  }

  @Override
  public List<Task> getTasksByFilter(TaskFilter taskFilter) {
    Specification<TaskMySQLEntity> spec = Specification.where(null);

    if (taskFilter.getStatus() != null) {
      spec = spec.and(TaskSpecifications.hasStatus(taskFilter.getStatus()));
    }
    if (taskFilter.getPriority() != null) {
      spec = spec.and(TaskSpecifications.hasPriority(taskFilter.getPriority()));
    }
    if (taskFilter.getDueDateBefore() != null) {
      spec = spec.and(TaskSpecifications.dueDateBefore(taskFilter.getDueDateBefore()));
    }

    List<TaskMySQLEntity> tasks = taskMySQLRepository.findAll(spec);

    return tasks.stream()
        .map(TaskMySQLMapper::toDomain)
        .toList();
  }
}
