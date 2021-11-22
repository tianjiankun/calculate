package com.tian.ast;

public class Int extends Node{
    public int value;

    public Int(int val, int line, int col) {
        this.value = val;
        this.line = line;
        this.col = col;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
