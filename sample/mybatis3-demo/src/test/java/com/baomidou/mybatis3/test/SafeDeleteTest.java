package com.baomidou.mybatis3.test;

/**
 * 传递的安全删除无效的测试用例
 * issue: https://github.com/baomidou/MybatisX/issues/54
 * 详细例子: https://youtrack.jetbrains.com/issue/IDEA-285485
 */
public class SafeDeleteTest {

    private static final String BAR = "bar";
    private static final String TWO = "2000";

    public static void main(String[] args) {
        SafeDeleteTest sample = new SafeDeleteTest();
//        System.out.println(sample.test());
    }

    private String test() {
        check1("foo");
        check2("foo");
        return TWO;
    }

    private void check1(String foo) {
        System.out.println(foo + BAR);
        System.out.println(foo + BAR);
        System.out.println(foo + BAR);
    }

    private static void check2(String foo) {
        System.out.println(foo + BAR);
        System.out.println(foo + BAR);
        System.out.println(foo + BAR);
    }

}
