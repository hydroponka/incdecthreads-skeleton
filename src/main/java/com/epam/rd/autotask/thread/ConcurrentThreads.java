package com.epam.rd.autotask.thread;

public class ConcurrentThreads {

    private static final int N = Integer.MAX_VALUE / 10;
    static long value;

    /**
     * In a loop increments {@code N} times the {@code value}.
     */
    static class Increment extends Thread {
        // place your code here
    }


    /**
     * In a loop decrements {@code N} times the {@code value}.
     */
    static class Decrement implements Runnable {

        @Override
        public void run() {
        // place your code here
        }
    }

    /**
     * @param t1 a thread to wait
     * @param t2 a thread to wait
     */
    private static void wait(Thread t1, Thread t2) {
        while (t1.isAlive() || t2.isAlive()) {
            // do nothing
        }
    }

    /**
     * Creates two threads and then runs them.
     * @return the {@code value} after the threads are finished.
     */
    public static long test() {
        value = 0;
        // ----- replace by your code here

        Thread t1 = null;
        Thread t2 = null;

        // -----
        wait(t1, t2);
        return value;
    }

    public static void main(String[] args) {
        // example for running task
        for (int j = 0; j < 5; j++) {
            System.out.println(test());
        }
    }
}
