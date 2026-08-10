package io.github.amsatrio.spring_boot_container_demo.module.multithread;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("multi-thread")
public class MultiThreadApi {
    @GetMapping("cooking-task")
    public String cookingTask() {
        for (int i = 0; i < 5; i++) {
            Thread thread = new CookingTask("Food " + i);
            thread.start();
        }
        return new String();
    }

    @GetMapping("cooking-job")
    public String cookingJob() {
        for (int i = 0; i < 5; i++) {
            Thread thread = new Thread(new CookingJob("Food " + i));
            thread.start();
        }
        return new String();
    }

    @GetMapping("simple-race-condition")
    public String simpleraceCondition() throws InterruptedException {
        RaceCondition raceCondition = new RaceCondition();
        raceCondition.start();
        return new String();
    }

    @GetMapping("simple-race-condition-sync")
    public String simpleRaceConditionSync() throws InterruptedException {
        RaceCondition raceCondition = new RaceCondition();
        raceCondition.startSync();
        return new String();
    }

    @GetMapping("simple-thread-communication")
    public String simpleThreadCommunication() throws InterruptedException {
        ThreadCommunication threadCommunication = new ThreadCommunication();
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                try {
                    threadCommunication.produced();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                try {
                    threadCommunication.consumed();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        t1.start();
        t2.start();
        t1.join();
        t2.join();
        return new String();
    }
}
