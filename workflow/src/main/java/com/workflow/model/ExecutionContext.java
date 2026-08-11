package com.workflow;

import java.util.*;

/**
 * Global mutable state for a single workflow execution.
 * Passed through every node during traversal.
 */
public class ExecutionContext {

    private final Map<String, Object> variables = new LinkedHashMap<>();
    private final List<OutputEntry> outputLog = new ArrayList<>();
    private ExitState exitState = null;

    // ── Variable store ────────────────────────────────────────────────────────

    public void setVariable(String name, Object value) {
        variables.put(name, value);
    }

    /**
     * Resolves a variable by name.
     * Throws if not set — treated as a NullPointer-style runtime error.
     */
    public Object getVariable(String name) {
        if (!variables.containsKey(name)) {
            throw new WorkflowRuntimeException(
                "Variable '" + name + "' is referenced but has not been set.");
        }
        return variables.get(name);
    }

    public boolean hasVariable(String name) {
        return variables.containsKey(name);
    }

    // ── Output log ────────────────────────────────────────────────────────────

    public void appendOutput(String nodeId, String type, String value) {
        outputLog.add(new OutputEntry(nodeId, type, value));
    }

    public List<OutputEntry> getOutputLog() {
        return Collections.unmodifiableList(outputLog);
    }

    // ── Exit state ────────────────────────────────────────────────────────────

    public void setExitState(ExitState state) {
        this.exitState = state;
    }

    public ExitState getExitState() {
        return exitState;
    }

    public boolean isExited() {
        return exitState != null;
    }

    // ── Debug / introspection ─────────────────────────────────────────────────

    public Map<String, Object> getVariables() {
        return Collections.unmodifiableMap(variables);
    }

    @Override
    public String toString() {
        return "ExecutionContext{variables=" + variables
                + ", outputLog=" + outputLog
                + ", exitState=" + exitState + "}";
    }
}
