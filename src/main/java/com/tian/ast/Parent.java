package com.tian.ast;

public class Parent extends Node {
    public boolean isStart;

    public Parent(boolean isStart, int line, int col) {
        this.isStart = isStart;
    }
}
