package io.github.amsatrio.spring_boot_container_demo.module.audittrail;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AuditTrailRequest {
    @JsonProperty("tableName")
    private String tableName;

    @JsonProperty("oldValues")
    private String oldValues;

    @JsonProperty("newValues")
    private String newValues;

    @JsonProperty("url")
    private String url;

    @JsonProperty("username")
    private String username;

    @JsonProperty("role")
    private String role;

    @JsonProperty("ipAddress")
    private String ipAddress;

    @JsonProperty("message")
    private String message;

    @JsonProperty("request")
    private String request;

    @JsonProperty("status")
    private String status;

    @JsonProperty("error")
    private String error;

    @JsonProperty("events")
    private String events;

    @JsonProperty("activity")
    private String activity;

    @JsonProperty("activityStatus")
    private String activityStatus;

    @JsonProperty("menu")
    private String menu;

    @JsonProperty("module")
    private String module;
}
