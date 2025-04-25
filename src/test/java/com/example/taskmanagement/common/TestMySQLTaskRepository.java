package com.example.taskmanagement.common;

import com.example.taskmanagement.infrastructure.mysql.entity.TaskMySQLEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TestMySQLTaskRepository extends JpaRepository<TaskMySQLEntity, Long> {

}
