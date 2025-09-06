package io.github.amsatrio.spring_boot_container_demo.health;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import io.github.amsatrio.spring_boot_container_demo.module.health.HealthRepository;

@SpringBootTest
public class HealthRepositoryTest {
    @Autowired
    private HealthRepository repository;

    @Test
    public void getStatus_success() {
        String expected = "ok";
        String actual = repository.getStatus();

        assertEquals(expected, actual);
    }
}
