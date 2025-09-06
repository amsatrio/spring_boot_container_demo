package io.github.amsatrio.spring_boot_container_demo.module.health;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.amsatrio.spring_boot_container_demo.dto.response.AppResponse;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@RestController
@RequestMapping("/health")
public class HealthApi {
    @Autowired
    private HealthService service;

    @GetMapping("/status")
    public ResponseEntity<AppResponse<String>> status() {
        log.info("status()");

        AppResponse<String> appResponse = AppResponse.ok(service.status());
        return ResponseEntity.status(appResponse.getStatus()).body(appResponse);
    }
    
}
