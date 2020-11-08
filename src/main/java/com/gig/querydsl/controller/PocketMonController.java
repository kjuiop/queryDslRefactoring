package com.gig.querydsl.controller;

import com.gig.querydsl.dto.PocketMonMasterDto;
import com.gig.querydsl.dto.PocketMonSearchCondition;
import com.gig.querydsl.repository.PocketMonJpaRepository;
import com.gig.querydsl.repository.PocketMonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class PocketMonController {

    private final PocketMonJpaRepository pocketMonJpaRepository;
    private final PocketMonRepository pocketMonRepository;

    @GetMapping("/v1/pocketMons")
    public List<PocketMonMasterDto> searchPocketMonV1(PocketMonSearchCondition condition) {
        return pocketMonJpaRepository.search(condition);
    }

    @GetMapping("/v2/pocketMons")
    public Page<PocketMonMasterDto> searchPocketMonV2(PocketMonSearchCondition condition, Pageable pageable) {
        return pocketMonRepository.searchPageSimple(condition, pageable);
    }

    @GetMapping("/v3/pocketMons")
    public Page<PocketMonMasterDto> searchPocketMonV3(PocketMonSearchCondition condition, Pageable pageable) {
        return pocketMonRepository.searchPageComplex(condition, pageable);
    }
}
