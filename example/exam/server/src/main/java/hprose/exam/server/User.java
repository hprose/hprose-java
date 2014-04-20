package hprose.exam.server;

import java.sql.Date;

public class User implements java.io.Serializable {
    private String name;
    private Sex sex;
    private Date birthday;
    private int age;
    private boolean married;

    public User() {
    }

    public User(String name, Sex sex, Date birthday, int age, boolean married) {
        this.name = name;
        this.sex = sex;
        this.birthday = birthday;
        this.age = age;
        this.married = married;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Sex getSex() {
        return sex;
    }

    public void setSex(Sex sex) {
        this.sex = sex;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public boolean isMarried() {
        return married;
    }

    public void setMarried(boolean married) {
        this.married = married;
    }
}
