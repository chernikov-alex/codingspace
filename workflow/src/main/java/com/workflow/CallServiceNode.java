package com.workflow;

import com.workflow.model.ExecutionContext;
import com.workflow.model.WorkflowRuntimeException;
import com.workflow.nodes.WorkflowNode;

/**
 * Calls an external HTTP service and stores the response body
 * into a named variable.
 *
 * Non-2xx responses are treated as exceptions (WorkflowRuntimeException),
 * which the executor converts to exit(failure).
 *
 * The actual HTTP call is delegated to a ServiceCaller functional interface,
 * so it can be mocked in tests or wired to a real HTTP client in production.
 */
public class CallServiceNode extends WorkflowNode {

    /**
     * Abstraction over the actual HTTP call.
     * Production: inject an HttpClient-backed implementation.
     * Tests / demo:  inject a mock.
     */
    @FunctionalInterface
    public interface ServiceCaller {
        /**
         * @param url the URL to call
         * @return the response body as a String
         * @throws WorkflowRuntimeException on non-2xx or network error
         */
        String call(String url);
    }

    private final String url;
    private final String resultVariable;
    private final ServiceCaller caller;

    public CallServiceNode(String id, String url, String resultVariable, ServiceCaller caller) {
        super(id);
        this.url            = url;
        this.resultVariable = resultVariable;
        this.caller         = caller;
    }

    @Override
    public boolean execute(ExecutionContext context) {
        System.out.println("[call_service] GET " + url);
        // ServiceCaller throws WorkflowRuntimeException on failure —
        // executor catches it at the top level.
        String response = caller.call(url);
        context.setVariable(resultVariable, response);
        System.out.println("[call_service] response stored in '$" + resultVariable + "' = " + response);
        return true;  // unconditional — always follow onTrue
    }
}
