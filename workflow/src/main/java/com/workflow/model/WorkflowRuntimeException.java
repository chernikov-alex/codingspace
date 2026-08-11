package com.workflow;

/**
 * Thrown when a node encounters an unrecoverable condition during execution:
 * - undefined variable reference
 * - call_service non-2xx response
 * - read_file failure
 * - type mismatch in comparison
 *
 * The executor catches this at the top level and converts it into
 * an ExitState(FAILURE) with the exception message.
 */
public class WorkflowRuntimeException extends RuntimeException {

    public WorkflowRuntimeException(String message) {
        super(message);
    }

    public WorkflowRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}
