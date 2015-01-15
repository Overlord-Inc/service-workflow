/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package inc.overloard.service.workflow;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import lombok.Data;

/**
 *
 * @author achelian
 */
@Data
public class ConstantRateWorkflow extends Workflow {
    final long delay;
    ConstantRateWorkflow(final String name, final Endpoint endpoint, final long delay) {
        super(name, endpoint);
        this.delay = delay;
    }

    @Override
    public WorkflowInstance createInstance(ScheduledThreadPoolExecutor executor, Object data) {
        return new ConstantRateWorkflowInstance(this, executor, data);
    }

    // The only thing updateable here is the delay
    @Override
    public void updateWorkflow(WorkflowInfo workflowInfo) throws Exception {
        if (workflowInfo instanceof ConstantRateWorkflowInfo) {
            ConstantRateWorkflowInfo info = (ConstantRateWorkflowInfo) workflowInfo;
        }
    }
}
