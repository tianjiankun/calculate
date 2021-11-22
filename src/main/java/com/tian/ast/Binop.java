package com.tian.ast;

import com.tian.compile.TokenKind;

public class Binop extends Node{

    public TokenKind kind;

    public Binop(TokenKind kind, int line, int col) {
        this.kind = kind;
        this.line = line;
        this.col = col;
    }

    @Override
    public String toString() {
        return kind.toString();
    }
}
