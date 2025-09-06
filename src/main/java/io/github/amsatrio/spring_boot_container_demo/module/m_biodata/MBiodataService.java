package io.github.amsatrio.spring_boot_container_demo.module.m_biodata;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.github.amsatrio.spring_boot_container_demo.dto.request.FilterRequest;
import io.github.amsatrio.spring_boot_container_demo.dto.request.SortRequest;
import io.github.amsatrio.spring_boot_container_demo.dto.response.PaginatedResponse;

@Service
public class MBiodataService {
    @Autowired
    private MBiodataRepository mBiodataRepository;

    public MBiodataDto findById(long id) {
        Optional<MBiodataDto> optionalDto = mBiodataRepository.findById(id);
        if (optionalDto.isEmpty()) {
            throw new RuntimeException("data not found");
        }
        return optionalDto.get();
    }

    public MBiodataDto create(MBiodataDto dto) {
        if (dto.getId() == null) {
            dto.setId(new Date().getTime());
        }

        dto.setCreatedBy(0L);
        dto.setCreatedOn(new Date());
        dto.setIsDelete(false);

        if (mBiodataRepository.insert(dto)) {
            return dto;
        }

        throw new RuntimeException("failed to insert data");
    }

    public MBiodataDto update(MBiodataDto dto) {
        dto.setModifiedBy(0L);
        dto.setModifiedOn(new Date());

        if (dto.getIsDelete()) {
            dto.setDeletedBy(0L);
            dto.setDeletedOn(new Date());
        }

        if (mBiodataRepository.update(dto)) {
            return dto;
        }

        throw new RuntimeException("failed to update data");
    }

    public void delete(long id) {
        boolean status = mBiodataRepository.delete(id);
        if (!status) {
            throw new RuntimeException("failed to delete data");
        }
    }

    public PaginatedResponse<MBiodataDto> page(int page, int size, List<SortRequest> sorts, List<FilterRequest> filters,
            String search) {
        return mBiodataRepository.page(page, size, sorts, filters, search);
    }

}
