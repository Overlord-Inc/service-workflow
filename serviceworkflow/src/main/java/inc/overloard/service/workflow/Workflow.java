/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package inc.overloard.service.workflow;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import lombok.Data;

/**
 *
 * @author achelian
 */
@Data
public abstract class Workflow {
    /**
     * The name of this workflow.
     */
    final String name;
    /**
     * These are the communications queues to talk to.
     */
    final Endpoint endpoint;

    /**
     * Creates a workflow instance given some data for the instance. Ideally,
     * the data is serializable and can be saved to disk, or passed over the
     * wire for distributed instances.
     * @param data
     * @param executor
     * @return 
     */
    public abstract WorkflowInstance createInstance(ScheduledThreadPoolExecutor executor, Object data);
    public abstract void updateWorkflow(WorkflowInfo workflowInfo) throws Exception;
}
