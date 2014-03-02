package hprose.exam;

import hprose.common.SimpleMode;

public interface IExam2 {
    @SimpleMode(true)
    User[] getUserList();
}
