/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package inc.overloard.service.workflow;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 *
 * @author achelian
 */
public class ServiceImpl implements Service {
    HashMap<String, Workflow> workflows;
    HashMap<String, Endpoint> endpoints;
    HashMap<UUID, WorkflowInstance> workflowInstances;
    ScheduledThreadPoolExecutor executor;

    @Override
    public Endpoint createEndpoint(EndpointInfo endpointInfo) throws Exception {
        Endpoint endpoint = new Endpoint(endpointInfo.getName(), new LinkedBlockingQueue<>(), new LinkedBlockingQueue<>());
        endpoints.put(endpointInfo.getName(), endpoint);
        return endpoint;
    }

    @Override
    public Endpoint getEndpoint(String name) throws Exception {
        if (name == null) {
            throw new IllegalArgumentException("Missing name");
        }
        Endpoint endpoint = endpoints.get(name);
        if (endpoint != null) {
            return endpoint;
        }
        else {
            throw new IllegalArgumentException("Unknown name");
        }
    }

    @Override
    public Endpoint updateEndpoint(String name, EndpointInfo endpointInfo) throws Exception {
        if (name == null) {
            throw new IllegalArgumentException("Missing name");
        }
        Endpoint endpoint = endpoints.get(name);
        if (endpoint != null) {
            return endpoint;
        }
        else {
            throw new IllegalArgumentException("Unknown name");
        }
    }

    @Override
    public void deleteEndpoint(String name) throws Exception {
        if (name == null) {
            throw new IllegalArgumentException("Missing name");
        }
        Endpoint endpoint = endpoints.remove(name);
        if (endpoints == null) {
            throw new IllegalArgumentException("No such endpoint");
        }
    }

    @Override
    public Workflow createWorkflow(WorkflowInfo workflowInfo) throws Exception {
        Workflow workflow = null;
        Endpoint endpoint = endpoints.get(workflowInfo.getEndpointName());
        if (workflowInfo instanceof ConstantRateWorkflowInfo) {
            ConstantRateWorkflowInfo info = (ConstantRateWorkflowInfo) workflowInfo;
            workflow = new ConstantRateWorkflow(workflowInfo.getName(), endpoint, info.getDelay());
        }
        workflows.put(workflowInfo.getName(), workflow);
        return workflow;
    }

    @Override
    public Workflow getWorkflow(String name) throws Exception {
        if (name == null) {
            throw new IllegalArgumentException("Missing name");
        }
        Workflow workflow = workflows.get(name);
        if (workflow != null) {
            return workflow;
        }
        throw new IllegalArgumentException("Unknown workflow");
    }

    @Override
    public Workflow updateWorkflow(String name, WorkflowInfo info) throws Exception {
        if (name == null) {
            throw new IllegalArgumentException();
        }
        Workflow workflow = workflows.get(name);
        return workflow;
    }

    @Override
    public void deleteWorkflow(String name) throws Exception {
        if (name == null) {
            throw new IllegalArgumentException("Missing name");
        }
        workflows.remove(name);
    }

    @Override
    public WorkflowInstance createWorkflowInstance(String workflowName, Object data) throws Exception {
        Workflow workflow = workflows.get(workflowName);
        if (workflow != null) {
            WorkflowInstance instance = workflow.createInstance(executor, data);
            return instance;
        }
        throw new IllegalArgumentException("Unknown workflow for: " + workflowName);
    }

    @Override
    public WorkflowInstance getWorkflowInstance(UUID identifier) throws Exception {
        if (identifier == null) {
            throw new IllegalArgumentException("No identifier");
        }
        WorkflowInstance instance = workflowInstances.get(identifier);
        return instance;
    }

    @Override
    public WorkflowInstance updateWorkflowInstance(UUID identifier, WorkflowInstanceInfo info) throws Exception {
        return null;
    }

    @Override
    public void deleteWorkflowInstance(UUID identifier) throws Exception {
        if (identifier == null) {
            throw new IllegalArgumentException("");
        }
        WorkflowInstance instance = workflowInstances.get(identifier);
        executor.remove(instance);
        workflowInstances.remove(identifier);
    }

}
