/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package inc.overloard.service.workflow;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;

/**
 *
 * @author achelian
 */
public class ConstantRateWorkflowInstanceTest {
    @Mock
    ScheduledThreadPoolExecutor executor;
    @Mock
    ScheduledFuture future;
    @Mock
    BlockingQueue outgoing;
    @Mock
    BlockingQueue incoming;
    @Captor
    ArgumentCaptor<WorkflowMessage> messageCaptor;

    Endpoint endpoint;
    ConstantRateWorkflow workflow;
    ConstantRateWorkflowInstance workflowInstance;

    @Before
    public void init() {
        initMocks(this);

        when(executor.scheduleAtFixedRate(workflowInstance, 0l, 10l, TimeUnit.MILLISECONDS)).thenReturn(future);
        endpoint = new Endpoint("endpoint", outgoing, incoming);
        workflow = new ConstantRateWorkflow("workflow", endpoint, 10l);
        workflowInstance = new ConstantRateWorkflowInstance(workflow, executor, null);
    }

    @Test(expected = NullPointerException.class)
    public void testNullWorkflowInConstructor1() {
        workflowInstance = new ConstantRateWorkflowInstance(null, executor, null);
    }

    @Test(expected = NullPointerException.class)
    public void testNullWorkflowInConstructor2() {
        workflowInstance = new ConstantRateWorkflowInstance(null, executor, null, null);
    }

    @Test(expected = NullPointerException.class)
    public void testNullExecutorInConstructor1() {
        workflowInstance = new ConstantRateWorkflowInstance(workflow, null, null);
    }

    @Test(expected = NullPointerException.class)
    public void testNullExecutorInConstructor2() {
        workflowInstance = new ConstantRateWorkflowInstance(workflow, null, null, null);
    }

    @Test
    public void testBeforeFirstRun() {
        assertFalse(workflowInstance.isScheduled());
        assertFalse(workflowInstance.isFinished());
        assertNull(workflowInstance.getFuture());
        assertEquals(0, workflowInstance.getOutstanding());
        assertEquals(0, workflowInstance.getFailures());
        assertEquals(0, workflowInstance.getSuccesses());
        assertEquals(WorkflowStatus.Initialized, workflowInstance.getStatus());
    }
    @Test
    public void testHappyCaseScheduling() throws InterruptedException {
        when(outgoing.offer(any(WorkflowMessage.class))).thenReturn(true);
        workflowInstance.run();
        verify(outgoing).offer(messageCaptor.capture());
        WorkflowMessage capturedMessage = messageCaptor.getValue();
        assertEquals(WorkflowMessageStatus.Start, capturedMessage.getStatus());
        assertEquals(workflowInstance.getIdentifier(), capturedMessage.getWorkflowId());
        assertEquals(workflowInstance.getWorkflow().getName(), capturedMessage.getWorkflowName());
        assertEquals(WorkflowStatus.Processing, workflowInstance.getStatus());
        assertEquals(1, workflowInstance.getOutstanding());
        assertTrue(workflowInstance.isScheduled());
        assertFalse(workflowInstance.isFinished());
    }

    public void testFailedOfferScheduling() throws InterruptedException {
        when(outgoing.offer(any(WorkflowMessage.class))).thenReturn(false);
        workflowInstance.run();
        assertEquals(0, workflowInstance.getOutstanding());
        assertTrue(workflowInstance.isScheduled());
        assertFalse(workflowInstance.isFinished());
    }

    public void testProcessSuccessfulContinueMessage() {
        WorkflowMessage message = new WorkflowMessage(workflowInstance, WorkflowMessageStatus.SuccessfulContinue);
        workflowInstance.processMessage(message);
        assertEquals(1, workflowInstance.getOutstanding());
        assertEquals(1, workflowInstance.getSuccesses());
        verifyZeroInteractions(outgoing);
    }

    public void testMultipleRunStatements() {
        workflowInstance.run();
        workflowInstance.run();
        verify(outgoing, times(2)).offer(messageCaptor.capture());
        for (WorkflowMessage message: messageCaptor.getAllValues()) {
            assertEquals(WorkflowMessageStatus.Start, message.getStatus());
        }
        assertEquals(2, workflowInstance.getOutstanding());
        assertEquals(0, workflowInstance.getFailures());
    }
}
