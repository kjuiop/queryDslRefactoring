package com.gig.querydsl.repository;

import com.gig.querydsl.dto.PocketMonMasterDto;
import com.gig.querydsl.dto.PocketMonSearchCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


import java.util.List;

public interface PocketMonRepositoryCustom {
    List<PocketMonMasterDto> search(PocketMonSearchCondition condition);

    Page<PocketMonMasterDto> searchPageSimple(PocketMonSearchCondition condition, Pageable pageable);

    Page<PocketMonMasterDto> searchPageComplex(PocketMonSearchCondition condition, Pageable pageable);
}
