package com.epam.rd.autotask.thread;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

public class IncDecThreads {

    static final int COUNT = 5000;
    static long value;

    /**
     * In a loop increments {@code COUNT} times the {@code value}
     * and prints to the console the name of the class, the name of
     * the thread and the value of the field {@code value}.
     */
    static class Increment extends Thread {
        @Override
        public void run() {
            for (int i = 0; i < COUNT; i++){
                value++;
                System.out.println(this.getName() + " : " + currentThread().getName() + " : " + value);
            }
        }
    }

    /**
     * In a loop decrements {@code COUNT} times the {@code value}
     * and prints to the console the name of the class, the name of
     * the thread and the value of the field {@code value}.
     */
    static class Decrement implements Runnable {
        @Override
        public void run() {
            for (int i = 0; i < COUNT; i++){
                value--;
                System.out.println(Thread.currentThread().getClass() + " : " + Thread.currentThread().getName() + " : " + value);
            }
        }
    }

    public static void main(String[] args) {
        Increment increment = new Increment();
        Decrement decrement = new Decrement();
        Thread thread = new Thread(decrement);
        increment.start();
        thread.start();
    }
}
