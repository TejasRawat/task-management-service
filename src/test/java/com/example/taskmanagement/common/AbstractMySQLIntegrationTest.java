package com.example.taskmanagement.common;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

@SpringBootTest
public class AbstractMySQLIntegrationTest {

  @Autowired
  protected TestMySQLTaskRepository testMySQLTaskRepository;

  @DynamicPropertySource
  static void configureMySQLProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", () -> MySQLContainerHolder.getDatabaseURL());
    registry.add("spring.datasource.driverClassName", () -> MySQLContainerHolder.getDatabaseDriverClassName());
    registry.add("spring.datasource.username", () -> MySQLContainerHolder.getDatabaseUserName());
    registry.add("spring.datasource.password", () -> MySQLContainerHolder.getDatabasePassword());
  }

  @BeforeEach
  public void initialiseMySQLTable() {
    testMySQLTaskRepository.deleteAll();
  }

}
