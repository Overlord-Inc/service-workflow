/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package inc.overloard.service.workflow;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

/**
 * This does not work. There does not seem to be an easy way to unschedule
 * something in ScheduledExecutorService, you have to use the concrete class.
 * @author achelian
 */
@Data @Slf4j @EqualsAndHashCode(callSuper = true)
public class ConstantRateWorkflowInstance extends WorkflowInstance<ConstantRateWorkflow> {
    volatile boolean scheduled = false;
    volatile boolean finished = false;
    volatile int outstanding = 0;
    volatile int failures = 0;
    volatile int successes = 0;
    volatile ScheduledFuture<?> future;

    ConstantRateWorkflowInstance(ConstantRateWorkflow workflow, ScheduledThreadPoolExecutor executor, Object data) {
        super(workflow, executor, data);
    }

    ConstantRateWorkflowInstance(ConstantRateWorkflow workflow, ScheduledThreadPoolExecutor executor, Object data, WorkflowInstance parent) {
        super(workflow, executor, data, parent);
    }

    @Override
    public void run() {
        if (!scheduled && !finished) {
            future = executor.scheduleAtFixedRate(this, 0, workflow.getDelay(), TimeUnit.MILLISECONDS);
            status = WorkflowStatus.Processing;
            scheduled = true;
        }
        WorkflowMessage message = new WorkflowMessage(this, WorkflowMessageStatus.Start);
        if (workflow.endpoint.outgoing.offer(message)) {
            outstanding++;
        }
        else {
            log.error("Failed to offer message");
        }
    }

    @Override
    public void processMessage(Message message) {
        if (message instanceof WorkflowMessage) {
            WorkflowMessage workflowMessage = (WorkflowMessage) message;
            switch (workflowMessage.getStatus()) {
                case Start: {
                    log.error("Unexpected message");
                    break;
                }
                case FailedFinish: {
                    status = WorkflowStatus.Failed;
                    if (log.isDebugEnabled()) {
                        log.debug("The task " + this.identifier.toString() + " has failed");
                    }
                    finished = true;
                    failures++;
                    outstanding--;
                    executor.remove(this);
                    break;
                }
                case SuccessfulFinish: {
                    status = WorkflowStatus.Successful;
                    if (log.isDebugEnabled()) {
                        log.debug("The task " + this.identifier.toString() + " has succeeded");
                    }
                    finished = true;
                    successes++;
                    outstanding--;
                    executor.remove(this);
                    break;
                }
                case FailedContinue: {
                    outstanding--;
                    failures++;
                    break;
                }
                case SuccessfulContinue: {
                    outstanding--;
                    successes++;
                    break;
                }
            }
        }
        else {
            // Don't know what to do with this kind of message, something sis probably wrong.
            log.error("Unknown message type");
        }
    }
}
