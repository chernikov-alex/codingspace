package com.workflow;

import com.workflow.model.ExecutionContext;
import com.workflow.nodes.WorkflowNode;

/**
 * Compares two operands for equality.
 *
 * Each operand is either:
 *   "$varName"  → resolved from the execution context (throws if unset)
 *   anything else → treated as a literal (String or Number)
 *
 * Comparison semantics (JavaScript-inspired loose equality for numbers):
 *   - If both sides are numeric (or one is a string that parses as a number
 *     and the other is a Number), compare as doubles.
 *   - Otherwise compare as strings (case-sensitive).
 *
 * Returns true → follow onTrue, false → follow onFalse.
 */
public class IfEqualsNode extends WorkflowNode {

    private final String leftOperand;   // "$varName" or literal
    private final String rightOperand;  // "$varName" or literal

    public IfEqualsNode(String id, String leftOperand, String rightOperand) {
        super(id);
        this.leftOperand  = leftOperand;
        this.rightOperand = rightOperand;
    }

    @Override
    public boolean execute(ExecutionContext context) {
        Object left  = resolve(leftOperand,  context);
        Object right = resolve(rightOperand, context);

        boolean result = compare(left, right);
        System.out.println("[if_equals] " + left + " == " + right + " → " + result);
        return result;
    }

    private Object resolve(String operand, ExecutionContext context) {
        if (operand.startsWith("$")) {
            return context.getVariable(operand.substring(1));
        }
        return operand;  // literal string
    }

    /**
     * Loose numeric comparison: if either side looks like a number,
     * compare both as doubles.  Otherwise compare toString.
     */
    private boolean compare(Object left, Object right) {
        // Both already Numbers
        if (left instanceof Number l && right instanceof Number r) {
            return l.doubleValue() == r.doubleValue();
        }

        // Try numeric coercion (JS-style: "42" == 42 → true)
        Double leftNum  = toDouble(left);
        Double rightNum = toDouble(right);
        if (leftNum != null && rightNum != null) {
            return leftNum.equals(rightNum);
        }

        // Fall back to string comparison
        return String.valueOf(left).equals(String.valueOf(right));
    }

    private Double toDouble(Object val) {
        if (val instanceof Number n) return n.doubleValue();
        try { return Double.parseDouble(String.valueOf(val)); }
        catch (NumberFormatException e) { return null; }
    }
}
