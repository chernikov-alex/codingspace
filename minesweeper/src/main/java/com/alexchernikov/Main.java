package com.alexchernikov;

import java.util.LinkedList;
import java.util.Queue;

public class Main {

    public static void main(String[] args) {

        Cell[][] board = {
                {
                        new Cell(0, 0, 0),
                        new Cell(0, 1, 0),
                        new Cell(0, 2, 1)
                },
                {
                        new Cell(1, 0, 0),
                        new Cell(1, 1, 1),
                        new Cell(1, 2, 1)
                },
                {
                        new Cell(2, 0, 0),
                        new Cell(2, 1, 1),
                        new Cell(2, 2, -1)
                }
        };

        System.out.println("Initial board:");
        printBoard(board);

        System.out.println();
        System.out.println("Starting flood fill from (0,0)");
        System.out.println("--------------------------------");

        processCells(board, 0, 0);

        System.out.println();
        System.out.println("Final board:");
        printBoard(board);
    }

    public static void processCells(Cell[][] board, int r, int c) {

        Queue<Cell> queue = new LinkedList<>();

        Cell start = board[r][c];

        start.expose();
        queue.add(start);

        System.out.println("Expose start cell: " + position(start));
        System.out.println("Queue: " + queueDescription(queue));

        while (!queue.isEmpty()) {

            Cell current = queue.poll();

            System.out.println();
            System.out.println("Processing cell: " + position(current)
                    + " counter=" + current.getCounter());

            // Iterate over all 8 possible neighbors
            for (int dr = -1; dr <= 1; dr++) {
                for (int dc = -1; dc <= 1; dc++) {

                    // Skip the current cell itself
                    if (dr == 0 && dc == 0) {
                        continue;
                    }

                    int nr = current.getR() + dr;
                    int nc = current.getC() + dc;

                    System.out.println(
                            "  Checking neighbor (" + nr + "," + nc + ")"
                    );

                    if (!isValidCell(board, nr, nc)) {
                        System.out.println("    -> outside board");
                        continue;
                    }

                    Cell neighbor = board[nr][nc];

                    if (neighbor.isExposed()) {
                        System.out.println("    -> already exposed");
                        continue;
                    }

                    if (neighbor.isBomb()) {
                        System.out.println("    -> bomb, skip");
                        continue;
                    }

                    neighbor.expose();

                    System.out.println(
                            "    -> expose " + position(neighbor)
                                    + ", counter=" + neighbor.getCounter()
                    );

                    if (neighbor.getCounter() == 0) {
                        queue.add(neighbor);

                        System.out.println(
                                "    -> counter is 0, add to queue"
                        );
                    } else {
                        System.out.println(
                                "    -> counter > 0, don't continue from here"
                        );
                    }
                }
            }

            System.out.println();
            System.out.println("Board after processing "
                    + position(current) + ":");
            printBoard(board);

            System.out.println("Queue: " + queueDescription(queue));
        }
    }

    public static boolean isValidCell(Cell[][] board, int r, int c) {
        return r >= 0
                && r < board.length
                && c >= 0
                && c < board[0].length;
    }

    private static String position(Cell cell) {
        return "(" + cell.getR() + "," + cell.getC() + ")";
    }

    private static String queueDescription(Queue<Cell> queue) {
        StringBuilder result = new StringBuilder("[");
        boolean first = true;

        for (Cell cell : queue) {
            if (!first) {
                result.append(", ");
            }

            result.append(position(cell));
            first = false;
        }

        result.append("]");
        return result.toString();
    }

    private static void printBoard(Cell[][] board) {

        for (Cell[] row : board) {
            for (Cell cell : row) {
                System.out.print(cell + " ");
            }
            System.out.println();
        }
    }
}