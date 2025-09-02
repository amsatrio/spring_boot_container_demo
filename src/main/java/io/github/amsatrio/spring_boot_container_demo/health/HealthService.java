package io.github.amsatrio.spring_boot_container_demo.health;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class HealthService {
    @Autowired
    private HealthRepository repository;

    public String status(){
        return repository.getStatus();
    }
}
