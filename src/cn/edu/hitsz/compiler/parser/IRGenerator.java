package cn.edu.hitsz.compiler.parser;

import cn.edu.hitsz.compiler.NotImplementedException;
import cn.edu.hitsz.compiler.ir.IRImmediate;
import cn.edu.hitsz.compiler.ir.IRValue;
import cn.edu.hitsz.compiler.ir.IRVariable;
import cn.edu.hitsz.compiler.ir.Instruction;
import cn.edu.hitsz.compiler.lexer.Token;
import cn.edu.hitsz.compiler.lexer.TokenKind;
import cn.edu.hitsz.compiler.lexer.Var;
import cn.edu.hitsz.compiler.parser.table.Production;
import cn.edu.hitsz.compiler.parser.table.Status;
import cn.edu.hitsz.compiler.symtab.SymbolTable;
import cn.edu.hitsz.compiler.utils.FileUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

// TODO: 实验三: 实现 IR 生成

/**
 *
 */
public class IRGenerator implements ActionObserver {

//    private Stack<Token> tokenStack = new Stack<Token>();
    private Stack<Var> varStack = new Stack<Var>();
    private ArrayList<Instruction> instructions = new ArrayList<Instruction>();
    private SymbolTable symbolTable;
    @Override
    public void whenShift(Status currentStatus, Token currentToken) {
        // TODO
//        throw new NotImplementedException();
//        System.out.println("IR shift");
//        System.out.println(currentToken);
        if (currentToken.getKindId().equals("Semicolon")){
            while(!varStack.empty()){
                varStack.pop();
            }
        } else if (currentToken.getKindId().equals("(")) {

        } else {
            varStack.push(new Var(currentToken.getKindId(), currentToken.getText()));
        }
    }

    public boolean isNumeric(String str){
        for (int i = str.length();--i>=0;){
            if (!Character.isDigit(str.charAt(i))){
                return false;
            }
        }
        return true;
    }

    private IRValue autoIR(String hs){
        IRValue res = null;

        if (isNumeric(hs)) {
            res = IRImmediate.of(Integer.parseInt(hs));
        }else{
            res = IRVariable.named(hs);
        }

        return res;
    }

    @Override
    public void whenReduce(Status currentStatus, Production production) {
        // TODO
//        throw new NotImplementedException();
//        System.out.println("IR reduce " + production);
        switch (production.index()){
            case 1 -> { //P->S_list

            }
            case 2 -> { //S_list -> S Semicolon S_list;

            }
            case 3 -> { //S_list -> S Semicolon;

            }
            case 4 -> { //S -> D id;
//                Token topToken = tokenStack.pop();
//                String id = topToken.getText();
//                topToken = tokenStack.pop();
//                Token newToken = Token.normal(topToken.getKind(), id);
//                tokenStack.push(newToken);
//                System.out.println("new Token = "+newToken);
            }
            case 5 -> { //D -> int;
//                Token topToken = tokenStack.pop();
//                Token newToken = Token.normal(topToken.getKind(), "D");
//                tokenStack.push(newToken);
            }
            case 6 -> { //S -> id = E;
                Var topVar = varStack.pop();
                String value = topVar.value;
                topVar = varStack.pop();
                topVar = varStack.pop();
                Var newVar = new Var(topVar.value, value);
//                System.out.println("NEWVAR = "+newVar.id + newVar.value);
                Instruction ins = Instruction.createMov(IRVariable.named(newVar.id), autoIR(newVar.value));
//                System.out.println(ins);
                instructions.add(ins);
            }
            case 7 -> { //S -> return E;
                Var topVar = varStack.pop();
                Instruction ins = Instruction.createRet(autoIR(topVar.value));
                instructions.add(ins);
            }
            case 8 -> { //E -> E + A;
                Var topVar = varStack.pop();
                String A_value = topVar.value;
                varStack.pop();
                topVar = varStack.pop();
                String E_value = topVar.value;
                IRVariable temp = IRVariable.temp();
                Var newVar = new Var("E", temp.getName());
                varStack.push(newVar);
                Instruction ins = Instruction.createAdd(temp, autoIR(E_value), autoIR(A_value));
                instructions.add(ins);
            }
            case 9 -> { //E -> E - A;
                Var topVar = varStack.pop();
//                System.out.println("01 = "+topVar);
                String A_value = topVar.value;
                topVar = varStack.pop();
//                System.out.println("02 = "+topVar);
                topVar = varStack.pop();
//                System.out.println("03 = "+topVar);
                String E_value = topVar.value;
//                System.out.println("01 = "+topVar);
//                System.out.println("E=E-A = ");
                IRVariable temp = IRVariable.temp();
                Var newVar = new Var("E", temp.getName());
                varStack.push(newVar);
                Instruction ins = Instruction.createSub(temp, autoIR(E_value), autoIR(A_value));
                instructions.add(ins);
            }
            case 10 -> { //E -> A;
                Var topVar = varStack.pop();
                Var newVar = new Var("E", topVar.value);
                varStack.push(newVar);
            }
            case 11 -> { //A -> A * B;
                Var topVar = varStack.pop();
                String B_value = topVar.value;
                varStack.pop();
                topVar = varStack.pop();
                String A_value = topVar.value;
                IRVariable temp = IRVariable.temp();
                Var newVar = new Var("A", temp.getName());
                varStack.push(newVar);
                Instruction ins = Instruction.createMul(temp, autoIR(A_value), autoIR(B_value));
                instructions.add(ins);
            }
            case 12 -> { //A -> B;
                Var topVar = varStack.pop();
                Var newVar = new Var("A", topVar.value);
                varStack.push(newVar);
            }
            case 13 -> { //B -> ( E );
                varStack.pop(); // ")"
                Var topVar = varStack.pop();
                Var newVar = new Var("B", topVar.value);
                varStack.push(newVar);
            }
            case 14 -> { //B -> id;
                Var topVar = varStack.pop();
//                System.out.println("B->id = "+topVar);
                Var newVar = new Var("B", topVar.value);
                varStack.push(newVar);
            }
            case 15 -> { //B -> IntConst;
                Var topVar = varStack.pop();
                Var newVar = new Var("B", topVar.value);
                varStack.push(newVar);
            }
        }
    }


    @Override
    public void whenAccept(Status currentStatus) {
        // TODO
//        throw new NotImplementedException();
//        System.out.println("IR accept");
    }

    @Override
    public void setSymbolTable(SymbolTable table) {
        // TODO
//        throw new NotImplementedException();
        symbolTable = table;
    }

    public List<Instruction> getIR() {
        // TODO
//        throw new NotImplementedException();
        return instructions;
    }

    public void dumpIR(String path) {
        FileUtils.writeLines(path, getIR().stream().map(Instruction::toString).toList());
    }
}

