package com.jsonparse.common;

public class Logger {
    public static void i(String message) {
        System.out.println(message);
    }

    public static void d(String message) {
        System.out.println(message);
    }

    public static void e(String message) {
        System.out.println(message);
    }

    public static void e(Throwable throwable) {
        throwable.printStackTrace();
    }

}
