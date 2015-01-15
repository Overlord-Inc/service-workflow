/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package inc.overloard.service.workflow;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author achelian
 */
@Data @Slf4j
public class EndpointListener implements Runnable {
    final Endpoint endpoint;
    final long timeout;
    final HashMap<UUID, WorkflowInstance> outstandingWorkflows = new HashMap<>();

    public void register(WorkflowInstance instance) {
        outstandingWorkflows.put(instance.getIdentifier(), instance);
    }

    @Override
    public void run() {
        try {
            Message message = endpoint.incoming.poll(timeout, TimeUnit.MILLISECONDS);
            if (message != null) {
                UUID workflowId = message.getWorkflowId();
                WorkflowInstance workflowInstance = outstandingWorkflows.get(workflowId);
                if (workflowInstance != null) {
                    workflowInstance.processMessage(message);
                }
            }
        }
        catch (final InterruptedException ex) {
            log.info("Processing interrupted, likely shutdown", ex);
        }
    }
}
