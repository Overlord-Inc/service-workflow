/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package inc.overloard.service.workflow;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 *
 * @author achelian
 */
@Data @EqualsAndHashCode(callSuper = true)
public class WorkflowMessage extends Message {
    final WorkflowMessageStatus status;
    WorkflowMessage(WorkflowInstance workflow, WorkflowMessageStatus status) {
        super(workflow);
        this.status = status;
    }
}
