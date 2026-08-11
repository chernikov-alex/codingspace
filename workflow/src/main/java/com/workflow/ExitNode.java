package com.workflow.nodes;

import com.workflow.model.ExecutionContext;
import com.workflow.model.ExitState;

/**
 * Terminal node — sets the exit state on the context and signals the executor
 * to stop traversal.
 *
 * Must always be present at the end of every branch.
 * The executor checks context.isExited() after each node and stops if true.
 */
public class ExitNode extends WorkflowNode {

    private final ExitState.Status status;
    private final String message;  // optional

    public ExitNode(String id, ExitState.Status status, String message) {
        super(id);
        this.status  = status;
        this.message = message;
    }

    public ExitNode(String id, ExitState.Status status) {
        this(id, status, null);
    }

    @Override
    public boolean execute(ExecutionContext context) {
        ExitState exit = new ExitState(status, message);
        context.setExitState(exit);
        System.out.println("[exit] " + exit);
        return true;  // return value is irrelevant — executor stops on isExited()
    }
}
