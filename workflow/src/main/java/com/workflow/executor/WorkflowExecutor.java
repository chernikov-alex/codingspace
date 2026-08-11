package com.workflow;

import com.workflow.model.*;
import com.workflow.nodes.WorkflowNode;

import java.util.ArrayList;
import java.util.List;

/**
 * Traverses the workflow node tree and produces an ExecutionResult.
 *
 * Algorithm:
 *   1. Start at the entry node with a fresh ExecutionContext.
 *   2. Execute the current node.
 *   3. If the node set an exit state → stop, collect result.
 *   4. Choose next node: result==true → onTrue, result==false → onFalse.
 *   5. If next node is null → implicit success exit.
 *   6. Repeat from step 2.
 *
 * Any WorkflowRuntimeException thrown by a node is caught here and
 * converted into exit(FAILURE) with the exception message.
 */
public class WorkflowExecutor {

    public ExecutionResult execute(String workflowId, WorkflowNode entryNode) {
        ExecutionContext context = new ExecutionContext();
        List<String> trace      = new ArrayList<>();

        System.out.println("\n▶ Starting workflow: " + workflowId);
        System.out.println("─────────────────────────────────────────");

        WorkflowNode current = entryNode;

        try {
            while (current != null) {
                trace.add(current.getId());

                boolean result = current.execute(context);

                // Exit node wrote to context — stop immediately
                if (context.isExited()) {
                    break;
                }

                // Choose the next node based on the boolean result
                WorkflowNode next = result ? current.getOnTrue() : current.getOnFalse();

                if (next == null && !context.isExited()) {
                    // Null pointer at end of a branch = implicit success
                    context.setExitState(ExitState.success("implicit end of branch"));
                }

                current = next;
            }

        } catch (WorkflowRuntimeException ex) {
            // Unrecoverable node error → fail the whole execution
            System.out.println("[executor] runtime error: " + ex.getMessage());
            context.setExitState(ExitState.failure(ex.getMessage()));
        }

        System.out.println("─────────────────────────────────────────");

        return new ExecutionResult(
            workflowId,
            context.getExitState(),
            context.getOutputLog(),
            trace
        );
    }
}
