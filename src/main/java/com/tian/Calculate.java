package com.tian;

import com.tian.ast.*;
import com.tian.compile.Lexer;
import com.tian.compile.TokenKind;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Calculate {

    private Map<Integer, Map<String, Double>> scope;

    public Calculate() {
        Map<String, Double> variable = new HashMap<>();
        scope = new HashMap<>();
        scope.put(0, variable);
    }

    public void exec(String text, String name) {
        Lexer lexer = new Lexer(text, name);
        parse(lexer);
    }

    public void parse(Lexer lexer) {
        while (lexer.lookAhead() != null) {
            Node node = lexer.lookAhead();
            if (node instanceof Var) {
                parseNameStat(lexer);
            } else if (node instanceof Token) {
                if (((Token) node).kind == TokenKind.TOKEN_LBRACE) {
                    parseBlockStat(lexer);
                } else {
                    break;
                }
            } else if (node instanceof Name) {
                assignStat(lexer);
            } else if (node instanceof Identifier) {
                if (((Identifier) node).idx.equals("print")) {
                    lexer.nextToken();
                    Double ret = level1(lexer);
                    System.out.println(String.valueOf(ret));
                }
            }
        }
    }

    public void parseBlockStat(Lexer lexer) {
        lexer.nextToken();
        Map<String, Double> variable = new HashMap<>();
        scope.put(scope.size(), variable);
        parse(lexer);
        lexer.nextToken();
        scope.remove(scope.size()-1);
    }

    public void assignStat(Lexer lexer) {
        Node node = lexer.nextToken();
        String var = ((Name) node).value;
        ArrayList<String> varList = new ArrayList<>();
        varList.add(var);
        while (lexer.lookAhead() instanceof Binop && ((Binop) lexer.lookAhead()).kind == TokenKind.TOKEN_OP_COMM) {
            lexer.nextToken();
            varList.add(((Name) lexer.nextToken()).value);
        }
        node = lexer.lookAhead();
        if (node instanceof Binop) {
            TokenKind kind = ((Binop) node).kind;
            if (kind == TokenKind.TOKEN_OP_ASSIGN) {
                lexer.nextToken();
                ArrayList<Double> valList = new ArrayList<>();
                valList.add(level1(lexer));
                while (lexer.lookAhead() instanceof Binop && ((Binop) lexer.lookAhead()).kind == TokenKind.TOKEN_OP_COMM) {
                    lexer.nextToken();
                    valList.add(level1(lexer));
                }
                for (int i = 0; i < valList.size(); i++) {
                    Map<String, Double> variable = scope.get(scope.size()-1);
                    variable.put(varList.get(i), valList.get(i));
                }
            }
        }
    }

    public void parseNameStat(Lexer lexer) {
        Node node = lexer.nextToken();
        if (node instanceof Var) {
            String name = ((Var) node).name;
            Double val = 0.0;
            if (lexer.lookAhead() instanceof Binop
                    && ((Binop) lexer.lookAhead()).kind == TokenKind.TOKEN_OP_ASSIGN
            ) {
                lexer.nextToken();
                val = level1(lexer);
            }
            Map<String, Double> variable = scope.get(scope.size()-1);
            variable.put(name, val);
        }
    }

    public static void main(String[] args) {
        String file = "script/hello";
        try {
            String text = new String(Files.readAllBytes(Paths.get(file)));
            Calculate calculate = new Calculate();
            calculate.exec(text, args[0]);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public Double level1(Lexer lexer) {
        Double left = level2(lexer);
        Node opNode = lexer.lookAhead();
        while (opNode instanceof Binop) {
            TokenKind opKind = ((Binop) opNode).kind;
            if (opKind == TokenKind.TOKEN_OP_ADD) {
                lexer.nextToken();
                Double right = lexer.lookAhead() instanceof Parent ? level3(lexer) : getValue(lexer.nextToken());
                left =  left + right;
            }else if (opKind == TokenKind.TOKEN_OP_SUB) {
                lexer.nextToken();
                Double right = lexer.lookAhead() instanceof Parent ? level3(lexer) : getValue(lexer.nextToken());
                left = left - right;
            } else {
                return left;
            }
            opNode = lexer.lookAhead();
        }
        return left;
    }

    public Double level2(Lexer lexer) {
        Double left;
        if (lexer.lookAhead() instanceof Parent) {
            left = level3(lexer);
        } else {
            left = getValue(lexer.nextToken());
        }
        Node opNode = lexer.lookAhead();
        while (opNode instanceof Binop) {
            TokenKind opKind = ((Binop) opNode).kind;
            if (opKind == TokenKind.TOKEN_OP_MUL) {
                lexer.nextToken();
                Double right = lexer.lookAhead() instanceof Parent ? level3(lexer) : getValue(lexer.nextToken());
                left = left * right;
            } else if (opKind == TokenKind.TOKEN_OP_DIV) {
                lexer.nextToken();
                Double right = lexer.lookAhead() instanceof Parent ? level3(lexer) : getValue(lexer.nextToken());
                left = left / right;
            } else {
                return left;
            }
            opNode = lexer.lookAhead();
        }

        return left;
    }

    public Double level3(Lexer lexer) {
        Node node = lexer.nextToken();
        if (node instanceof Parent && ((Parent) node).isStart) {
            Double ret = level1(lexer);
            Node rParent = lexer.nextToken();
            if (rParent instanceof Parent && !((Parent) rParent).isStart) {
                return ret;
            } else {
                throw new RuntimeException("error parent, line:" + lexer.line() + ", col:" + lexer.col());
            }

        }
        throw new RuntimeException("22error parent, line:" + lexer.line() + ", col:" + lexer.col());
    }


    public Double getValue(Node node) {
        Double v = null;
        if (node instanceof Int) {
            v = (double) ((Int) node).value;
        } else if (node instanceof InternalDoubleM) {
            v = ((InternalDoubleM) node).value;
        } else if (node instanceof Name) {
            for (int i = scope.size() - 1; i >= 0; i--) {
                if (scope.get(i).containsKey(((Name) node).value)) {
                    return scope.get(i).get(((Name) node).value);
                }
            }
        } else {
            return null;
        }

        return v;
    }
}
