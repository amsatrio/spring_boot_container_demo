package io.github.amsatrio.spring_boot_container_demo.module.m_biodata;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import io.github.amsatrio.spring_boot_container_demo.dto.request.FilterRequest;
import io.github.amsatrio.spring_boot_container_demo.dto.request.SortRequest;
import io.github.amsatrio.spring_boot_container_demo.dto.response.PaginatedResponse;
import io.github.amsatrio.spring_boot_container_demo.service.pagination.PaginationService;
import io.github.amsatrio.spring_boot_container_demo.util.AppConverter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class MBiodataRepository {
    @Autowired
    @Qualifier("db1JdbcTemplate") 
    // @Qualifier("db2JdbcTemplate") 
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private PaginationService<MBiodataDto> paginationService;

    @Autowired
    private AppConverter appConverter;

    public Optional<MBiodataDto> findById(long id) {
        String sql = "SELECT * FROM m_biodata WHERE id = ?";
        List<MBiodataDto> mBiodataDtos = jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(MBiodataDto.class), id);
        if (mBiodataDtos.isEmpty()) {
            return Optional.empty();
        }
        return Optional.ofNullable(mBiodataDtos.getFirst());
    }

    public List<MBiodataDto> findAll() {
        String sql = "SELECT * FROM m_biodata";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(MBiodataDto.class));
    }

    public boolean isExists(long id) {
        String sql = "SELECT COUNT(id) FROM m_biodata WHERE id = ? LIMIT 1";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, id);
        if (count == null) {
            throw new RuntimeException("sql must not be null");
        }
        return count > 0;
    }

    public boolean isDeleted(long id) {
        String sql = "SELECT COUNT(id) FROM m_biodata WHERE id = ? and is_delete = 1 LIMIT 1";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, id);
        if (count == null) {
            throw new RuntimeException("sql must not be null");
        }
        return count > 0;
    }

    public boolean insert(MBiodataDto dto) {
        if (isExists(dto.getId())) {
            throw new RuntimeException("data exist");
        }

        String fieldModel = "";
        String fieldValueModel = "";
        List<Object> args = new ArrayList<>();
        Class<MBiodataDto> dtoClass = MBiodataDto.class;
        Field[] fields = dtoClass.getDeclaredFields();
        for (Field field : fields) {
            String stringField = appConverter.camelToSnakeCase(field.getName());

            // exclude item from filter
            if (stringField.equals("modified_by") ||
                    stringField.equals("modified_on") ||
                    stringField.equals("deleted_by") ||
                    stringField.equals("deleted_on")) {
                continue;
            }

            fieldModel += fieldModel.isEmpty() ? "" : ",";
            fieldModel += stringField;

            fieldValueModel += fieldValueModel.isEmpty() ? "" : ",";
            fieldValueModel += "?";

            try {
                field.setAccessible(true);
                args.add(field.get(dto));
            } catch (IllegalArgumentException exception) {
                log.error("error:", exception);
                throw new RuntimeException(exception.getMessage());
            } catch (IllegalAccessException exception) {
                log.error("error:", exception);
                throw new RuntimeException(exception.getMessage());
            }
        }
        String sql = "INSERT INTO m_biodata" + " (" + fieldModel + ") " + "VALUES (" + fieldValueModel + ")";

        return jdbcTemplate.update(sql, args.toArray()) == 1;
    }

    public boolean update(MBiodataDto dto) {
        if (!isExists(dto.getId())) {
            throw new RuntimeException("data not found");
        }
        if (!isDeleted(dto.getId()) && dto.getIsDelete()) {
            return updateSoftDelete(dto);
        }

        String fieldModel = "";
        List<Object> args = new ArrayList<>();
        Class<MBiodataDto> dtoClass = MBiodataDto.class;
        Field[] fields = dtoClass.getDeclaredFields();
        for (Field field : fields) {
            String stringField = appConverter.camelToSnakeCase(field.getName());

            // exclude item from filter
            if (stringField.equals("created_by") ||
                    stringField.equals("created_on") ||
                    stringField.equals("deleted_by") ||
                    stringField.equals("deleted_on") ||
                    stringField.equals("id")) {
                continue;
            }

            fieldModel += fieldModel.isEmpty() ? "" : ",";
            fieldModel += stringField + " = ?";

            try {
                field.setAccessible(true);
                args.add(field.get(dto));
            } catch (IllegalArgumentException exception) {
                log.error("error:", exception);
                throw new RuntimeException(exception.getMessage());
            } catch (IllegalAccessException exception) {
                log.error("error:", exception);
                throw new RuntimeException(exception.getMessage());
            }
        }
        args.add(dto.getId());
        String sql = "UPDATE m_biodata SET " + fieldModel + " WHERE id = ?";
        return jdbcTemplate.update(sql, args.toArray()) == 1;
    }

    private boolean updateSoftDelete(MBiodataDto dto) {
        String fieldModel = "";
        List<Object> args = new ArrayList<>();
        Class<MBiodataDto> dtoClass = MBiodataDto.class;
        Field[] fields = dtoClass.getDeclaredFields();
        for (Field field : fields) {
            String stringField = appConverter.camelToSnakeCase(field.getName());

            // only these item
            if (!stringField.equals("is_delete") ||
                    !stringField.equals("deleted_by") ||
                    !stringField.equals("deleted_on")) {
                continue;
            }

            fieldModel += fieldModel.isEmpty() ? "" : ",";
            fieldModel += stringField + " = ?";

            try {
                field.setAccessible(true);
                args.add(field.get(dto));
            } catch (IllegalArgumentException exception) {
                log.error("error:", exception);
                throw new RuntimeException(exception.getMessage());
            } catch (IllegalAccessException exception) {
                log.error("error:", exception);
                throw new RuntimeException(exception.getMessage());
            }
        }
        args.add(dto.getId());
        String sql = "UPDATE m_biodata SET " + fieldModel + " WHERE id = ?";
        return jdbcTemplate.update(sql, args.toArray()) == 1;
    }

    public boolean delete(long id) {
        if (!isExists(id)) {
            throw new RuntimeException("data not found");
        }
        String sql = "DELETE FROM m_biodata WHERE id = ?";
        return jdbcTemplate.update(sql, id) == 1;
    }

    public PaginatedResponse<MBiodataDto> page(
            int page,
            int size,
            List<SortRequest> sorts,
            List<FilterRequest> filters,
            String globalFilters) {

        return paginationService.pagination(jdbcTemplate, "m_biodata", MBiodataDto.class, page, size, sorts, filters, globalFilters);
    }
}
