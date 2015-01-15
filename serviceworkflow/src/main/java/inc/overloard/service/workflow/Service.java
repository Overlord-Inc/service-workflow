/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package inc.overloard.service.workflow;

import java.util.UUID;

/**
 * 
 * @author achelian
 */
public interface Service {
    /**
     * Create an endpoint. This takes information about the endpoint and
     * tries to create the endpoint.
     * @param endpointInfo The information needed to create the endpoint.
     * @return The created endpoint.
     * @throws Exception Decided by the implementation and error.
     */
    Endpoint createEndpoint(EndpointInfo endpointInfo) throws Exception;

    /**
     * Get the endpoint using the identifying information. Not guaranteed to
     * return anything useful.
     * @param name The name of the endpoint.
     * @return
     * @throws Exception 
     */
    Endpoint getEndpoint(String name) throws Exception;

    /**
     * 
     * @param name
     * @param endpoint
     * @return
     * @throws Exception 
     */
    Endpoint updateEndpoint(String name, EndpointInfo endpointInfo) throws Exception;

    void deleteEndpoint(String name) throws Exception;

    Workflow createWorkflow(WorkflowInfo workflowInfo) throws Exception;

    Workflow getWorkflow(String name) throws Exception;

    Workflow updateWorkflow(String name, WorkflowInfo workflowInfo) throws Exception;

    void deleteWorkflow(String name) throws Exception;

    WorkflowInstance createWorkflowInstance(String workflow, Object data) throws Exception;

    WorkflowInstance getWorkflowInstance(UUID identifier) throws Exception;

    WorkflowInstance updateWorkflowInstance(UUID identifier, WorkflowInstanceInfo workflowInstanceInfo) throws Exception;

    void deleteWorkflowInstance(UUID identifier) throws Exception;
}
