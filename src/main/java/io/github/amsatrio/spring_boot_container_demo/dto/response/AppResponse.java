package io.github.amsatrio.spring_boot_container_demo.dto.response;

import java.util.Arrays;
import java.util.Date;
import java.util.Optional;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AppResponse<T> {
    private int status;
    private String message;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date timestamp;
    private String path;
    private T data;
    private StackTraceElement[] trace;

    
    // === GETTER

    public Date getTimestamp() {
        return Optional.ofNullable(this.timestamp).orElse(null);
    }

    public StackTraceElement[] getTrace() {
        return trace != null
                ? Arrays.copyOf(trace, trace.length)
                : null;
    }

    // === SETTER

    private void setTimestamp(Date date) {
        this.timestamp = (date != null)
                ? new Date(date.getTime())
                : null;
    }

    private void setTrace(StackTraceElement[] originalTrace) {
        if (originalTrace != null) {
            // Limit stack trace to prevent potential information leakage
            this.trace = Arrays.copyOfRange(
                    originalTrace,
                    0,
                    Math.min(originalTrace.length, 5));
        } else {
            this.trace = null;
        }
    }

    // === CUSTOM

    public static <T> AppResponse<T> ok(T data) {
        AppResponse<T> response = new AppResponse<>();
        response.setTimestamp(new Date());
        response.setStatus(HttpStatus.OK.value());
        response.setData(data);
        response.setMessage("success");
        return response;
    }

    public static <T> AppResponse<T> success(int status, String path, T data) {
        AppResponse<T> response = new AppResponse<>();
        response.setTimestamp(new Date());
        response.setStatus(status);
        response.setData(data);
        response.setMessage("success");
        response.setPath(path);
        return response;
    }

    public static <T> AppResponse<T> success(int status, String path) {
        AppResponse<T> response = new AppResponse<>();
        response.setTimestamp(new Date());
        response.setStatus(status);
        response.setMessage("success");
        response.setPath(path);
        return response;
    }

    public static <T> AppResponse<T> error(int status, String message, StackTraceElement[] errorTrace) {
        AppResponse<T> response = new AppResponse<>();
        response.setTimestamp(new Date());
        response.setStatus(status);
        response.setTrace(errorTrace);
        response.setMessage(message);
        return response;
    }

    public static <T> AppResponse<T> error(int status, String message, T data, StackTraceElement[] trace) {
        AppResponse<T> response = new AppResponse<>();
        response.setTimestamp(new Date());
        response.setStatus(status);
        response.setTrace(trace);
        response.setMessage(message);
        response.setData(data);
        return response;
    }

}
