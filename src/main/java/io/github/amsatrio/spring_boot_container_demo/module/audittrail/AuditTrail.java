package io.github.amsatrio.spring_boot_container_demo.module.audittrail;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuditTrail {
    @Builder.Default
    private String id = "";
    @Builder.Default
    private String tableName = "";
    @Builder.Default
    private String oldValues = "";
    @Builder.Default
    private String newValues = "";
    @Builder.Default
    private String url = "";
    @Builder.Default
    private String username = "";
    @Builder.Default
    private String role = "";
    @Builder.Default
    private String ipAddress = "";
    @Builder.Default
    private String createdDate = "";
    @Builder.Default
    private String message = "";
    @Builder.Default
    private String request = "";
    @Builder.Default
    private String event = "";
    @Builder.Default
    private AuditTrailStatus status = AuditTrailStatus.INFO;
    @Builder.Default
    private Throwable error = null;
}
