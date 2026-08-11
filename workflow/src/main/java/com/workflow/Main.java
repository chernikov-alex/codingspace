package com.workflow;

import com.workflow.executor.WorkflowExecutor;
import com.workflow.model.ExecutionResult;
import com.workflow.model.ExitState;
import com.workflow.nodes.*;

/**
 * Builds the example workflow from the diagram and runs it twice:
 *
 *   call_service(url=weather.com/forecast, variable=weather)
 *       │
 *       ▼
 *   if_equals(weather, "hot")
 *       │ TRUE                          │ FALSE
 *       ▼                               ▼
 *   set_variable(destination=beach)   print("too cold")
 *       │                               │
 *       ▼                               ▼
 *   exit(success)                     exit(fail)
 *
 * Run 1: mock returns "hot"  → TRUE  branch → success
 * Run 2: mock returns "cold" → FALSE branch → failure
 */
public class Main {

    public static void main(String[] args) {

        WorkflowExecutor executor = new WorkflowExecutor();

        // ── Run 1: weather = "hot" ────────────────────────────────────────────
        System.out.println("\n╔══════════════════════════════════════╗");
        System.out.println("║  Run 1: mock weather = \"hot\"          ║");
        System.out.println("╚══════════════════════════════════════╝");

        ExecutionResult result1 = executor.execute(
            "weather-workflow",
            buildWeatherWorkflow("hot")
        );
        System.out.println("\n" + result1);

        // ── Run 2: weather = "cold" ───────────────────────────────────────────
        System.out.println("\n╔══════════════════════════════════════╗");
        System.out.println("║  Run 2: mock weather = \"cold\"         ║");
        System.out.println("╚══════════════════════════════════════╝");

        ExecutionResult result2 = executor.execute(
            "weather-workflow",
            buildWeatherWorkflow("cold")
        );
        System.out.println("\n" + result2);

        // ── Run 3: service failure (non-2xx mock) ─────────────────────────────
        System.out.println("\n╔══════════════════════════════════════╗");
        System.out.println("║  Run 3: service returns 503           ║");
        System.out.println("╚══════════════════════════════════════╝");

        ExecutionResult result3 = executor.execute(
            "weather-workflow",
            buildWeatherWorkflowWithError()
        );
        System.out.println("\n" + result3);
    }

    /**
     * Constructs the workflow tree for a given mocked weather response.
     * In production, CallServiceNode would receive a real HTTP ServiceCaller.
     */
    private static WorkflowNode buildWeatherWorkflow(String mockedWeather) {

        // ── Leaf nodes ────────────────────────────────────────────────────────

        ExitNode exitSuccess = new ExitNode("exit_success", ExitState.Status.SUCCESS);
        ExitNode exitFail    = new ExitNode("exit_fail",    ExitState.Status.FAILURE, "weather is not hot");

        // ── TRUE branch: set_variable → exit(success) ─────────────────────────
        SetVariableNode setDestination = new SetVariableNode("set_destination", "destination", "beach");
        setDestination.setOnTrue(exitSuccess);

        // ── FALSE branch: print → exit(fail) ──────────────────────────────────
        PrintNode printTooCold = new PrintNode("print_too_cold", "too cold");
        printTooCold.setOnTrue(exitFail);

        // ── Condition node ─────────────────────────────────────────────────────
        IfEqualsNode ifEquals = new IfEqualsNode("if_equals_hot", "$weather", "hot");
        ifEquals.setOnTrue(setDestination);
        ifEquals.setOnFalse(printTooCold);

        // ── Entry node: call_service (mocked) ─────────────────────────────────
        CallServiceNode callService = new CallServiceNode(
            "call_weather",
            "weather.com/forecast",
            "weather",
            url -> {
                // Mock: ignore the real URL, return our scripted response
                System.out.println("  [mock] HTTP 200 ← " + url);
                return mockedWeather;
            }
        );
        callService.setOnTrue(ifEquals);

        return callService;
    }

    /**
     * Same workflow wired with a mock that simulates a 503 error.
     */
    private static WorkflowNode buildWeatherWorkflowWithError() {

        ExitNode exitSuccess = new ExitNode("exit_success", ExitState.Status.SUCCESS);
        ExitNode exitFail    = new ExitNode("exit_fail",    ExitState.Status.FAILURE, "weather is not hot");

        SetVariableNode setDestination = new SetVariableNode("set_destination", "destination", "beach");
        setDestination.setOnTrue(exitSuccess);

        PrintNode printTooCold = new PrintNode("print_too_cold", "too cold");
        printTooCold.setOnTrue(exitFail);

        IfEqualsNode ifEquals = new IfEqualsNode("if_equals_hot", "$weather", "hot");
        ifEquals.setOnTrue(setDestination);
        ifEquals.setOnFalse(printTooCold);

        CallServiceNode callService = new CallServiceNode(
            "call_weather",
            "weather.com/forecast",
            "weather",
            url -> {
                System.out.println("  [mock] HTTP 503 ← " + url);
                // Non-2xx → exception → executor converts to exit(failure)
                throw new com.workflow.model.WorkflowRuntimeException(
                    "call_service failed: HTTP 503 Service Unavailable from " + url);
            }
        );
        callService.setOnTrue(ifEquals);

        return callService;
    }
}
