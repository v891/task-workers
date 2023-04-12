package com.example.taskworkers.config;

import com.netflix.conductor.common.metadata.tasks.TaskDef;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class TasksConfig {

    @Bean
    public TaskDef createDynamicTasksDef() {
        final TaskDef taskDef = new TaskDef("create_dynamic_tasks", "This will create the required number of dynamic " +
                "tasks");

        taskDef.setInputKeys(List.of("numberOfTasks"));
        taskDef.setOutputKeys(List.of("dynamicTasksJSON", "dynamicTasksInputJSON"));
        taskDef.setResponseTimeoutSeconds(90);
        taskDef.setOwnerEmail("watch@myworkflow.com");
        return taskDef;
    }

    @Bean
    public TaskDef logInputSquareTaskDef() {
        final TaskDef taskDef = new TaskDef("task_log_input_square", "This will log the input square");

        taskDef.setInputKeys(List.of("givenNumber"));
        taskDef.setOutputKeys(List.of("square"));
        taskDef.setResponseTimeoutSeconds(90);
        taskDef.setOwnerEmail("watch@myworkflow.com");
        return taskDef;
    }
}
