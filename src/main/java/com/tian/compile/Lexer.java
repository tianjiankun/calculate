package com.tian.compile;

import com.tian.ast.*;
import static com.tian.compile.TokenKind.*;
import java.lang.Double;

public class Lexer {

    private String text;
    private String name;
    private int offest;
    private int line;
    private int col;

    private Node currNode;
    private int currLine;
    private int currCol;

    public Lexer(String text, String name) {
        this.text = text;
        this.name = name;
        this.offest = 0;
        this.line = 1;
        this.col = 1;
    }

    public int line() {
        return currLine != 0 ? currLine : line;
    }

    public int col() {
        return currCol != 0 ? currCol : col;
    }

    public Node lookAhead() {
        if (currNode == null) {
            currLine = line;
            currCol = col;
            currNode = nextToken();
        }
        return currNode;
    }

    public Node nextToken() {

        if (currNode != null) {
            Node node =  currNode;
            currNode = null;
            currLine = 0;
            currCol = 0;
            return node;
        }

        skipWhiteSpaces();

        if (text.length() <= offest) {
            return null;
        }

        if (Character.isDigit(text.charAt(offest))) {
            return scanNumber();
        }

        if (text.startsWith("var", offest)) {
            offest += 3;
            skipWhiteSpaces();
            String name = scanName();
            return new Var(name, line, col);
        }

        if (text.startsWith("print", offest)) {
            offest += 5;
            return new Identifier("print");
        }

        if (Character.isLetter(text.charAt(offest))) {
            String name = scanName();
            return new Name(name, line(), col());
        }

        switch (text.charAt(offest)) {
            case '+':
                forward();
                return new Binop(TOKEN_OP_ADD, line, col);
            case '-':
                forward();
                return new Binop(TOKEN_OP_SUB, line, col);
            case '*':
                forward();
                return new Binop(TOKEN_OP_MUL, line, col);
            case '/':
                forward();
                return new Binop(TOKEN_OP_DIV, line, col);
            case '(':
                forward();
                return new Parent(true, line, col);
            case ')':
                forward();
                return new Parent(false, line, col);
            case '=':
                forward();
                return new Binop(TOKEN_OP_ASSIGN, line, col);
            case ',':
                forward();
                return new Binop(TOKEN_OP_COMM, line, col);
            case '{':
                forward();
                return new Token(TOKEN_LBRACE);
            case '}':
                forward();
                return new Token(TOKEN_RBRACE);
        }

        throw new RuntimeException("error lexer!");
    }

    private String scanName() {
        int start = offest;
        while (offest < text.length()) {
            offest++;
            if (Character.isWhitespace(text.charAt(offest)) || text.charAt(offest) == ')' || text.charAt(offest) == ',') {
                break;
            }
        }
        return text.substring(start, offest);
    }

    private Node scanNumber() {
        int start = offest;
        boolean isInt = true;
        while (offest < text.length()) {
            if (Character.isDigit(text.charAt(offest))) {
                forward();
            } else if (text.charAt(offest) == '.') {
                forward();
                isInt = false;
            } else {
                break;
            }
        }

        if (isInt) {
            int val = Integer.valueOf(text.substring(start, offest));
            return new Int(val, line, col);
        } else {
            Double val = Double.valueOf(text.substring(start, offest));
            return new InternalDoubleM(val, line, col);
        }
    }

    private void skipWhiteSpaces() {
        while (offest < text.length()) {
            if (Character.isWhitespace(text.charAt(offest))) {
               forward();
            } else if (text.startsWith("//", offest)) {
                forward();
            }else {
                break;
            }
        }
    }

    private void forward() {
        if (text.startsWith("\n\r", offest) || text.startsWith("\r\n")) {
            offest += 2;
            col = 0;
            line++;
        } else if (text.charAt(offest) == '\n' || text.charAt(offest) == '\r') {
            offest ++;
            col = 0;
            line++;
        } else {
            offest++;
            col++;
        }
    }
}
