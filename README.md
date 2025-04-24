# Task Management Service

A Spring Boot Netflix DGS GraphQL simple task management service that lets you:

* create, update and search tasks with filters (priority, status e.t.c) and provide some basic task statistics.

## Schema & Key features

```
type TaskDto {
id: Int
title: String
description: String
priority: TaskPriorityDto (NONE, LOW, MID, HIGH, NONE)
status: TaskStatusDto (CREATED, TO_DO, IN_PROGRESS, COMPLETED)
dueDate: Date
createDate: Instant
}

type Query {
    getTasksByFilter(filter : TaskFilterInput!) : [TaskDto!]!
}

input TaskFilterInput {
    priority: TaskPriorityDto
    status: TaskStatusDto
    dueDateBefore: Date
}

...more details: src/main/resources/schema/schema.graphqls
```

## Architecture

This project follows the principles of **Hexagonal Architecture** (also known as Ports and Adapters). The codebase is
organized into the following main layers:

* **`domain`**: Contains the core business logic, domain models, and input/output ports. It has no dependencies on other
  layers.
* **`infrastructure`**: Implements adapters for external systems like databases, message queues, or external APIs. It
  depends on the domain layer (output ports).
* **`entrypoint` (or `adapters/primary`)**: Contains entrypoint that drive the application, such as GraphQL & REST

## API Documentation

The service exposes a GraphQL API. The schema is defined in `src/main/resources/schema/schema.graphqls`.

**Key Operations:**

* **Queries:**
    * `getTasksByFilter`: Retrieve tasks based on filters (priority, status, due date).
    * `searchTasks`: Search tasks by term (Implement later).
    * `getTaskStats`: Get statistics about tasks (Implement later).
* **Mutations:**
    * `createTask`: Create a new task.
    * `updateTask`: Update an existing task.
    * `deleteTaskById`: Delete a task by its ID.

**DB**

```
CREATE TABLE Task (
id BIGINT AUTO_INCREMENT PRIMARY KEY,
title VARCHAR(255) NOT NULL,
description TEXT NOT NULL,
priority VARCHAR(10) NOT NULL,
status VARCHAR(15) NOT NULL,
dueDate DATE NULL,
createDate DATETIME NOT NULL
);```