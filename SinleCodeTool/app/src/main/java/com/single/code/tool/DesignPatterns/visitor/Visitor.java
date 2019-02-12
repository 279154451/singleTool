package com.single.code.tool.DesignPatterns.visitor;

/**
 * Created by czf on 2019/2/1.
 */

public interface Visitor {
    void visit(ManagerCeo managerCeo);
    void visit(ManagerCto managerCto);
    void visit(Engenier engenier);
}
