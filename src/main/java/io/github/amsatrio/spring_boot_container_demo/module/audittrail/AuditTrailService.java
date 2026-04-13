package io.github.amsatrio.spring_boot_container_demo.module.audittrail;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import io.github.amsatrio.spring_boot_container_demo.util.AppGenerator;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class AuditTrailService {
    public void logging(AuditTrail auditTrail) {
        try {
            Date now = new Date();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            MDC.put("username", auditTrail.getUsername());
            MDC.put("role", auditTrail.getRole());
            MDC.put("ipAddress", auditTrail.getIpAddress());
            MDC.put("url", auditTrail.getUrl());
            MDC.put("events", auditTrail.getEvent());
            MDC.put("request", auditTrail.getRequest());
            MDC.put("oldValues", auditTrail.getOldValues());
            MDC.put("newValues", auditTrail.getNewValues());
            MDC.put("createdDate", simpleDateFormat.format(now));
            MDC.put("id", AppGenerator.generateUUIDv7().toString());
            MDC.put("tableName", auditTrail.getTableName());

            switch (auditTrail.getStatus()) {
                case AuditTrailStatus.ERROR:
                    log.error(auditTrail.getMessage(), auditTrail.getError());
                    break;
                case AuditTrailStatus.WARN:
                    log.warn(auditTrail.getMessage());
                    break;
                case AuditTrailStatus.DEBUG:
                    log.debug(auditTrail.getMessage());
                    break;
                default:
                    log.info(auditTrail.getMessage());
                    break;
            }
        } finally {
            MDC.clear();
        }
    }
}
