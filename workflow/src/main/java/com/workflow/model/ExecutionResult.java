package com.workflow;

import java.util.List;

/**
 * Returned to the caller (and ultimately to the UI) after execution completes.
 */
public class ExecutionResult {

    private final String workflowId;
    private final ExitState exitState;
    private final List<OutputEntry> outputs;
    private final List<String> executionTrace;  // ordered node IDs visited

    public ExecutionResult(
            String workflowId,
            ExitState exitState,
            List<OutputEntry> outputs,
            List<String> executionTrace) {
        this.workflowId     = workflowId;
        this.exitState      = exitState;
        this.outputs        = outputs;
        this.executionTrace = executionTrace;
    }

    public String getWorkflowId()         { return workflowId; }
    public ExitState getExitState()       { return exitState; }
    public List<OutputEntry> getOutputs() { return outputs; }
    public List<String> getExecutionTrace() { return executionTrace; }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("═══════════════════════════════════════\n");
        sb.append("  Workflow:  ").append(workflowId).append("\n");
        sb.append("  Status:    ").append(exitState).append("\n");
        sb.append("  Trace:     ").append(executionTrace).append("\n");
        if (!outputs.isEmpty()) {
            sb.append("  Outputs:\n");
            for (OutputEntry e : outputs) {
                sb.append("    • ").append(e).append("\n");
            }
        }
        sb.append("═══════════════════════════════════════");
        return sb.toString();
    }
}
