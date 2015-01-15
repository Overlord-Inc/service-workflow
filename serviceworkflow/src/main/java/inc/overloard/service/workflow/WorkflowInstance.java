/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package inc.overloard.service.workflow;

import java.util.UUID;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import lombok.Data;
import lombok.NonNull;
import org.codehaus.jackson.annotate.JsonIgnore;

/**
 * This is an instance of the workflow. State should be stored per instance.
 * @author achelian
 * @param <W>
 */
@Data
public abstract class WorkflowInstance<W extends Workflow> implements Runnable {
    /**
     * The workflow being executed.
     */
    final W workflow;
    /**
     * The current status of the workflow.
     */
    WorkflowStatus status;

    final UUID identifier;

    /**
     * The executor that will be invoked to kick off subtasks. The executor
     * should be provided by the deserializing environment.
     */
    @JsonIgnore
    final ScheduledThreadPoolExecutor executor;

    final long startedTimestamp;

    final UUID parentIdentifier;

    final Object data;

    WorkflowInstance(@NonNull W workflow, @NonNull ScheduledThreadPoolExecutor executor, Object data) {
        this.workflow = workflow;
        this.status = WorkflowStatus.Initialized;
        this.identifier = UUID.randomUUID();
        this.executor = executor;
        this.startedTimestamp = System.currentTimeMillis();
        this.parentIdentifier = null;
        this.data = data;
    }

    WorkflowInstance(@NonNull W workflow, @NonNull ScheduledThreadPoolExecutor executor, Object data, WorkflowInstance parent) {
        this.workflow = workflow;
        this.status = WorkflowStatus.Initialized;
        this.identifier = UUID.randomUUID();
        this.executor = executor;
        this.startedTimestamp = System.currentTimeMillis();
        this.parentIdentifier = parent.getIdentifier();
        this.data = data;
    }

    public abstract void processMessage(Message message);
}
