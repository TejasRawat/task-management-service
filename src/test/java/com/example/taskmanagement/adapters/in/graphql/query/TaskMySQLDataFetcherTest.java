package com.example.taskmanagement.adapters.in.graphql.query;

import static com.example.taskmanagement.codegen.types.TaskPriorityDto.LOW;

import com.example.taskmanagement.codegen.client.GetTasksByFilterGraphQLQuery;
import com.example.taskmanagement.codegen.client.GetTasksByFilterProjectionRoot;
import com.example.taskmanagement.codegen.types.TaskDto;
import com.example.taskmanagement.codegen.types.TaskFilterInput;
import com.example.taskmanagement.codegen.types.TaskPriorityDto;
import com.example.taskmanagement.codegen.types.TaskStatusDto;
import com.example.taskmanagement.common.AbstractMySQLIntegrationTest;
import com.example.taskmanagement.common.TestMySQLTaskRepository;
import com.example.taskmanagement.domain.model.TaskPriority;
import com.example.taskmanagement.domain.model.TaskStatus;
import com.example.taskmanagement.adapters.out.repository.mysql.entity.TaskMySQLEntity;
import com.jayway.jsonpath.TypeRef;
import com.netflix.graphql.dgs.DgsQueryExecutor;
import com.netflix.graphql.dgs.client.codegen.GraphQLQueryRequest;
import java.util.LinkedList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@Disabled("Temporarily disabled to test ES tests")
public class TaskMySQLDataFetcherTest extends AbstractMySQLIntegrationTest {

  @Autowired
  private DgsQueryExecutor dgsQueryExecutor;

  @Autowired
  private TestMySQLTaskRepository testMySQLTaskRepository;

  @Test
  public void testGetTaskByFilter_Success() {
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

    GetTasksByFilterGraphQLQuery query = GetTasksByFilterGraphQLQuery.newRequest()
        .filter(TaskFilterInput.newBuilder()
            .priority(LOW)
            .build())
        .build();

    GraphQLQueryRequest graphQLQueryRequest = new GraphQLQueryRequest(
        query,
        new GetTasksByFilterProjectionRoot()
            .id()
            .createDate()
            .title()
            .description()
            .priority()
            .getParent()
            .status()
    );

    List<TaskDto> taskDtos =
        dgsQueryExecutor.executeAndExtractJsonPathAsObject(graphQLQueryRequest.serialize(), "data.getTasksByFilter",
            new TypeRef<List<TaskDto>>() {
            });

    Assertions.assertNotNull(taskDtos);
    Assertions.assertEquals(5, taskDtos.size());
    taskDtos.forEach(
        taskDto -> {
          Assertions.assertEquals(TaskPriorityDto.LOW, taskDto.getPriority());
          Assertions.assertEquals(TaskStatusDto.TODO, taskDto.getStatus());
        }
    );
  }
}
