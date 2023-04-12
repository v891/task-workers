package com.example.taskworkers.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.conductor.client.automator.TaskRunnerConfigurer;
import com.netflix.conductor.client.http.MetadataClient;
import com.netflix.conductor.client.http.TaskClient;
import com.netflix.conductor.client.worker.Worker;
import com.netflix.conductor.common.metadata.tasks.TaskDef;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
public class ConductorClientConfig {

    private final String conductorServerUrl = "http://localhost:8099/api/";

    @Bean
    public MetadataClient metadataClient(List<TaskDef> taskDefs) {
        final MetadataClient metadataClient = new MetadataClient();
        metadataClient.setRootURI(conductorServerUrl);
        metadataClient.registerTaskDefs(taskDefs);
        return metadataClient;
    }

    @Bean
    public TaskClient taskClient() {
        final TaskClient taskClient = new TaskClient();
        taskClient.setRootURI(conductorServerUrl);
        return taskClient;
    }

    @Bean
    public TaskRunnerConfigurer taskRunnerConfigurer(List<Worker> workers, TaskClient taskClient,
                                                     List<TaskDef> taskDefs) {
        final TaskRunnerConfigurer runnerConfigurer = new TaskRunnerConfigurer.Builder(taskClient,
                workers.stream()
                        .map(name -> Collections.nCopies(3, name))
                        .flatMap(List::stream)
                        .toList())
                .withTaskThreadCount(taskDefs.stream()
                        .map(TaskDef::getName)
                        .collect(Collectors.toMap(name -> name, name -> 3)))
                .build();
        runnerConfigurer.init();
        return runnerConfigurer;
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
