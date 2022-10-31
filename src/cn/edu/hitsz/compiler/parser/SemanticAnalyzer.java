package cn.edu.hitsz.compiler.parser;

import cn.edu.hitsz.compiler.lexer.Token;
import cn.edu.hitsz.compiler.parser.table.Production;
import cn.edu.hitsz.compiler.parser.table.Status;
import cn.edu.hitsz.compiler.symtab.SourceCodeType;
import cn.edu.hitsz.compiler.symtab.SymbolTable;
import cn.edu.hitsz.compiler.symtab.SymbolTableEntry;

import java.util.ArrayList;
import java.util.Stack;

// TODO: 实验三: 实现语义分析
public class SemanticAnalyzer implements ActionObserver {

    private SymbolTable symbolTable;
    private Stack<Token> tokenStack = new Stack<Token>();

    private ArrayList<SymbolTableEntry> symbolList;
    private int i = 0;
    private Stack<Production> productionStack = new Stack<Production>();

//    public SemanticAnalyzer(SymbolTable symbolTable) {
//        this.symbolTable = symbolTable;
//    }

    @Override
    public void whenAccept(Status currentStatus) {
        // TODO: 该过程在遇到 Accept 时要采取的代码动作
//        throw new NotImplementedException();
//        System.out.println("accept");
    }

    @Override
    public void whenReduce(Status currentStatus, Production production) {
        // TODO: 该过程在遇到 reduce production 时要采取的代码动作
//        throw new NotImplementedException();
//        System.out.println(production);
//        for (Term body:production.body()){
//            System.out.println(body);
//        }
//        System.out.println(production);

        switch (production.index()){
            case 4 -> { //S -> D id;
                Token topToken = tokenStack.pop();
                String id = topToken.getText();
                topToken = tokenStack.pop();
                Token newToken = Token.normal(topToken.getKind(), id);
//                tokenStack.push(newToken);
//                System.out.println("new Token = "+newToken);
                SymbolTableEntry symbolTableEntry = symbolTable.get(id);
                symbolTableEntry.setType(SourceCodeType.Int);
            }
            case 5 -> { //D -> int;
                Token topToken = tokenStack.pop();
                Token newToken = Token.normal(topToken.getKind(), "D");
                tokenStack.push(newToken);
            }
        }

//        if( production.index() == 4) {
//            symbolList = symbolTable.getSymbolList();
//            Production target = productionStack.pop();
////            System.out.println(target.head().getTermName());
////            System.out.println(production.body().get(1).getTermName());
//            String name = symbolList.get(i).getText();
//            String type = target.body().get(0).getTermName();
////            System.out.printf("(%s, %s)%n", name, type);
//            SymbolTableEntry entry = symbolTable.get(name);
//            entry.setType(SourceCodeType.Int);
//            i++;
//        }else {
//            productionStack.push(production);
//        }
    }


    @Override
    public void whenShift(Status currentStatus, Token currentToken) {
        // TODO: 该过程在遇到 shift 时要采取的代码动作
//        throw new NotImplementedException();
//        System.out.println("shift");
//        System.out.println(currentToken);
        if (currentToken.getKindId().equals("Semicolon")){
            while(!tokenStack.empty()){
                tokenStack.pop();
            }
        } else {
            tokenStack.push(currentToken);
        }

    }

    @Override
    public void setSymbolTable(SymbolTable table) {
        // TODO: 设计你可能需要的符号表存储结构
        // 如果需要使用符号表的话, 可以将它或者它的一部分信息存起来, 比如使用一个成员变量存储
//        throw new NotImplementedException();
        this.symbolTable = table;
    }
}

