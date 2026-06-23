package com.alexchernikov;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class CustomCollection {
    private static final int INITIAL_CAPACITY = 10;
    // 3 | 7 | 5 |
    // 3 |   | 5 |
    // 3 | 5 |
    // Add - O(1) / LIFO / FIFO
    // Remove - O(1)
    // Random - O(1) - Array
    private ArrayList<Integer> elements;
    private HashMap<Integer, Integer> map;

    public CustomCollection() {
        elements = new ArrayList();
    }

    public void add(Integer e) {
        this.elements.add(e);
    }

    public boolean remove(Integer e) {
        int index = map.get(e);
        Integer elem = this.elements.remove(index);
        return Objects.equals(elem, e);
    }

    public Integer getRandom() {

        int index = 9; // Math.random() * (this.elements.size() - 1);// random index;

        return this.elements.get(index);
    }




}
