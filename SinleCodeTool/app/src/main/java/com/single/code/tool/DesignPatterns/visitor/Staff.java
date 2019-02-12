package com.single.code.tool.DesignPatterns.visitor;

/**
 * 员工
 * Created by czf on 2019/2/1.
 */

public interface Staff {

    String getName();
    void accept(Visitor visitor);
}
