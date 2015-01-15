/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package inc.overloard.service.workflow;

import java.util.UUID;
import lombok.Data;

/**
 * Intended to be serialized in some implementations.
 * @author achelian
 */
@Data
public class Message {
    final UUID workflowId;
    final String workflowName;
    Message(WorkflowInstance workflow) {
        this.workflowId = workflow.getIdentifier();
        this.workflowName = workflow.getWorkflow().getName();
    }
}
