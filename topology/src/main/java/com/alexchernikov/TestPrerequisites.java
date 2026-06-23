package com.alexchernikov;

import java.util.*;

public class TestPrerequisites {
    public static void main(String[] args) {
        test();
    }
    private static void test() {
        int n = 6;
        ArrayList<ArrayList<Integer>> prerequisites3 = new ArrayList<>(Arrays.asList(
                new ArrayList<>(Arrays.asList(0, 1)),
                new ArrayList<>(Arrays.asList(0, 2)),
                new ArrayList<>(Arrays.asList(3, 2)),
                new ArrayList<>(Arrays.asList(1, 4)),
                new ArrayList<>(Arrays.asList(2, 4)),
                new ArrayList<>(Arrays.asList(4, 5))
        ));
        boolean result = prerequisites(n, prerequisites3);
        System.out.println("Can all courses be enrolled? " + result); // Expected: True
    }

    public static boolean prerequisites(int n, ArrayList<ArrayList<Integer>> prerequisites) {
        //giq
        Map<Integer, List<Integer>> graph = new HashMap();
        int[] inDegrees = new int[n];
        // Represent the graph as an adjacency list and record the in-degree of each course.
        for (ArrayList<Integer> pair : prerequisites) {
           int prerequisite = pair.get(0);
           int course = pair.get(1);
           graph.computeIfAbsent(prerequisite, k->new ArrayList<>()).add(course);
           inDegrees[course]++;
        }

        // Initialize queue
        Deque<Integer> queue = new LinkedList<>();
        // Add all courses with an in-degree of 0 to the queue.
        for(int i = 0; i < n; i++) {
            if(inDegrees[i] == 0) {
                queue.add(i);
            }
        }

        int enrolledCourses = 0;
        List<Integer> executionOrder = new ArrayList<>();
        while(!queue.isEmpty()) {
            int node = queue.poll();
            executionOrder.add(node);
            enrolledCourses++;

            if(graph.containsKey(node)) {
                List<Integer> neighbors = graph.get(node);
                for(int neighbor : neighbors) {
                    inDegrees[neighbor]--;
                    // If the indegree of course become 0 due to completion of prerequisite, add it to the queue
                    if(inDegrees[neighbor] == 0) {
                        queue.add(neighbor);
                    }
                }
            }
        }
        if (enrolledCourses == n) {
            System.out.println("Order of courses: " + executionOrder);
        }

        return enrolledCourses == n;
    }
}
