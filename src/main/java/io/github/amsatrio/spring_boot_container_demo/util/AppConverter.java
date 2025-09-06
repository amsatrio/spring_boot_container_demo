package io.github.amsatrio.spring_boot_container_demo.util;

import org.springframework.stereotype.Component;

@Component
public class AppConverter {
    public String camelToSnakeCase(String input) {
        return input.replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase();
    }
}
