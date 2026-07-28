package io.github.amsatrio.spring_boot_container_demo.module.audittrail;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.amsatrio.spring_boot_container_demo.dto.response.AppResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@RestController
@RequestMapping("/audit-trail")
public class AuditTrailApi {
    @Autowired
    private HttpServletRequest httpServletRequest;
    @Autowired
    private AuditTrailService auditTrailService;

    @GetMapping("/insert-log")
    public ResponseEntity<AppResponse<String>> insertLog() {
        String url = httpServletRequest.getRequestURL().toString();
        String ipAddress = httpServletRequest.getRemoteAddr();
        String functionName = new Object() {
        }.getClass().getEnclosingMethod().getName();

        AuditTrail auditTrail = AuditTrail.builder()
                .url(url)
                .ipAddress(ipAddress)
                .event(functionName)
                .request("")
                .message("insert log")
                .build();
        auditTrailService.logging(auditTrail);

        AppResponse<String> appResponse = AppResponse.ok(null);
        return ResponseEntity.status(appResponse.getStatus()).body(appResponse);
    }

    @GetMapping("get-all-log-from-elastic")
    public ResponseEntity<AppResponse<Object>> getAllLogFromElastic() {
        List<AuditTrail> logs = auditTrailService.getAllLogsFromElastic();

        AppResponse<Object> appResponse = AppResponse.ok(logs);
        return ResponseEntity.status(appResponse.getStatus()).body(appResponse);
    }

    @GetMapping("/get-all-log-from-file")
    public ResponseEntity<AppResponse<Object>> getAllLogFromFile() {
        List<AuditTrail> logs = auditTrailService.getAllLogsFromDir();

        AppResponse<Object> appResponse = AppResponse.ok(logs);
        return ResponseEntity.status(appResponse.getStatus()).body(appResponse);
    }
}