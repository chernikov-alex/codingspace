package com.workflow.model;

/**
 * Terminal state of a workflow execution.
 */
public class ExitState {

    public enum Status { SUCCESS, FAILURE }

    private final Status status;
    private final String message;  // optional; useful for failure diagnosis

    public ExitState(Status status, String message) {
        this.status  = status;
        this.message = message;
    }

    public static ExitState success() {
        return new ExitState(Status.SUCCESS, null);
    }

    public static ExitState success(String message) {
        return new ExitState(Status.SUCCESS, message);
    }

    public static ExitState failure(String message) {
        return new ExitState(Status.FAILURE, message);
    }

    public Status getStatus()  { return status; }
    public String getMessage() { return message; }

    public boolean isSuccess() { return status == Status.SUCCESS; }

    @Override
    public String toString() {
        return status + (message != null ? ": " + message : "");
    }
}
