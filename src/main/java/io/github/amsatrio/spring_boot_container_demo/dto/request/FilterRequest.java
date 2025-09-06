package io.github.amsatrio.spring_boot_container_demo.dto.request;

import io.github.amsatrio.spring_boot_container_demo.dto.enumerator.FilterDataType;
import io.github.amsatrio.spring_boot_container_demo.dto.enumerator.FilterMatchMode;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class FilterRequest {
    private String id;
    private Object value;
    private FilterMatchMode matchMode = FilterMatchMode.CONTAINS;
    private FilterDataType dataType = FilterDataType.TEXT;
}
