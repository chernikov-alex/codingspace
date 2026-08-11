package com.workflow;

import com.workflow.model.ExecutionContext;
import com.workflow.nodes.WorkflowNode;

/**
 * Sets a variable in the global execution context.
 *
 * params:
 *   name  - variable name to set
 *   value - literal value (String or Number)
 *           OR a reference to another variable prefixed with "$"
 */
public class SetVariableNode extends WorkflowNode {

    private final String variableName;
    private final Object value;  // String literal, Number, or "$varRef"

    public SetVariableNode(String id, String variableName, Object value) {
        super(id);
        this.variableName = variableName;
        this.value        = value;
    }

    @Override
    public boolean execute(ExecutionContext context) {
        Object resolved = resolveValue(value, context);
        context.setVariable(variableName, resolved);
        System.out.println("[set_variable] " + variableName + " = " + resolved);
        return true;  // unconditional — always follow onTrue
    }

    private Object resolveValue(Object val, ExecutionContext context) {
        if (val instanceof String s && s.startsWith("$")) {
            return context.getVariable(s.substring(1));
        }
        return val;
    }
}
