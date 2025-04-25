package com.example.taskmanagement.entrypoint.graphql.query;

import static com.example.taskmanagement.codegen.types.TaskPriorityDto.LOW;

import com.example.taskmanagement.codegen.client.GetTasksByFilterGraphQLQuery;
import com.example.taskmanagement.codegen.client.GetTasksByFilterProjectionRoot;
import com.example.taskmanagement.codegen.types.TaskDto;
import com.example.taskmanagement.codegen.types.TaskFilterInput;
import com.example.taskmanagement.codegen.types.TaskPriorityDto;
import com.example.taskmanagement.codegen.types.TaskStatusDto;
import com.example.taskmanagement.common.AbstractElasticsearchIntegrationTest;
import com.example.taskmanagement.domain.model.TaskPriority;
import com.example.taskmanagement.domain.model.TaskStatus;
import com.example.taskmanagement.infrastructure.elasticsearch.entity.TaskESDoc;
import com.jayway.jsonpath.TypeRef;
import com.netflix.graphql.dgs.DgsQueryExecutor;
import com.netflix.graphql.dgs.client.codegen.GraphQLQueryRequest;
import java.util.LinkedList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
public class TaskESDataFetcherTest extends AbstractElasticsearchIntegrationTest {

  @Autowired
  private DgsQueryExecutor dgsQueryExecutor;

  @Test
  public void testGetTaskByFilter_Success() {
    List<TaskESDoc> docs = new LinkedList<>();
    for (int i = 1; i <= 10; i++) {
      TaskESDoc doc = new TaskESDoc();
      doc.setDescription("description" + i);
      doc.setTitle("title" + i);
      doc.setId(String.valueOf(i));
      if (i % 2 == 0) {
        doc.setStatus(TaskStatus.CREATED.name());
        doc.setPriority(TaskPriority.NONE.name());
      } else {
        doc.setStatus(TaskStatus.TODO.name());
        doc.setPriority(TaskPriority.LOW.name());
      }
      docs.add(doc);
    }

    saveTasks(docs);

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
