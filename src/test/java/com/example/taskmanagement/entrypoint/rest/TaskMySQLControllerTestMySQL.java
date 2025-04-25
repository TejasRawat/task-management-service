package com.example.taskmanagement.entrypoint.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.taskmanagement.common.AbstractMySQLIntegrationTest;
import com.example.taskmanagement.common.TestMySQLTaskRepository;
import com.example.taskmanagement.domain.model.TaskPriority;
import com.example.taskmanagement.domain.model.TaskStatus;
import com.example.taskmanagement.entrypoint.rest.dto.TaskRestCreateRequest;
import com.example.taskmanagement.entrypoint.rest.dto.TaskRestResponse;
import com.example.taskmanagement.entrypoint.rest.dto.TaskRestUpdateRequest;
import com.example.taskmanagement.infrastructure.mysql.entity.TaskMySQLEntity;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@Disabled("Temporarily disabled to test ES tests")
@AutoConfigureMockMvc
@SpringBootTest
class TaskMySQLControllerTestMySQL extends AbstractMySQLIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private TestMySQLTaskRepository testMySQLTaskRepository;

  @Test
  @SneakyThrows
  void getTasksByFilters_Success() {
    List<TaskMySQLEntity> taskEntityList = new LinkedList<>();
    for (int i = 1; i <= 10; i++) {
      TaskMySQLEntity taskEntity = new TaskMySQLEntity();
      taskEntity.setDescription("description" + i);
      taskEntity.setTitle("title" + i);
      if (i % 2 == 0) {
        taskEntity.setStatus(TaskStatus.CREATED.name());
        taskEntity.setPriority(TaskPriority.NONE.name());
      } else {
        taskEntity.setStatus(TaskStatus.TODO.name());
        taskEntity.setPriority(TaskPriority.LOW.name());
      }
      taskEntityList.add(taskEntity);
    }
    testMySQLTaskRepository.saveAll(taskEntityList);

    MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/tasks")
            .param("priority", TaskPriority.LOW.name()))
        .andExpect(status().isOk())
        .andReturn();

    String contentAsString = mvcResult.getResponse().getContentAsString();

    List<TaskRestResponse> taskDtos = objectMapper.readValue(contentAsString, new TypeReference<>() {
    });

    Assertions.assertNotNull(taskDtos);
    Assertions.assertEquals(5, taskDtos.size());
    taskDtos.forEach(
        taskDto -> {
          Assertions.assertEquals(TaskPriority.LOW, taskDto.getPriority());
          Assertions.assertEquals(TaskStatus.TODO, taskDto.getStatus());
        }
    );

  }

  @Test
  void createTask_Success() throws Exception {
    TaskRestCreateRequest request = TaskRestCreateRequest.builder()
        .title("title")
        .description("description")
        .priority(TaskPriority.NONE)
        .build();

    MvcResult result = mockMvc.perform(post("/tasks")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andReturn();

    String responseBody = result.getResponse().getContentAsString();
    TaskRestResponse response = objectMapper.readValue(responseBody, TaskRestResponse.class);

    assertNotNull(response);
    assertEquals("title", response.getTitle());
    assertEquals("description", response.getDescription());
    assertEquals(TaskPriority.NONE, response.getPriority());
    assertNotNull(response.getCreateDate());
  }

  @Test
  @SneakyThrows
  void updateTask_Success() {
    TaskMySQLEntity taskEntity = new TaskMySQLEntity();
    taskEntity.setTitle("tittle");
    taskEntity.setDescription("description");
    TaskMySQLEntity savedTaskEntity = testMySQLTaskRepository.save(taskEntity);

    TaskRestUpdateRequest taskRestUpdateRequest = TaskRestUpdateRequest.builder()
        .description("desc")
        .priority(TaskPriority.LOW)
        .build();

    MvcResult result = mockMvc.perform(MockMvcRequestBuilders.put("/tasks/" + savedTaskEntity.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(taskRestUpdateRequest)))
        .andExpect(status().isOk())
        .andReturn();

    String responseBody = result.getResponse().getContentAsString();
    TaskRestResponse response = objectMapper.readValue(responseBody, TaskRestResponse.class);

    assertEquals("desc", response.getDescription());
    assertEquals(TaskPriority.LOW, response.getPriority());
  }

  @Test
  @SneakyThrows
  void deleteTask_Success() {
    TaskMySQLEntity taskEntity = new TaskMySQLEntity();
    taskEntity.setTitle("tittle");
    taskEntity.setDescription("description");
    TaskMySQLEntity savedTaskEntity = testMySQLTaskRepository.save(taskEntity);

    mockMvc.perform(MockMvcRequestBuilders.delete("/tasks/" + savedTaskEntity.getId()))
        .andExpect(status().isOk());

    assertEquals(Optional.empty(), testMySQLTaskRepository.findById(Long.valueOf(savedTaskEntity.getId())));
  }

} 