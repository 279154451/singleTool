package com.single.code.tool.DesignPatterns.visitor;

/**
 * Created by czf on 2019/2/1.
 */

public class Engenier implements Staff {
    @Override
    public String getName() {
        return "engenier";
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
