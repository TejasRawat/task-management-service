package com.example.taskmanagement.common;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.Conflicts;
import co.elastic.clients.elasticsearch._types.Refresh;
import co.elastic.clients.elasticsearch.core.BulkRequest;
import co.elastic.clients.elasticsearch.core.BulkResponse;
import co.elastic.clients.elasticsearch.core.bulk.BulkOperation;
import co.elastic.clients.elasticsearch.indices.CreateIndexRequest;
import com.example.taskmanagement.adapters.out.repository.elasticsearch.constants.ElasticSearchConstants;
import com.example.taskmanagement.adapters.out.repository.elasticsearch.entity.TaskESDoc;
import java.io.IOException;
import java.util.List;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

@SpringBootTest
@Slf4j
public class AbstractElasticsearchIntegrationTest {

  private static boolean elasticsearchSchemaInitialized = false;

  @Autowired
  protected ElasticsearchClient esClient;

  @Value("classpath:elasticsearch/schema.json")
  private Resource elasticsearchSchema;

  @DynamicPropertySource
  static void configureMySQLProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.elasticsearch.uris", () -> ElasticsearchContainerHolder.getHttpHostAddress());
    registry.add("spring.datasource.url", () -> MySQLContainerHolder.getDatabaseURL());
    registry.add("spring.datasource.driverClassName", () -> MySQLContainerHolder.getDatabaseDriverClassName());
    registry.add("spring.datasource.username", () -> MySQLContainerHolder.getDatabaseUserName());
    registry.add("spring.datasource.password", () -> MySQLContainerHolder.getDatabasePassword());
  }

  @BeforeEach
  @SneakyThrows
  protected void init() {
    if (elasticsearchSchemaInitialized) {
      flushIndex();
      return;
    }
    boolean indexAlreadyExists = esClient.indices().exists(b -> b.index(ElasticSearchConstants.ELASTICSEARCH_INDEX)).value();
    if (indexAlreadyExists) {
      flushIndex();
    } else {
      esClient.indices().create(
          new CreateIndexRequest.Builder()
              .index(ElasticSearchConstants.ELASTICSEARCH_INDEX)
              .withJson(elasticsearchSchema.getInputStream()).build()
      ).index();
    }
    elasticsearchSchemaInitialized = true;
  }

  private void flushIndex() throws IOException {
    esClient.deleteByQuery(
        dbq -> dbq.index(ElasticSearchConstants.ELASTICSEARCH_INDEX)
            .query(qb -> qb.matchAll(b -> b))
            .waitForCompletion(true)
            .refresh(true)
            .conflicts(Conflicts.Proceed)
    );
  }

  @SneakyThrows
  public void saveTasks(List<TaskESDoc> docs) {
    List<BulkOperation> bulkOperations = docs.stream()
        .map(doc -> BulkOperation.of(b -> b
            .index(ib -> ib
                .id(doc.getId())
                .document(doc)))
        ).toList();

    BulkRequest request = new BulkRequest.Builder()
        .index(ElasticSearchConstants.ELASTICSEARCH_INDEX)
        .operations(bulkOperations)
        .refresh(Refresh.True)
        .build();

    BulkResponse response = esClient.bulk(request);

    if (response.errors()) {
      List<String> errorReasons =
          response.items().stream().filter(item -> item.error() != null).map(item -> item.error().reason()).toList();
      log.error("Elasticsearch bulk insert error {}", errorReasons);
    }
  }

}
