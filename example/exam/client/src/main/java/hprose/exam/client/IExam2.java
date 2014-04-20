package hprose.exam.client;

import hprose.common.SimpleMode;

public interface IExam2 {
    @SimpleMode(true)
    User[] getUserList();
}
