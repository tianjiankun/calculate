package com.tian.ast;

import com.tian.compile.TokenKind;

public class Token extends Node {
    public TokenKind kind;

    public Token(TokenKind kind) {
        this.kind = kind;
    }
}
