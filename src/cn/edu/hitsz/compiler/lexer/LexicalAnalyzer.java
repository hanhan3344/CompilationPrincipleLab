package cn.edu.hitsz.compiler.lexer;

import cn.edu.hitsz.compiler.symtab.SymbolTable;
import cn.edu.hitsz.compiler.utils.FileUtils;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.stream.StreamSupport;

/**
 * TODO: 实验一: 实现词法分析
 * <br>
 * 你可能需要参考的框架代码如下:
 *
 * @see Token 词法单元的实现
 * @see TokenKind 词法单元类型的实现
 */
public class LexicalAnalyzer {
    private final SymbolTable symbolTable;
    private char[] inputArray;

    private final ArrayList<Token> tokens = new ArrayList<>();

    public ArrayList<String> result = new ArrayList<>();
    public LexicalAnalyzer(SymbolTable symbolTable) {
        this.symbolTable = symbolTable;
    }


    /**
     * 从给予的路径中读取并加载文件内容
     *
     * @param path 路径
     */
    public void loadFile(String path) throws FileNotFoundException {
        // TODO: 词法分析前的缓冲区实现
        // 可自由实现各类缓冲区
        // 或直接采用完整读入方法
        Scanner s = new Scanner(new File(path));
        String content = s.useDelimiter("\\A").next();
        s.close();
        inputArray = content.toCharArray();
    }

    /**
     * 执行词法分析, 准备好用于返回的 token 列表 <br>
     * 需要维护实验一所需的符号表条目, 而得在语法分析中才能确定的符号表条目的成员可以先设置为 null
     */
    public void run() {
        // TODO: 自动机实现的词法分析过程
        int i = 0, j = 0;
        while (i < inputArray.length){
            j = i;
            i = DFA(i, j, 0, Boolean.FALSE);
        }
        tokens.add(Token.simple(TokenKind.eof()));
    }

    private int DFA(int i, int j, int state, Boolean is_finished) {
        int next_begin = -1;
        if (is_finished) {
            String string = new String(inputArray, j, i - j);
            switch (state) {
                case 15 -> {
                    if (TokenKind.isAllowed(string)) {
                        tokens.add(Token.simple(TokenKind.fromString(string)));
                    }else {
                        tokens.add(Token.normal("id", string));
                        if (!symbolTable.has(string)) {
                            symbolTable.add(string);
                        }
                    }
                }
                case 17 -> tokens.add(Token.normal("IntConst", string));
                case 19, 20, 22, 23, 26, 27, 28, 30, 29, 31, 32 -> tokens.add(Token.simple(TokenKind.fromString(string)));
                case 25 -> tokens.add(Token.normal("STR_CONST", string));
                case 33 -> tokens.add(Token.simple(TokenKind.fromString("Semicolon")));
            }
            next_begin = i;
        } else {
            switch (state) {
                case 0 -> {
                    switch (inputArray[i]) {
                        case 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z' -> {
                            next_begin = DFA(i + 1, j, 14, Boolean.FALSE);
                        }
                        case '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' ->
                                next_begin = DFA(i + 1, j, 16, Boolean.FALSE);
                        case '*' -> next_begin = DFA(i + 1, j, 18, Boolean.FALSE);
                        case '=' -> next_begin = DFA(i + 1, j, 21, Boolean.FALSE);
                        case '"' -> next_begin = DFA(i + 1, j, 24, Boolean.FALSE);
                        case '(' -> next_begin = DFA(i + 1, j, 26, Boolean.TRUE);
                        case ')' -> next_begin = DFA(i + 1, j, 27, Boolean.TRUE);
                        case ':' -> next_begin = DFA(i + 1, j, 28, Boolean.TRUE);
                        case '+' -> next_begin = DFA(i + 1, j, 29, Boolean.TRUE);
                        case '-' -> next_begin = DFA(i + 1, j, 30, Boolean.TRUE);
                        case '/' -> next_begin = DFA(i + 1, j, 31, Boolean.TRUE);
                        case '\'' -> next_begin = DFA(i + 1, j, 32, Boolean.TRUE);
                        case ';' -> next_begin = DFA(i + 1, j, 33, Boolean.TRUE);
                        default -> {
                            return i + 1;
                        }
                    }
                }
                case 14 -> {
                    switch (inputArray[i]) {
                        case 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' -> {
                            next_begin = DFA(i + 1, j, 14, Boolean.FALSE);
                        }
                        default -> {
                            next_begin = DFA(i, j, 15, Boolean.TRUE);
                        }
                    }
                }
                case 16 -> {
                    switch (inputArray[i]) {
                        case '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' ->
                                next_begin = DFA(i + 1, j, 16, Boolean.FALSE);
                        default -> next_begin = DFA(i, j, 17, Boolean.TRUE);
                    }
                }
                case 18 -> {
                    if (inputArray[i] == '*') {
                        next_begin = DFA(i + 1, j, 19, Boolean.TRUE);
                    } else {
                        next_begin = DFA(i, j, 20, Boolean.TRUE);
                    }
                }
                case 21 -> {
                    if (inputArray[i] == '=') {
                        next_begin = DFA(i + 1, j, 22, Boolean.TRUE);
                    } else {
                        next_begin = DFA(i, j, 23, Boolean.TRUE);
                    }
                }
                case 24 -> {
                    switch (inputArray[i]) {
                        case 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' ->
                                next_begin = DFA(i + 1, j, 24, Boolean.FALSE);
                        case '"' -> next_begin = DFA(i + 1, j, 25, Boolean.TRUE);
                    }
                }
            }
        }

        return next_begin;
    }

    /**
     * 获得词法分析的结果, 保证在调用了 run 方法之后调用
     *
     * @return Token 列表
     */
    public Iterable<Token> getTokens() {
        // TODO: 从词法分析过程中获取 Token 列表
        // 词法分析过程可以使用 Stream 或 Iterator 实现按需分析
        // 亦可以直接分析完整个文件
        // 总之实现过程能转化为一列表即可
        return tokens;
    }

    public void dumpTokens(String path) {
        FileUtils.writeLines(
            path,
            StreamSupport.stream(getTokens().spliterator(), false).map(Token::toString).toList()
        );
    }


}
