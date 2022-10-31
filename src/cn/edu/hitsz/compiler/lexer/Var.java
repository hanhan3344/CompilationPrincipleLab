package cn.edu.hitsz.compiler.lexer;

public class Var {
    public String id;
    public String value;

    public Var(String id, String value){
        this.id = id;
        this.value = value;
    }

    @Override
    public String toString() {
        return "(%s,%s)".formatted(id, value);
    }

}
