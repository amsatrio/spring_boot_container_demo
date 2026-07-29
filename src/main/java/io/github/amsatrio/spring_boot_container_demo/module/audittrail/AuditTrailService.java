package io.github.amsatrio.spring_boot_container_demo.module.audittrail;

import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
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
            MDC.put("activity", auditTrail.getActivity());
            MDC.put("activityStatus", auditTrail.getActivityStatus());
            MDC.put("menu", auditTrail.getMenu());
            MDC.put("module", auditTrail.getModule());

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

    @Autowired
    private ElasticsearchClient elasticsearchClient;

    public List<AuditTrail> getAllLogsFromElastic() {
        try {
            SearchResponse<AuditTrail> response = elasticsearchClient.search(s -> s
                    .index("kafka-spring-boot-container-demo*")
                    // .size(100) // retrieve up to 100 documents
                    .query(q -> q.matchAll(m -> m)),
                    AuditTrail.class);

            return response.hits().hits().stream()
                    .map(Hit::source)
                    .filter(Objects::nonNull)
                    .filter(audit -> audit.getId() != null && !audit.getId().isBlank())
                    .collect(Collectors.toList());

        } catch (IOException e) {
            log.error("Error fetching logs from Elasticsearch", e);
            throw new RuntimeException("Failed to query Elasticsearch", e);
        }
    }

    public List<AuditTrail> getLogsFromElastic(String user, String activity, String module, String ipAddress,
            String device, String activityStatus) {
        try {
            SearchResponse<AuditTrail> response = elasticsearchClient.search(s -> s
                    .index("kafka-spring-boot-container-demo*")
                    // .size(100) // retrieve up to 100 documents
                    .query(q -> q
                            .bool(b -> {
                                // Dynamically add conditions only if the argument is provided
                                if (user != null && !user.isBlank()) {
                                    b.must(m -> m.match(m1 -> m1.field("username").query(user)));
                                }
                                if (activity != null && !activity.isBlank()) {
                                    b.must(m -> m.match(m1 -> m1.field("activity").query(activity)));
                                }
                                if (module != null && !module.isBlank()) {
                                    b.must(m -> m.match(m1 -> m1.field("module").query(module)));
                                }
                                if (ipAddress != null && !ipAddress.isBlank()) {
                                    b.must(m -> m.term(t -> t.field("ipAddress").value(ipAddress)));
                                }
                                if (device != null && !device.isBlank()) {
                                    b.must(m -> m.match(m1 -> m1.field("devices").query(device)));
                                }
                                if (activityStatus != null && !activityStatus.isBlank()) {
                                    b.filter(f -> f.term(t -> t.field("activityStatus.keyword").value(activityStatus)));
                                }
                                return b;
                            })),
                    AuditTrail.class);

            return response.hits().hits().stream()
                    .map(Hit::source)
                    .filter(Objects::nonNull)
                    .filter(audit -> audit.getId() != null && !audit.getId().isBlank())
                    .collect(Collectors.toList());

        } catch (IOException e) {
            log.error("Error fetching logs from Elasticsearch", e);
            throw new RuntimeException("Failed to query Elasticsearch", e);
        }
    }

    @Value("${logging.file.name:logs/spring-boot-container-demo_current.log}")
    private String logFilePath;

    public List<AuditTrail> getAllLogsFromDir() {
        Path filePath = Paths.get(logFilePath);
        Path dirPath = filePath.getParent();

        // Fallback to current directory if no parent folder is explicitly specified
        if (dirPath == null) {
            dirPath = Paths.get(".");
        }

        if (!Files.exists(dirPath) || !Files.isDirectory(dirPath)) {
            log.warn("Log directory does not exist or is not a folder: {}", dirPath);
            return Collections.emptyList();
        }

        ObjectMapper objectMapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        // 1. Stream all regular files in the directory
        try (Stream<Path> stream = Files.list(dirPath)) {
            return stream
                    .filter(Files::isRegularFile) // Ignore subdirectories
                    // Filter log files if needed, e.g., .filter(p -> p.toString().endsWith(".log"))
                    .flatMap(path -> readAndParseFile(path, objectMapper).stream())
                    .collect(Collectors.toList());

        } catch (IOException e) {
            log.error("Error listing files in directory: {}", dirPath, e);
            throw new RuntimeException("Failed to read log directory", e);
        }
    }

    private List<AuditTrail> readAndParseFile(Path path, ObjectMapper objectMapper) {
        try (Stream<String> lines = Files.lines(path)) {
            return lines
                    .map(String::trim)
                    .filter(line -> !line.isEmpty())
                    .map(line -> {
                        try {
                            if (!line.contains("\"id\":"))
                                return null;
                            return objectMapper.readValue(line, new TypeReference<AuditTrail>() {
                            });
                        } catch (Exception e) {
                            log.error("Failed to parse log line in file {}: {}", path.getFileName(), line, e);
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            log.error("Error reading file: {}", path, e);
            return Collections.emptyList(); // Skip unreadable/corrupted files gracefully
        }
    }

    public List<AuditTrail> getLogsFromDir(String user, String activity, String module, String ipAddress, String device,
            String activityStatus) {
        Path filePath = Paths.get(logFilePath);
        Path dirPath = filePath.getParent();

        // Fallback to current directory if no parent folder is explicitly specified
        if (dirPath == null) {
            dirPath = Paths.get(".");
        }

        if (!Files.exists(dirPath) || !Files.isDirectory(dirPath)) {
            log.warn("Log directory does not exist or is not a folder: {}", dirPath);
            return Collections.emptyList();
        }

        ObjectMapper objectMapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        // 1. Stream all regular files in the directory
        try (Stream<Path> stream = Files.list(dirPath)) {
            return stream
                    .filter(Files::isRegularFile) // Ignore subdirectories
                    // Filter log files if needed, e.g., .filter(p -> p.toString().endsWith(".log"))
                    .flatMap(path -> readAndParseFileQuery(path, objectMapper, user, activity, module, ipAddress,
                            device, activityStatus).stream())
                    .collect(Collectors.toList());

        } catch (IOException e) {
            log.error("Error listing files in directory: {}", dirPath, e);
            throw new RuntimeException("Failed to read log directory", e);
        }
    }

    private List<AuditTrail> readAndParseFileQuery(Path path, ObjectMapper objectMapper, String user, String activity,
            String module, String ipAddress, String device, String activityStatus) {
        try (Stream<String> lines = Files.lines(path)) {
            return lines
                    .map(String::trim)
                    .filter(line -> !line.isEmpty())
                    .map(line -> {
                        try {
                            if (!line.contains("\"id\":"))
                                return null;
                            return objectMapper.readValue(line, new TypeReference<AuditTrail>() {
                            });
                        } catch (Exception e) {
                            log.error("Failed to parse log line in file {}: {}", path.getFileName(), line, e);
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .filter(audit -> matchesCriteria(audit, user, activity, module, ipAddress, device, activityStatus))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            log.error("Error reading file: {}", path, e);
            return Collections.emptyList();
        }
    }

    private boolean matchesCriteria(
            AuditTrail audit,
            String user,
            String activity,
            String module,
            String ipAddress,
            String device,
            String activityStatus) {

        if (user != null && !user.isBlank()
                && (audit.getUsername() == null || !audit.getUsername().toLowerCase().contains(user.toLowerCase()))) {
            return false;
        }
        if (activity != null && !activity.isBlank() && (audit.getActivity() == null
                || !audit.getActivity().toLowerCase().contains(activity.toLowerCase()))) {
            return false;
        }
        if (module != null && !module.isBlank()
                && (audit.getModule() == null || !audit.getModule().toLowerCase().contains(module.toLowerCase()))) {
            return false;
        }
        if (ipAddress != null && !ipAddress.isBlank()
                && (audit.getIpAddress() == null || !audit.getIpAddress().contains(ipAddress))) {
            return false;
        }
        if (device != null && !device.isBlank()
                && (audit.getDevices() == null || !audit.getDevices().toLowerCase().contains(device.toLowerCase()))) {
            return false;
        }
        if (activityStatus != null && !activityStatus.isBlank()
                && (audit.getActivityStatus() == null || !audit.getActivityStatus().equalsIgnoreCase(activityStatus))) {
            return false;
        }

        return true;
    }
}
