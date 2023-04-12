package com.example.taskworkers.workers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.conductor.client.worker.Worker;
import com.netflix.conductor.common.metadata.tasks.Task;
import com.netflix.conductor.common.metadata.tasks.TaskDef;
import com.netflix.conductor.common.metadata.tasks.TaskResult;
import com.netflix.conductor.common.metadata.tasks.TaskType;
import com.netflix.conductor.common.metadata.workflow.SubWorkflowParams;
import com.netflix.conductor.common.metadata.workflow.WorkflowTask;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Component
public class CreateDynamicTasksWorker implements Worker {

    private final TaskDef createDynamicTasksDef;
    private final ObjectMapper objectMapper;

    @Override
    public String getTaskDefName() {
        return createDynamicTasksDef.getName();
    }

    @Override
    public TaskResult execute(Task task) {
        final TaskResult taskResult = new TaskResult();

        taskResult.setStatus(TaskResult.Status.COMPLETED);
        final Integer numberOfTasks = Integer.valueOf(task.getInputData().get("numberOfTasks").toString());
        final List<Integer> list = new ArrayList<>();
        for (int i = 0; i < numberOfTasks; i++) {
            Integer integer = i;
            list.add(integer);
        }

        final List<WorkflowTask> tasks = list.stream()
                .map(integer -> {
                            final WorkflowTask workflowTask = new WorkflowTask();
                            workflowTask.setName("wf_log_input_square");
                            workflowTask.setTaskReferenceName("wf_log_input_square_" + integer);
                            workflowTask.setType(TaskType.SUB_WORKFLOW.name());

                            final SubWorkflowParams subWorkflowParams = new SubWorkflowParams();
                            subWorkflowParams.setName("wf_log_input_square");
                            subWorkflowParams.setVersion(1);

                            workflowTask.setSubWorkflowParam(subWorkflowParams);
                            return workflowTask;
                        }
                )
                .toList();

        final Map<String, Map<String, Integer>> tasksInput = list.stream()
                .collect(Collectors.toMap(integer -> "wf_log_input_square_" + integer, integer -> Map.of("givenNumber", integer)));

        final JsonNode dynamicTasks = objectMapper.valueToTree(tasks);
        final JsonNode dynamicTasksInput = objectMapper.valueToTree(tasksInput);

        taskResult.getOutputData().put("dynamicTasksJSON", dynamicTasks);
        taskResult.getOutputData().put("dynamicTasksInputJSON", dynamicTasksInput);
//        log.info("dynamicTasksJSON: {}", dynamicTasks);
//        log.info("dynamicTasksInputJSON: {}", dynamicTasksInput);
        return taskResult;
    }
}
