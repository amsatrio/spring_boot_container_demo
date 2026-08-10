package io.github.amsatrio.spring_boot_container_demo.module.multithread;

import java.util.ArrayList;
import java.util.List;

public class CookingTask extends Thread {
    private String task;

    // Shared list across all thread instances
    private static final List<String> PREPARED_FOOD = new ArrayList<>();
    // Shared lock object
    private static final Object LOCK = new Object();

    CookingTask(String task) {
        this.task = task;
    }

    public void run() {
        System.out.println(task + " is being prepared by " + Thread.currentThread().getName());

        // Synchronize ONLY the critical section where shared data is modified
        synchronized (LOCK) {
            PREPARED_FOOD.add(task);
            System.out.println("Finished: " + task + " | Total prepared: " + PREPARED_FOOD.size());
        }
    }
    
}
