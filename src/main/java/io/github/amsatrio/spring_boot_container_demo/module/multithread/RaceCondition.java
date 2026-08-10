package io.github.amsatrio.spring_boot_container_demo.module.multithread;

public class RaceCondition {
    private int count = 0;

    public void start() throws InterruptedException {
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 10000; i++)
                count++;
        });

        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 10000; i++)
                count++;
        });

        t1.start();
        t2.start();
        t1.join();
        t2.join();

        System.out.println("Final count: " + count);
    }

    public void startSync() throws InterruptedException {
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 10000; i++) {
                synchronized (this) {
                    count++;
                }
            }
        });

        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 10000; i++) {
                synchronized (this) {
                    count++;
                }
            }
        });

        t1.start();
        t2.start();
        t1.join();
        t2.join();

        System.out.println("Final count: " + count);
    }
}
