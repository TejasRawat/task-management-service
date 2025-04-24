package com.example.taskmanagement.common;

import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.utility.DockerImageName;

public class MySQLContainerHolder {

  private static MySQLContainer MYSQL_CONTAINER = new MySQLContainer<>(DockerImageName.parse("mysql:8.0"))
      .withReuse(true);

  static {
    MYSQL_CONTAINER.start();
  }

  public static MySQLContainer getMysqlContainer() {
    return MYSQL_CONTAINER;
  }

  public static String getDatabaseUserName() {
    return MYSQL_CONTAINER.getUsername();
  }

  public static String getDatabasePassword() {
    return MYSQL_CONTAINER.getPassword();
  }

  public static String getDatabaseName() {
    return MYSQL_CONTAINER.getDatabaseName();
  }


  public static String getDatabaseURL() {
    return MYSQL_CONTAINER.getJdbcUrl();
  }

  public static String getDatabaseDriverClassName() {
    return MYSQL_CONTAINER.getDriverClassName();
  }
}
