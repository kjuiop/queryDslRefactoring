package com.gig.querydsl.controller;

import com.gig.querydsl.dto.PocketMonMasterDto;
import com.gig.querydsl.dto.PocketMonSearchCondition;
import com.gig.querydsl.repository.PocketMonJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class PocketMonController {

    private final PocketMonJpaRepository pocketMonJpaRepository;

    @GetMapping("/v1/pocketMons")
    public List<PocketMonMasterDto> searchPocketMonV1(PocketMonSearchCondition condition) {
        return pocketMonJpaRepository.search(condition);
    }
}
