package com.example.library.common;

public class TestFlow {
    private static volatile TestFlow instance;

    public void Test() {
        System.out.println("Test");
    }

    private TestFlow() {
        // private constructor để ngăn chặn tạo instance từ bên ngoài
    }

    public void Thing()      {
        System.out.println("Thing");
    }
}
