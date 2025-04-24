package com.example.taskmanagement.infrastructure.repository.mysql.repository;

import com.example.taskmanagement.infrastructure.repository.mysql.entity.TaskMySQLEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskMySQLRepository extends JpaRepository<TaskMySQLEntity, Integer>,
    JpaSpecificationExecutor<TaskMySQLEntity> {

}
