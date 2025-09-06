package io.github.amsatrio.spring_boot_container_demo.service.pagination;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import io.github.amsatrio.spring_boot_container_demo.dto.enumerator.FilterMatchMode;
import io.github.amsatrio.spring_boot_container_demo.dto.request.FilterRequest;
import io.github.amsatrio.spring_boot_container_demo.dto.request.SortRequest;
import io.github.amsatrio.spring_boot_container_demo.dto.response.PaginatedResponse;
import io.github.amsatrio.spring_boot_container_demo.util.AppConverter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class JDBCPagination<T> implements PaginationService<T> {
    @Autowired
    private AppConverter appConverter;

    @Override
    public PaginatedResponse<T> pagination(
            JdbcTemplate jdbcTemplate,
            String tableName,
            Class<T> dtoClass,
            int page,
            int size,
            List<SortRequest> sorts,
            List<FilterRequest> filters,
            String globalFilters) {
        // Build dynamic where clause
        List<Object> params = new ArrayList<>();
        StringBuilder whereClause = new StringBuilder();

        // SORTING
        String sortClause = buildSortClause(sorts);

        // GLOBAL FILTER / SEARCH
        appendGlobalFilter(dtoClass, globalFilters, whereClause, params);

        // FILTER
        appendSpecificFilter(filters, whereClause, params);

        // Count total records
        String countSql = "SELECT COUNT(*) FROM " + tableName + whereClause;
        Integer totalRecords = jdbcTemplate.queryForObject(
                countSql,
                Integer.class,
                params.toArray());
        if (totalRecords == null) {
            totalRecords = 0;
        }

        // Calculate page
        int totalPages = totalRecords / size;
        if (totalRecords % size != 0)
            totalPages += 1;
        Boolean firstPage = page == 0;
        Boolean lastPage = page == totalPages - 1 || totalPages == 0;

        // Calculate pagination
        int offset = page * size;
        String paginationClause = " LIMIT " + size + " OFFSET " + offset;

        // Query with pagination
        String sql = "SELECT * FROM " + tableName +
                whereClause +
                sortClause +
                paginationClause;

        // Execute query
        List<T> results = jdbcTemplate.query(
                sql,
                new BeanPropertyRowMapper<>(dtoClass),
                params.toArray());

        PaginatedResponse<T> paginatedResponse = new PaginatedResponse<>();
        paginatedResponse.setContent(results);
        paginatedResponse.setFirst(firstPage);
        paginatedResponse.setLast(lastPage);
        paginatedResponse.setTotalElements(totalRecords);
        paginatedResponse.setTotalPages(totalPages);
        return paginatedResponse;
    }

    private void appendGlobalFilter(
            Class<? extends Object> dtoClass,
            String globalFilters,
            StringBuilder whereClause,
            List<Object> params) {
        Field[] fields = dtoClass.getDeclaredFields();
        for (Field field : fields) {
            // exclude item from filter
            if (field.getName().startsWith("id") ||
                    field.getName().endsWith("id") ||
                    field.getType() != String.class) {
                continue;
            }
            String globalFilterBy = appConverter.camelToSnakeCase(field.getName());
            whereClause.append(whereClause.length() == 0 ? " WHERE (" : " OR ")
                    .append(globalFilterBy + " LIKE LOWER(?)");
            params.add("%" + globalFilters + "%");
        }
        whereClause.append(whereClause.length() == 0 ? "" : " ) ");
    }

    private String buildSortClause(List<SortRequest> sorts) {
        if (sorts.isEmpty())
            return "";

        String sortClause = " ORDER BY ";
        return sortClause + sorts.stream()
                .map(sort -> sort.getId() + " " + (sort.isDesc() ? "DESC" : "ASC"))
                .collect(Collectors.joining(", "));

    }

    private void appendSpecificFilter(
            List<FilterRequest> filters,
            StringBuilder whereClause,
            List<Object> params) {
        if (filters.isEmpty())
            return;

        String filterMode = " AND ";

        whereClause.append(whereClause.length() == 0 ? " WHERE " : " AND ").append("(");

        for (int i = 0; i < filters.size(); i++) {
            FilterRequest filterRequest = filters.get(i);

            String value1 = "";
            String value2 = "";
            String key = appConverter.camelToSnakeCase(filterRequest.getId());

            Object object1 = filterRequest.getValue();
            Object object2 = null;
            if (filterRequest.getValue() instanceof ArrayList) {
                try {
                    @SuppressWarnings("unchecked")
                    List<Object> objectList = (List<Object>) filterRequest.getValue();
                    if (objectList.size() == 2) {
                        filterRequest.setMatchMode(FilterMatchMode.BETWEEN);
                        object1 = objectList.get(0);
                        object2 = objectList.get(1);
                    } else if (objectList.size() == 1) {
                        object1 = objectList.get(0);
                    } else {
                        continue;
                    }
                } catch (Exception exception) {
                    log.error("get filter data error:", exception);
                }
            }

            value1 = object1.toString();
            switch (filterRequest.getMatchMode()) {
                case BETWEEN:
                    value2 = object2.toString();
                    whereClause.append("(" + key + " BETWEEN " + value1 + " AND " + value2 + ")");
                    params.add(value1);
                    break;
                case EQUALS:
                    whereClause.append(key + " = ?");
                    params.add(value1);
                    break;
                case CONTAINS:
                    whereClause.append(key + " LIKE ?");
                    params.add("%" + value1 + "%");
                    break;
                case NOT:
                    whereClause.append(key + " <> ?");
                    params.add(value1);
                    break;
                case LESS_THAN:
                    whereClause.append(key + " < ?");
                    params.add(value1);
                    break;
                case GREATER_THAN:
                    whereClause.append(key + " > ?");
                    params.add(value1);
                    break;
                default:
                    whereClause.append(key + " LIKE ?");
                    params.add("%" + value1 + "%");
                    break;
            }

            // Append filter mode: <filter> and/or <filter> and/or <filter>
            if (i + 1 < filters.size()) {
                whereClause.append(filterMode);
            }
        }
        whereClause.append(")");

    }

}
