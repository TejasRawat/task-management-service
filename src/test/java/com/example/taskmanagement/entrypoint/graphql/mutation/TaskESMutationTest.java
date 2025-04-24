package com.example.taskmanagement.entrypoint.graphql.mutation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import co.elastic.clients.elasticsearch.core.mget.MultiGetResponseItem;
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
import com.example.taskmanagement.common.AbstractElasticsearchIntegrationTest;
import com.example.taskmanagement.infrastructure.repository.elasticsearch.constants.ElasticSearchConstants;
import com.example.taskmanagement.infrastructure.repository.elasticsearch.entity.TaskESDoc;
import com.netflix.graphql.dgs.DgsQueryExecutor;
import com.netflix.graphql.dgs.client.codegen.GraphQLQueryRequest;
import graphql.ExecutionResult;
import graphql.GraphQLError;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;

class TaskESMutationTest extends AbstractElasticsearchIntegrationTest {

  @Autowired
  private DgsQueryExecutor dgsQueryExecutor;

  private static final String CREATE_MUTATION_JSON_PATH = "data." + DgsConstants.MUTATION.CreateTask;
  private static final String DELETE_MUTATION_JSON_PATH = "data." + DgsConstants.MUTATION.DeleteTaskById;
  private static final String UPDATE_MUTATION_JSON_PATH = "data." + DgsConstants.MUTATION.UpdateTask;

  @Test
  void testCreateTask_Success() throws IOException {
    CreateTaskDto createTaskDto = CreateTaskDto.newBuilder()
        .description("desc")
        .title("title")
        .priority(TaskPriorityDto.NONE)
        .build();

    buildCreateTask("title", "desc", TaskPriorityDto.NONE, null);

    CreateTaskGraphQLQuery createTaskGraphQLQuery = CreateTaskGraphQLQuery.newRequest()
        .input(createTaskDto)
        .build();

    CreateTask_PriorityProjection projection = new CreateTaskProjectionRoot()
        .id()
        .createDate()
        .description()
        .title()
        .status().getParent()
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

    List<MultiGetResponseItem<TaskESDoc>> multiGetResponseItems =
        esClient.mget(builder ->
            builder.index(ElasticSearchConstants.ELASTICSEARCH_INDEX).ids(taskDto.getId()), TaskESDoc.class).docs();

    assertNotNull(multiGetResponseItems);
    assertEquals(1, multiGetResponseItems.size());

    TaskESDoc doc = multiGetResponseItems.get(0).result().source();

    assertEquals(createTaskDto.getTitle(), doc.getTitle());
    assertEquals(createTaskDto.getDescription(), doc.getDescription());
    assertEquals(createTaskDto.getPriority().name(), doc.getPriority());
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
    TaskESDoc taskESDoc = new TaskESDoc();
    taskESDoc.setId(UUID.randomUUID().toString());
    taskESDoc.setTitle("tittle");
    taskESDoc.setDescription("description");
    taskESDoc.setCreatedDate(Instant.now());

    saveTasks(List.of(taskESDoc));

    DeleteTaskByIdGraphQLQuery deleteTaskGraphQLQuery = DeleteTaskByIdGraphQLQuery.newRequest()
        .id(String.valueOf(taskESDoc.getId()))
        .build();

    GraphQLQueryRequest graphQLQueryRequest = new GraphQLQueryRequest(deleteTaskGraphQLQuery);

    Boolean isDeleted = dgsQueryExecutor.executeAndExtractJsonPathAsObject(graphQLQueryRequest.serialize(),
        DELETE_MUTATION_JSON_PATH,
        Boolean.class);

    assertTrue(isDeleted);
  }

  @Test
  void testUpdateTask_Failure_TaskNotFound() {
    TaskESDoc taskESDoc = new TaskESDoc();
    taskESDoc.setId(UUID.randomUUID().toString());
    taskESDoc.setTitle("tittle");
    taskESDoc.setDescription("description");
    taskESDoc.setCreatedDate(Instant.now());

    saveTasks(List.of(taskESDoc));

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
    TaskESDoc taskESDoc = new TaskESDoc();
    taskESDoc.setId(UUID.randomUUID().toString());
    taskESDoc.setTitle("tittle");
    taskESDoc.setDescription("description");
    taskESDoc.setCreatedDate(Instant.now());

    saveTasks(List.of(taskESDoc));

    UpdateTaskDto updateTaskDto = UpdateTaskDto
        .newBuilder()
        .id(String.valueOf(taskESDoc.getId()))
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
    assertEquals(taskESDoc.getId(), taskDto.getId());
    assertEquals("newTitle", taskDto.getTitle());
    assertEquals("newDescription", taskDto.getDescription());
    assertEquals(TaskPriorityDto.LOW, taskDto.getPriority());
    assertEquals(TaskStatusDto.TODO, taskDto.getStatus());
  }

}
