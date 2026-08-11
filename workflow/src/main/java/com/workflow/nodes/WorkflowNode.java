package com.workflow;

import com.workflow.model.ExecutionContext;

/**
 * Base class for every node in the workflow tree.
 *
 * Each node has:
 *   - a unique id (for tracing and output attribution)
 *   - an onTrue pointer  (next node if result is TRUE or node is unconditional)
 *   - an onFalse pointer (next node if result is FALSE; null for non-branching nodes)
 *
 * execute() performs the node's action and returns the branch result:
 *   - true  → follow onTrue
 *   - false → follow onFalse
 *
 * Non-conditional nodes always return true (executor follows onTrue as the
 * single continuation pointer). This is explicit in each subclass.
 */
public abstract class WorkflowNode {

    protected final String id;
    private WorkflowNode onTrue;
    private WorkflowNode onFalse;

    protected WorkflowNode(String id) {
        this.id = id;
    }

    public String getId() { return id; }

    public WorkflowNode getOnTrue()  { return onTrue; }
    public WorkflowNode getOnFalse() { return onFalse; }

    public void setOnTrue(WorkflowNode node)  { this.onTrue  = node; }
    public void setOnFalse(WorkflowNode node) { this.onFalse = node; }

    /**
     * Execute this node's logic.
     *
     * @param context the mutable global execution context
     * @return true to follow onTrue, false to follow onFalse
     * @throws com.workflow.model.WorkflowRuntimeException on unrecoverable error
     */
    public abstract boolean execute(ExecutionContext context);

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + id + "]";
    }
}
