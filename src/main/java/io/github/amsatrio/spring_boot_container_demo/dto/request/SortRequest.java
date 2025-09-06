package io.github.amsatrio.spring_boot_container_demo.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SortRequest {
    private String id;
    private boolean desc;
}
