package com.example.taskmanagement.entrypoint.graphql.mutation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.taskmanagement.codegen.DgsConstants;
import com.example.taskmanagement.codegen.client.CreateTaskGraphQLQuery;
import com.example.taskmanagement.codegen.client.CreateTaskProjectionRoot;
import com.example.taskmanagement.codegen.client.CreateTask_PriorityProjection;
import com.example.taskmanagement.codegen.client.DeleteTaskByIdGraphQLQuery;
import com.example.taskmanagement.codegen.client.UpdateTaskGraphQLQuery;
import com.example.taskmanagement.codegen.client.UpdateTaskProjectionRoot;
import com.example.taskmanagement.codegen.client.UpdateTask_PriorityProjection;
import com.example.taskmanagement.codegen.client.UpdateTask_StatusProjection;
import com.example.taskmanagement.codegen.types.CreateTaskDto;
import com.example.taskmanagement.codegen.types.TaskDto;
import com.example.taskmanagement.codegen.types.TaskPriorityDto;
import com.example.taskmanagement.codegen.types.TaskStatusDto;
import com.example.taskmanagement.codegen.types.UpdateTaskDto;
import com.example.taskmanagement.common.AbstractMySQLIntegrationTest;
import com.example.taskmanagement.infrastructure.mysql.entity.TaskMySQLEntity;
import com.netflix.graphql.dgs.DgsQueryExecutor;
import com.netflix.graphql.dgs.client.codegen.GraphQLQueryRequest;
import graphql.ExecutionResult;
import graphql.GraphQLError;
import java.time.LocalDate;
import java.util.Map;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;

@Disabled("Temporarily disabled to test ES tests")
class TaskMySQLMutationTest extends AbstractMySQLIntegrationTest {

  @Autowired
  private DgsQueryExecutor dgsQueryExecutor;

  private static final String CREATE_MUTATION_JSON_PATH = "data." + DgsConstants.MUTATION.CreateTask;
  private static final String DELETE_MUTATION_JSON_PATH = "data." + DgsConstants.MUTATION.DeleteTaskById;
  private static final String UPDATE_MUTATION_JSON_PATH = "data." + DgsConstants.MUTATION.UpdateTask;

  @Test
  void testCreateTask_Success() {
    CreateTaskDto task = CreateTaskDto.newBuilder()
        .description("desc")
        .title("title")
        .priority(TaskPriorityDto.NONE)
        .build();

    buildCreateTask("title", "desc", TaskPriorityDto.NONE, null);

    CreateTaskGraphQLQuery createTaskGraphQLQuery = CreateTaskGraphQLQuery.newRequest()
        .input(task)
        .build();

    CreateTask_PriorityProjection projection = new CreateTaskProjectionRoot()
        .id()
        .createDate()
        .description()
        .title()
        .priority();

    TaskDto taskDto =
        dgsQueryExecutor.executeAndExtractJsonPathAsObject(
            new GraphQLQueryRequest(createTaskGraphQLQuery, projection).serialize(),
            CREATE_MUTATION_JSON_PATH,
            Map.of(),
            TaskDto.class,
            new HttpHeaders());

    assertNotNull(taskDto);
    assertEquals("title", taskDto.getTitle());
    assertEquals("desc", taskDto.getDescription());
    assertEquals(TaskPriorityDto.NONE, taskDto.getPriority());
    assertNotNull(taskDto.getCreateDate());
  }

  private CreateTaskDto buildCreateTask(String title, String desc, TaskPriorityDto priority, LocalDate dueDate) {
    return CreateTaskDto.newBuilder()
        .description(desc)
        .title(title)
        .priority(priority)
        .dueDate(dueDate)
        .build();
  }

  @Test
  void testDeleteTask_Success() {
    TaskMySQLEntity taskEntity = TaskMySQLEntity.builder()
        .title("title")
        .description("desc")
        .priority(TaskPriorityDto.NONE.name())
        .status(TaskStatusDto.CREATED.name())
        .build();
    TaskMySQLEntity savedEntity = testMySQLTaskRepository.save(taskEntity);

    DeleteTaskByIdGraphQLQuery deleteTaskGraphQLQuery = DeleteTaskByIdGraphQLQuery.newRequest()
        .id(String.valueOf(savedEntity.getId()))
        .build();

    GraphQLQueryRequest graphQLQueryRequest = new GraphQLQueryRequest(deleteTaskGraphQLQuery);

    Boolean isDeleted = dgsQueryExecutor.executeAndExtractJsonPathAsObject(graphQLQueryRequest.serialize(),
        DELETE_MUTATION_JSON_PATH,
        Boolean.class);

    assertTrue(isDeleted);
  }

  @Test
  void testUpdateTask_Failure_TaskNotFound() {
    TaskMySQLEntity taskEntity = TaskMySQLEntity.builder()
        .title("title")
        .description("desc")
        .priority(TaskPriorityDto.NONE.name())
        .status(TaskStatusDto.CREATED.name())
        .build();

    testMySQLTaskRepository.save(taskEntity);

    UpdateTaskDto updateTaskDto = UpdateTaskDto
        .newBuilder()
        .id(String.valueOf(100))
        .title("newTitle")
        .description("newDescription")
        .priority(TaskPriorityDto.LOW)
        .status(TaskStatusDto.TODO)
        .build();

    UpdateTaskGraphQLQuery updateTaskGraphQLQuery = UpdateTaskGraphQLQuery
        .newRequest().input(updateTaskDto).build();

    UpdateTask_PriorityProjection updateTaskProjection = new UpdateTaskProjectionRoot()
        .id()
        .createDate()
        .description()
        .title()
        .priority();

    ExecutionResult execute =
        dgsQueryExecutor.execute(new GraphQLQueryRequest(updateTaskGraphQLQuery, updateTaskProjection).serialize());

    assertNotNull(execute.getErrors());
    GraphQLError graphQLError = execute.getErrors().get(0);
    assertTrue(graphQLError.getMessage().contains("Missing Task ID"));
  }

  @Test
  void testUpdateTask_Success() {
    TaskMySQLEntity taskEntity = TaskMySQLEntity.builder()
        .title("title")
        .description("desc")
        .priority(TaskPriorityDto.NONE.name())
        .status(TaskStatusDto.CREATED.name())
        .build();
    TaskMySQLEntity savedEntity = testMySQLTaskRepository.save(taskEntity);

    UpdateTaskDto updateTaskDto = UpdateTaskDto
        .newBuilder()
        .id(String.valueOf(savedEntity.getId()))
        .title("newTitle")
        .description("newDescription")
        .priority(TaskPriorityDto.LOW)
        .status(TaskStatusDto.TODO)
        .build();

    UpdateTaskGraphQLQuery updateTaskGraphQLQuery = UpdateTaskGraphQLQuery
        .newRequest().input(updateTaskDto).build();

    UpdateTask_StatusProjection updateTaskProjection = new UpdateTaskProjectionRoot()
        .id()
        .createDate()
        .description()
        .title()
        .priority()
        .getParent()
        .status();

    TaskDto taskDto =
        dgsQueryExecutor.executeAndExtractJsonPathAsObject(
            new GraphQLQueryRequest(updateTaskGraphQLQuery, updateTaskProjection).serialize(),
            UPDATE_MUTATION_JSON_PATH,
            Map.of(),
            TaskDto.class,
            new HttpHeaders());

    assertNotNull(taskDto);
    assertEquals(savedEntity.getId(), taskDto.getId());
    assertEquals("newTitle", taskDto.getTitle());
    assertEquals("newDescription", taskDto.getDescription());
    assertEquals(TaskPriorityDto.LOW, taskDto.getPriority());
    assertEquals(TaskStatusDto.TODO, taskDto.getStatus());
  }

}
