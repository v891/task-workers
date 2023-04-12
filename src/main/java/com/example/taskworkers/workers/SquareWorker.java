package com.example.taskworkers.workers;

import com.netflix.conductor.client.worker.Worker;
import com.netflix.conductor.common.metadata.tasks.Task;
import com.netflix.conductor.common.metadata.tasks.TaskDef;
import com.netflix.conductor.common.metadata.tasks.TaskResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Component
public class SquareWorker implements Worker {

    private final TaskDef logInputSquareTaskDef;

    @Override
    public String getTaskDefName() {
        return logInputSquareTaskDef.getName();
    }

    @Override
    public TaskResult execute(Task task) {
        final TaskResult taskResult = new TaskResult(task);
        log.info("Executing task: {} with inputs: {}", task.getTaskDefName(), task.getInputData());
        final Integer givenNumber = Integer.valueOf(task.getInputData().get("givenNumber").toString());

        log.info("Square of {} is {}", givenNumber, givenNumber * givenNumber);

        task.setOutputData(Map.of("square", givenNumber * givenNumber));
        taskResult.setStatus(TaskResult.Status.COMPLETED);
        return taskResult;
    }
}
