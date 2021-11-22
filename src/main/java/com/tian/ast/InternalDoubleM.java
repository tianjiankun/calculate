package com.tian.ast;

public class InternalDoubleM extends Node{

    public Double value;

    public InternalDoubleM(Double val, int line, int col) {
        this.value = val;
        this.line = line;
        this.col = col;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
