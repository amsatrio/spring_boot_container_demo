package io.github.amsatrio.spring_boot_container_demo.module.multithread;

public class ThreadCommunication {
    private boolean available = false;
    
    public synchronized void produced() throws InterruptedException {
        while (available) {
            wait();
        }

        System.out.println("producing item...");
        available = true;
        notify();
    }

    public synchronized void consumed() throws InterruptedException {
        while (!available) {
            wait();
        }

        System.out.println("consumed item...");
        available = false;
        notify();
    }
}
