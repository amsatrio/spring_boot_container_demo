package io.github.amsatrio.spring_boot_container_demo.module.health;

import org.springframework.stereotype.Component;

@Component
public class HealthRepository {
    private String status = "ok";
    public String getStatus(){
        return status;
    }
}
