package io.github.amsatrio.spring_boot_container_demo.module.health;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.amsatrio.spring_boot_container_demo.dto.response.AppResponse;
import io.github.amsatrio.spring_boot_container_demo.module.audittrail.AuditTrail;
import io.github.amsatrio.spring_boot_container_demo.module.audittrail.AuditTrailService;
import jakarta.servlet.http.HttpServletRequest;
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

    @Autowired
    private HttpServletRequest httpServletRequest;
    @Autowired
    private AuditTrailService auditTrailService;

    @GetMapping("/status")
    public ResponseEntity<AppResponse<String>> status() {
        String url = httpServletRequest.getRequestURL().toString();
        String ipAddress = httpServletRequest.getRemoteAddr();
        String functionName = new Object() {
        }.getClass().getEnclosingMethod().getName();

        AuditTrail auditTrail = AuditTrail.builder()
                .url(url)
                .ipAddress(ipAddress)
                .event(functionName)
                .request("")
                .message("Health check status..")
                .build();
        auditTrailService.logging(auditTrail);

        AppResponse<String> appResponse = AppResponse.ok(service.status());
        return ResponseEntity.status(appResponse.getStatus()).body(appResponse);
    }

}
