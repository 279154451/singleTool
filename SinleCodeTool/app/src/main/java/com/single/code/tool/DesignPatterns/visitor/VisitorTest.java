package com.single.code.tool.DesignPatterns.visitor;

/**
 * Created by czf on 2019/2/1.
 */

public class VisitorTest {

    public void test(){
        ManagerCto cto = new ManagerCto();
        ManagerCeo ceo = new ManagerCeo();
        Engenier engenier = new Engenier();
        VisitorCTO visitorCTO = new VisitorCTO();
        VisitorCEO visitorCEO = new VisitorCEO();
        cto.accept(visitorCEO);
        ceo.accept(visitorCTO);
        engenier.accept(visitorCEO);
        engenier.accept(visitorCTO);

    }
}
