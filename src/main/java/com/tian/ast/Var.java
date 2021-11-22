package com.tian.ast;

public class Var extends Node{
    public String name;

    public Var(String name, int line, int col) {
        this.name = name;
        this.line = line;
        this.col = col;
    }
}
