package com.workflow.nodes;

import com.workflow.model.ExecutionContext;
import com.workflow.model.WorkflowRuntimeException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Reads a file's contents into a named variable.
 *
 * Failure (file not found, permission denied, IO error) throws
 * WorkflowRuntimeException, which the executor converts to exit(failure).
 * Use if_file_exists upstream if you want explicit branching on absence.
 */
public class ReadFileNode extends WorkflowNode {

    private final String filePath;
    private final String resultVariable;

    public ReadFileNode(String id, String filePath, String resultVariable) {
        super(id);
        this.filePath       = filePath;
        this.resultVariable = resultVariable;
    }

    @Override
    public boolean execute(ExecutionContext context) {
        System.out.println("[read_file] " + filePath + " → $" + resultVariable);
        try {
            String content = Files.readString(Path.of(filePath), StandardCharsets.UTF_8);
            context.setVariable(resultVariable, content);
            context.appendOutput(id, "read_file", content);
            return true;
        } catch (IOException e) {
            throw new WorkflowRuntimeException(
                "read_file failed for path '" + filePath + "': " + e.getMessage(), e);
        }
    }
}
