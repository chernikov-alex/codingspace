package com.alexchernikov;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
    // int [][] = //;

        // 1, 2, 3, 4

    }

    // Return number of islands

    public static int processMatrix(int [][] matrix, int n, int m ) {
        // matrix N*M
        int id = 0;
        for (int i = 0; i < n ; i++) {
            for (int j = 0; j < m ; j++) {
                if (matrix[i][j] > 0) {
                    id++;
                    processNode(matrix, i, j);
                }
            }
        }
        return id;
    }

    public static boolean processNode(int [][] matrix, int x, int y) {

        // processNode(int [][] matrix )
        return false;
    }

    /*
    OIOOOO
    IOIOII
    OOIOOI
    OOOOOO
    OIIOIO
    OOOOIO
    */
}