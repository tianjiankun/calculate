package com.tian.ast;

public class Name extends Node {
    public String value;

    public Name(String val, int line, int col) {
        this.value = val;
        this.line = line;
        this.col = col;
    }
}
