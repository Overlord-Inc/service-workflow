/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package inc.overloard.service.workflow;

import lombok.Data;

/**
 *
 * @author achelian
 */
@Data
public class ConstantRateWorkflowInfo implements WorkflowInfo {
    String name;
    String endpointName;
    long delay;
}
