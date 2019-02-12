package com.single.code.tool.DesignPatterns.visitor;

import android.util.Log;

/**
 * Created by czf on 2019/2/1.
 */

public class VisitorCEO implements Visitor {

    @Override
    public void visit(ManagerCeo managerCeo) {
        String name = managerCeo.getName();
    }

    @Override
    public void visit(ManagerCto managerCto) {
        String name = managerCto.getName();
    }

    @Override
    public void visit(Engenier engenier) {
        String name = engenier.getName();
    }
}
