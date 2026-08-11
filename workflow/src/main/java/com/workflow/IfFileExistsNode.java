package com.workflow.nodes;

import com.workflow.model.ExecutionContext;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Branches based on whether a file exists at the given path.
 *
 * Both onTrue and onFalse should always be set for this node —
 * it's a pure branching node with no other side effect.
 *
 * Returns true  → file exists    → follow onTrue
 * Returns false → file not found → follow onFalse
 */
public class IfFileExistsNode extends WorkflowNode {

    private final String filePath;

    public IfFileExistsNode(String id, String filePath) {
        super(id);
        this.filePath = filePath;
    }

    @Override
    public boolean execute(ExecutionContext context) {
        boolean exists = Files.exists(Path.of(filePath));
        System.out.println("[if_file_exists] " + filePath + " → " + (exists ? "exists" : "not found"));
        return exists;
    }
}
