package com.alexchernikov;

public class Cell {
    private boolean exposed;
    private final int counter;
    private final int r;
    private final int c;

    public Cell(int r, int c, int counter) {
        this.r = r;
        this.c = c;
        this.counter = counter;
    }

    public boolean isBomb() {
        return counter == -1; // < 0
    }

    public boolean isExposed() {
        return exposed;
    }

    public void expose() {
        exposed = true;
    }

    public int getR() {
        return r;
    }

    public int getC() {
        return c;
    }

    public int getCounter() {
        return counter;
    }

    @Override
    public String toString() {
        if (!exposed) {
            return "#";
        }

        if (isBomb()) {
            return "*";
        }

        return String.valueOf(counter);
    }
}
