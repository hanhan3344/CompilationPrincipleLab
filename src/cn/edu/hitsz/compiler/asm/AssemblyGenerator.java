package cn.edu.hitsz.compiler.asm;

import cn.edu.hitsz.compiler.NotImplementedException;
import cn.edu.hitsz.compiler.ir.IRImmediate;
import cn.edu.hitsz.compiler.ir.IRValue;
import cn.edu.hitsz.compiler.ir.IRVariable;
import cn.edu.hitsz.compiler.ir.Instruction;
import cn.edu.hitsz.compiler.lexer.Token;
import cn.edu.hitsz.compiler.utils.FileUtils;

import java.nio.file.FileAlreadyExistsException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.StreamSupport;


/**
 * TODO: 实验四: 实现汇编生成
 * <br>
 * 在编译器的整体框架中, 代码生成可以称作后端, 而前面的所有工作都可称为前端.
 * <br>
 * 在前端完成的所有工作中, 都是与目标平台无关的, 而后端的工作为将前端生成的目标平台无关信息
 * 根据目标平台生成汇编代码. 前后端的分离有利于实现编译器面向不同平台生成汇编代码. 由于前后
 * 端分离的原因, 有可能前端生成的中间代码并不符合目标平台的汇编代码特点. 具体到本项目你可以
 * 尝试加入一个方法将中间代码调整为更接近 risc-v 汇编的形式, 这样会有利于汇编代码的生成.
 * <br>
 * 为保证实现上的自由, 框架中并未对后端提供基建, 在具体实现时可自行设计相关数据结构.
 *
 * @see AssemblyGenerator#run() 代码生成与寄存器分配
 */
public class AssemblyGenerator {


    private List<Instruction> instructions = new ArrayList<>();
    private ArrayList<String> result = new ArrayList<>();
    private BMap<Integer, IRVariable> map = new BMap<>();
    private int tempIRValue = 114;
    /**
     * 加载前端提供的中间代码
     * <br>
     * 视具体实现而定, 在加载中或加载后会生成一些在代码生成中会用到的信息. 如变量的引用
     * 信息. 这些信息可以通过简单的映射维护, 或者自行增加记录信息的数据结构.
     *
     * @param originInstructions 前端提供的中间代码
     */
    public void loadIR(List<Instruction> originInstructions) {
        // TODO: 读入前端提供的中间代码并生成所需要的信息
//        throw new NotImplementedException();
        this.instructions = originInstructions;
    }


    /**
     * 执行代码生成.
     * <br>
     * 根据理论课的做法, 在代码生成时同时完成寄存器分配的工作. 若你觉得这样的做法不好,
     * 也可以将寄存器分配和代码生成分开进行.
     * <br>
     * 提示: 寄存器分配中需要的信息较多, 关于全局的与代码生成过程无关的信息建议在代码生
     * 成前完成建立, 与代码生成的过程相关的信息可自行设计数据结构进行记录并动态维护.
     */
    public void run() {
        // TODO: 执行寄存器分配与代码生成
//        throw new NotImplementedException();
        for (int i = 0 ; i < instructions.size() ; i++){
            Instruction ins = instructions.get(i);
            switch(ins.getKind()){
                case MOV -> {
//                    System.out.println(ins);
                    if(ins.getFrom().isImmediate()){
                        IRImmediate rhs = (IRImmediate) ins.getFrom();
                        IRVariable lhs = (IRVariable) ins.getResult();
                        int rd;
                        result.add("li t%s, %s".formatted(getReg(i, lhs), rhs.getValue()));
                    }else{
                        IRVariable rhs = (IRVariable) ins.getFrom();
                        IRVariable lhs = (IRVariable) ins.getResult();
                        int rd;
                        if(map.containsValue(lhs)){
                            rd = map.getByValue(lhs);
                        }else{
                            rd = getReg(i, lhs);
                        }
                        result.add("mv t%s, t%s".formatted(rd, map.getByValue(rhs)));
                    }
//                    System.out.println(result);
                }
                case SUB -> {
//                    System.out.println(ins);
                    if(ins.getLHS().isImmediate()) {
                        IRVariable res = ins.getResult();
                        IRImmediate lhs = (IRImmediate) ins.getLHS();
                        IRVariable rhs = (IRVariable) ins.getRHS();
                        int rs1 = getReg(i, IRVariable.named("tempIRVariable%d".formatted(tempIRValue)));
                        result.add("li t%s, %s".formatted(rs1, lhs.getValue()));
                        result.add("sub t%s, t%s, t%s".formatted(getReg(i, ins.getResult()), rs1, map.getByValue(rhs)));
//                        System.out.println(result);
                    }else if (ins.getRHS().isImmediate()){
                        IRImmediate rhs = (IRImmediate) ins.getRHS();
                        result.add("subi t%s, t%s, %s".formatted(getReg(i, ins.getResult()), getReg(i, (IRVariable) ins.getLHS()), rhs.getValue()));
                    } else {
                        result.add("sub t%s, t%s, t%s".formatted(getReg(i, ins.getResult()), getReg(i, (IRVariable) ins.getLHS()), getReg(i, (IRVariable) ins.getRHS())));
                    }
                }
                case MUL -> {
//                    System.out.println(ins);

                    result.add("mul t%s, t%s, t%s".formatted(getReg(i, ins.getResult()), getReg(i, (IRVariable) ins.getLHS()), getReg(i, (IRVariable) ins.getRHS())));
//                    System.out.println(result);
                }
                case ADD -> {
//                    System.out.println(ins);
                    if(ins.getRHS().isImmediate()){
                        IRImmediate rhs = (IRImmediate) ins.getRHS();
                        result.add("addi t%s, t%s, %s".formatted(getReg(i, ins.getResult()), getReg(i, (IRVariable) ins.getLHS()), rhs.getValue()));
                    } else if (ins.getLHS().isImmediate()) {
                        IRImmediate lhs = (IRImmediate) ins.getLHS();
                        result.add("addi t%s, t%s, %s".formatted(getReg(i, ins.getResult()), getReg(i, (IRVariable) ins.getRHS()), lhs.getValue()));
                    } else {
                        result.add("add t%s, t%s, t%s".formatted(getReg(i, ins.getResult()), getReg(i, (IRVariable) ins.getLHS()), getReg(i, (IRVariable) ins.getRHS())));
                    }
                }
                case RET -> {
                    result.add("mv a0, t%s".formatted(map.getByValue((IRVariable) ins.getReturnValue())));
                }
            }
        }
    }


    /**
     * 输出汇编代码到文件
     *
     * @param path 输出文件路径
     */
    public void dump(String path) {
        // TODO: 输出汇编代码到文件
//        throw new NotImplementedException();
//        System.out.println(result);
        FileUtils.writeLines(
                path,
                result.stream().toList()
        );
    }


    private int getReg(int ins_num, IRVariable ins_IR){
        int res = -1;
        if(map.containsValue(ins_IR)){
            return map.getByValue(ins_IR);
        }
        int i;
        boolean flag = Boolean.TRUE;
        for(i = 0 ; i < 7 ; i++){
            if(!map.containsKey(i)){
                res = i;
                flag = Boolean.FALSE;
                break;
            }
        }
        if(flag){ // 无空闲寄存器
//            System.out.println("flag = true");
            boolean total_will_use = Boolean.TRUE;
            for(i = 0 ; i < 7 && total_will_use; i++){
//                System.out.printf("now reg t%s%n", i);
                boolean will_use = Boolean.FALSE;
                IRVariable irVariable = map.getByKey(i);
                for(int j = ins_num;j<instructions.size() && !will_use;j++){

                    Instruction ins = instructions.get(j);
//                    System.out.println("now ins %s".formatted(ins));
                    switch (ins.getKind()){
                        case MOV -> {
                            if(irVariable.equals(ins.getResult()) || irVariable.equals(ins.getFrom())){
                                will_use = Boolean.TRUE;
                            }
                        }
                        case RET -> {
                            if(irVariable.equals(ins.getReturnValue())){
                                will_use = Boolean.TRUE;
                            }
                        }
                        default -> {
                            if(irVariable.equals(ins.getResult()) || irVariable.equals(ins.getLHS()) || irVariable.equals(ins.getRHS())){
                                will_use = Boolean.TRUE;
                            }
                        }
                    }
                }
                if(!will_use){
                    total_will_use = Boolean.FALSE;
                    res = i;
                }
            }
            if(total_will_use){
                System.out.println("Error!!!!!!!!!!!!!!!!!!!!!!");
                System.exit(0);
            }
        }
        if(res != -1){
            map.replace(res, ins_IR);
        }

        return res;
    }
}

