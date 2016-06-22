package hprose.example.io;

import hprose.io.HproseClassManager;

public class ClassManagerExam {
    public static void main(String[] args) {
        HproseClassManager.register(User.class, "my_package_User");
        System.out.println(HproseClassManager.getClassAlias(User.class));
        System.out.println(HproseClassManager.getClass("my_package_User"));
        System.out.println(HproseClassManager.getClass("User"));
        System.out.println(HproseClassManager.containsClass("my_package_User"));
        System.out.println(HproseClassManager.containsClass("User"));
    }
}
