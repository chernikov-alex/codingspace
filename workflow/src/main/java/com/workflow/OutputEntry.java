package com.workflow;

/**
 * One entry in the execution output log.
 * Returned to the UI as part of the execution result.
 */
public class OutputEntry {

    private final String nodeId;
    private final String type;   // "print", "read_file", etc.
    private final String value;

    public OutputEntry(String nodeId, String type, String value) {
        this.nodeId = nodeId;
        this.type   = type;
        this.value  = value;
    }

    public String getNodeId() { return nodeId; }
    public String getType()   { return type; }
    public String getValue()  { return value; }

    @Override
    public String toString() {
        return "[" + type + " | node=" + nodeId + "] " + value;
    }
}
