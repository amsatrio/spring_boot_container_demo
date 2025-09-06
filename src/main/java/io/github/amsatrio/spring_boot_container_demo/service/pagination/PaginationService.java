package io.github.amsatrio.spring_boot_container_demo.service.pagination;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;

import io.github.amsatrio.spring_boot_container_demo.dto.request.FilterRequest;
import io.github.amsatrio.spring_boot_container_demo.dto.request.SortRequest;
import io.github.amsatrio.spring_boot_container_demo.dto.response.PaginatedResponse;

public interface PaginationService<T> {
    PaginatedResponse<T> pagination(
            JdbcTemplate jdbcTemplate,
            String tableName,
            Class<T> dtoClass,
            int page,
            int size,
            List<SortRequest> sorts,
            List<FilterRequest> filters,
            String globalFilters);
}
