package com.alexchernikov;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class FirstMain {
    public static int N = 3;
    public static int M = 3;
    public static Set<int[]> exposed = new HashSet<>();
    public static void main(String[] args) {
        int n = 3;
        int m = 3;

        int[][] board = {
                {0,0,0},
                {0,0,0},
                {0,0,0},
        };
        processCells(board, 0, 0);
    }

        public static void processCells(/*Cell*/ int[][] board, int r, int c) {
            Queue<int[]> queue = new LinkedList<>();
            queue.add(new int[]{r,c}); // 0 , 0
            board[r][c] = 0;

            while (!queue.isEmpty()) {
                int[] curr = queue.poll(); // [0,1]
                //int dr = 1;// -1 , 0 1
                //int dc = 1;
                // for(/*each neighboor*/) {
                /*
(-1,-1)  (-1,0)  (-1,+1)
( 0,-1)    CELL  ( 0,+1)
(+1,-1)  (+1,0)  (+1,+1)
                 */
                for(int dr = -1; dr <= 1; dr++) {
                    for (int dc = -1; dc <= 1; dc++) {

                        if (dr == 0 && dc == 0) {
                            continue; //Skip our cell
                        }

                        int nr = curr[0] + dr;
                        int nc = curr[1] + dc;
                        int[] currentCell = new int[]{nr, nc};
                        if (isValidCell(nr, nc) && board[nr][nc] == 0 && !exposed.contains(currentCell)) {
                            queue.add(currentCell); // [0,1] , [1,1] , [1,0]
                            board[nr][nc] = 0;
                            exposed.add(currentCell);
                        }
                    }
                }
            }
        }

        public static boolean isValidCell(int r, int c) {

        return (r >= 0 && r < N) && (c >= 0 && c < M);
        }
}