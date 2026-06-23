package com.alexchernikov;

public class Cell {
    private boolean exposed;
    private int counter;
    private int r;
    private int c;

    public boolean isBomb() {
        return counter == -1; // < 0
    }
}
