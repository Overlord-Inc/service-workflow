/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package inc.overloard.service.workflow;

import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author achelian
 */
public class WiredConstantRateWorkflow {
    ScheduledThreadPoolExecutor executor;
    ConstantRateWorkflow workflow;
    ConstantRateWorkflowInstance workflowInstance;
    Endpoint endpoint;
    BlockingQueue<Message> outgoing;
    BlockingQueue<Message> incoming;
    EndpointListener endpointListener;
    Random random = new Random();

    @Before
    public void init() {
        executor = new ScheduledThreadPoolExecutor(2);
        outgoing = new LinkedBlockingQueue<>();
        incoming = new LinkedBlockingQueue<>();
        endpoint = new Endpoint("endpoint", outgoing, incoming);
        workflow = new ConstantRateWorkflow("example", endpoint, 10l);
        workflowInstance = new ConstantRateWorkflowInstance(workflow, executor, null);
        endpointListener = new EndpointListener(endpoint, 20l);
        endpointListener.register(workflowInstance);
        executor.scheduleAtFixedRate(endpointListener, 0l, 10l, TimeUnit.MILLISECONDS);
    }

    @After
    public void shutdown() {
        try {
            executor.awaitTermination(300, TimeUnit.SECONDS);
        }
        catch (InterruptedException ex) {
            Logger.getLogger(WiredConstantRateWorkflow.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    @Test
    public void runABunch() {
        Callable<Boolean> runRandomContinue = new Callable<Boolean>() {
            @Override
            public Boolean call() {
                if (random.nextBoolean()) {
                    WorkflowMessage message = new WorkflowMessage(workflowInstance, WorkflowMessageStatus.SuccessfulContinue);
                    incoming.add(message);
                    return Boolean.TRUE;
                }
                else {
                    WorkflowMessage message = new WorkflowMessage(workflowInstance, WorkflowMessageStatus.FailedContinue);
                    incoming.add(message);
                    return Boolean.FALSE;
                }
            }
        };
        // this should keep executing until
        executor.schedule(workflowInstance, 0l, TimeUnit.MILLISECONDS);
        int successCount = 0;
        int failureCount = 0;
        do {
            try {
                Message message = outgoing.take();
                Future<Boolean> successFuture = executor.schedule(runRandomContinue, 30l, TimeUnit.MILLISECONDS);
                try {
                    Boolean success = successFuture.get();
                    if (success) {
                        successCount++;
                    }
                    else {
                        failureCount++;
                    }
                }
                catch (ExecutionException ex) {

                }
            }
            catch (InterruptedException ex) {
                
            }
        }
        while (successCount < 20);
        try {
            Message message = outgoing.take();
            incoming.add(new WorkflowMessage(workflowInstance, WorkflowMessageStatus.SuccessfulFinish));
        }
        catch (InterruptedException ex) {
            
        }
        try {
            while (!incoming.isEmpty()) {
                Thread.sleep(10l);
            }
        }
        catch (InterruptedException e) {
            
        }
        executor.remove(endpointListener);
        executor.shutdown();
        assertEquals(21, workflowInstance.getSuccesses());
        assertTrue(workflowInstance.isFinished());
        assertEquals(WorkflowStatus.Successful, workflowInstance.getStatus());
    }
}
