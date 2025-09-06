package io.github.amsatrio.spring_boot_container_demo.module.m_biodata;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.amsatrio.spring_boot_container_demo.dto.request.FilterRequest;
import io.github.amsatrio.spring_boot_container_demo.dto.request.SortRequest;
import io.github.amsatrio.spring_boot_container_demo.dto.response.AppResponse;
import io.github.amsatrio.spring_boot_container_demo.util.AppConverter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

@Slf4j
@RestController
@RequestMapping("/m-biodata")
@CrossOrigin(origins = "*")
public class MBiodataApi {
    @Autowired
    private HttpServletRequest httpServletRequest;
    @Autowired
    private MBiodataService mBiodataService;

    @Autowired
    private AppConverter appConverter;

    @GetMapping("/{id}")
    public ResponseEntity<Object> read(@PathVariable long id) throws IOException {
        MBiodataDto dto = mBiodataService.findById(id);
        AppResponse<MBiodataDto> response = AppResponse.success(HttpStatus.OK.value(),
                httpServletRequest.getRequestURI(),
                dto);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PostMapping()
    public ResponseEntity<Object> create(@Valid @ModelAttribute MBiodataRequest payload) throws IOException {

        AppResponse<MBiodataDto> response = AppResponse.success(HttpStatus.OK.value(),
                httpServletRequest.getRequestURI(),
                mBiodataService.create(payload.toDto()));
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PutMapping()
    public ResponseEntity<Object> update(@Valid @ModelAttribute MBiodataRequest payload) throws IOException {
        if (payload.getId() == null) {
            log.info("payload: " + payload.toString());
            throw new RuntimeException("payload is invalid: id must not be null");
        }
        log.info("payload: " + payload);

        AppResponse<MBiodataDto> response = AppResponse.success(HttpStatus.OK.value(),
                httpServletRequest.getRequestURI(),
                mBiodataService.update(payload.toDto()));
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable long id) {
        mBiodataService.delete(id);
        AppResponse<Object> response = AppResponse.success(HttpStatus.OK.value(), httpServletRequest.getRequestURI());
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping()
    public ResponseEntity<Object> page(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "[]") String sort,
            @RequestParam(defaultValue = "[]") String filter,
            @RequestParam(defaultValue = "") String search) {

        List<FilterRequest> filterRequests = new ArrayList<>();
        List<SortRequest> sortRequests = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            List<Object> filterRequestObjects = objectMapper.readValue(filter, new TypeReference<List<Object>>() {
            });
            for (Object object : filterRequestObjects) {
                FilterRequest filterRequest = objectMapper.convertValue(object, FilterRequest.class);
                if (filterRequest.getValue().toString().startsWith("[")
                        && filterRequest.getValue().toString().endsWith("]")) {
                    List<Object> objectList = objectMapper.convertValue(filterRequest.getValue(),
                            new TypeReference<List<Object>>() {
                            });
                    filterRequest.setValue(objectList);
                }
                filterRequests.add(filterRequest);
            }

            List<Object> sortRequestObjects = objectMapper.readValue(sort, new TypeReference<List<Object>>() {
            });
            for (Object object : sortRequestObjects) {
                SortRequest sortRequest = objectMapper.convertValue(object, SortRequest.class);
                sortRequest.setId(appConverter.camelToSnakeCase(sortRequest.getId()));
                sortRequests.add(sortRequest);
            }
        } catch (Exception exception) {
            log.error("mBiodata > parse error ", exception);
            throw new RuntimeException("request param is invalid");
        }

        AppResponse<Object> response = AppResponse.success(HttpStatus.OK.value(), httpServletRequest.getRequestURI(),
                mBiodataService.page(page, size, sortRequests, filterRequests, search));
        return ResponseEntity.status(response.getStatus()).body(response);
    }
}
