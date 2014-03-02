package hprose.exam;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class Exam2 extends Exam1 {
    public Exam2() {
        id = "Exam2";
    }
    public List<User> getUserList() {
        ArrayList<User> userlist = new ArrayList<User>();
        userlist.add(new User("Amy", Sex.Female, Date.valueOf("1983-12-03"), 26, true));
        userlist.add(new User("Bob", Sex.Male, Date.valueOf("1989-06-12"), 20, false));
        userlist.add(new User("Chris", Sex.Unknown, Date.valueOf("1980-03-08"), 29, true));
        userlist.add(new User("Alex", Sex.InterSex, Date.valueOf("1992-06-14"), 17, false));
        return userlist;
    }
}
