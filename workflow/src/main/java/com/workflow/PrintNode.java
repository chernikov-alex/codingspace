package com.workflow.nodes;

import com.workflow.model.ExecutionContext;

/**
 * Appends a value to the execution output log (returned to the UI).
 *
 * value: either a "$varName" reference or a literal string.
 */
public class PrintNode extends WorkflowNode {

    private final String value;  // "$varName" or literal string

    public PrintNode(String id, String value) {
        super(id);
        this.value = value;
    }

    @Override
    public boolean execute(ExecutionContext context) {
        String resolved;
        if (value.startsWith("$")) {
            resolved = String.valueOf(context.getVariable(value.substring(1)));
        } else {
            resolved = value;
        }
        context.appendOutput(id, "print", resolved);
        System.out.println("[print] " + resolved);
        return true;  // unconditional — always follow onTrue
    }
}
