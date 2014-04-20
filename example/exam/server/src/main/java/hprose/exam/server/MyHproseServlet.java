package hprose.exam.server;

import hprose.common.HproseMethods;
import hprose.server.HproseServlet;

public class MyHproseServlet extends HproseServlet {
    @Override
    protected void setGlobalMethods(HproseMethods methods) {
        Exam2 exam2 = new Exam2();
        methods.addMethod("getID", exam2);
    }
}