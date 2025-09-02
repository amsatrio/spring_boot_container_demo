package io.github.amsatrio.spring_boot_container_demo.health;

import org.springframework.stereotype.Component;

@Component
public class HealthRepository {
    private String status = "ok";
    public String getStatus(){
        return new String(status);
    }
}
