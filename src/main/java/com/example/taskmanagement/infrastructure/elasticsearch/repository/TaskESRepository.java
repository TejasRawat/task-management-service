package com.example.taskmanagement.infrastructure.elasticsearch.repository;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.Result;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.DeleteResponse;
import co.elastic.clients.elasticsearch.core.MgetResponse;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.mget.MultiGetResponseItem;
import co.elastic.clients.util.ObjectBuilder;
import com.example.taskmanagement.domain.exception.TaskNotFoundException;
import com.example.taskmanagement.domain.model.CreateTask;
import com.example.taskmanagement.domain.model.Task;
import com.example.taskmanagement.domain.model.TaskFilter;
import com.example.taskmanagement.domain.model.TaskPriority;
import com.example.taskmanagement.domain.model.TaskStatus;
import com.example.taskmanagement.domain.model.UpdateTask;
import com.example.taskmanagement.domain.port.out.TaskDBPort;
import com.example.taskmanagement.infrastructure.elasticsearch.constants.ElasticSearchConstants;
import com.example.taskmanagement.infrastructure.elasticsearch.constants.SchemaConstants;
import com.example.taskmanagement.infrastructure.elasticsearch.entity.TaskESDoc;
import com.example.taskmanagement.infrastructure.elasticsearch.mapper.TaskESMapper;
import java.util.List;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class TaskESRepository implements TaskDBPort {

  private final ElasticsearchClient esClient;

  @Override
  @SneakyThrows
  public Task saveTask(CreateTask task) {
    TaskESDoc doc = TaskESMapper.toDoc(task);

    esClient.index(builder ->
        builder.id(doc.getId())
            .index(ElasticSearchConstants.ELASTICSEARCH_INDEX)
            .document(doc)
    );

    return TaskESMapper.toDomain(doc);
  }

  @Override
  @SneakyThrows
  public Boolean deleteTaskById(String id) {
    DeleteResponse delete = esClient.delete(d -> d
        .index(ElasticSearchConstants.ELASTICSEARCH_INDEX)
        .id(id)
    );
    return delete.result() == Result.Deleted;
  }

  @Override
  @SneakyThrows
  public Task updateTask(UpdateTask updateTask) {
    MgetResponse<TaskESDoc> mgetResponse = esClient.mget(m -> m
            .index(ElasticSearchConstants.ELASTICSEARCH_INDEX)
            .ids(updateTask.getId()),
        TaskESDoc.class
    );

    MultiGetResponseItem<TaskESDoc> hit = mgetResponse.docs().get(0);
    if (!hit.result().found()) {
      throw new TaskNotFoundException("Missing Task ID");
    }

    TaskESDoc existingDoc = hit.result().source();
    TaskESDoc updatedDoc = TaskESMapper.applyUpdates(existingDoc, updateTask);
    esClient.index(i -> i
        .index(ElasticSearchConstants.ELASTICSEARCH_INDEX)
        .id(updatedDoc.getId())
        .document(updatedDoc)
    );

    return TaskESMapper.toDomain(updatedDoc);
  }

  @Override
  @SneakyThrows
  public List<Task> getTasksByFilter(TaskFilter taskFilter) {
    SearchRequest searchRequest = new SearchRequest.Builder()
        .index(ElasticSearchConstants.ELASTICSEARCH_INDEX)
        .query(buildQuery(taskFilter))
        .build();
    SearchResponse<TaskESDoc> searchResponse = esClient.search(searchRequest, TaskESDoc.class);
    return searchResponse.hits().hits().stream()
        .map(doc -> TaskESMapper.toDomain(doc.source())).toList();
  }

  private Function<Query.Builder, ObjectBuilder<Query>> buildQuery(TaskFilter taskFilter) {
    return qb ->
        qb.bool(
            builder -> {
              addPriorityFilter(builder, taskFilter.getPriority());
              addStatusFilter(builder, taskFilter.getStatus());
              return builder;
            }
        );
  }

  private void addStatusFilter(BoolQuery.Builder builder, TaskStatus taskStatus) {
    if (taskStatus != null) {
      addTermFilter(builder, SchemaConstants.STATUS, taskStatus.name());
    }
  }

  private void addPriorityFilter(BoolQuery.Builder builder, TaskPriority taskPriority) {
    if (taskPriority != null) {
      addTermFilter(builder, SchemaConstants.PRIORITY, taskPriority.name());
    }
  }

  private void addTermFilter(BoolQuery.Builder bool, String field, String value) {
    if (value != null && !value.isBlank()) {
      bool.must(qb -> qb.term(t -> t.field(field).value(value)));
    }
  }
}
