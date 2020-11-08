package com.gig.querydsl.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;

@Data
public class PocketMonMasterDto {

    private Long pocketMonId;

    private String name;

    private int level;

    private Long pocketMonMasterId;

    private String pocketMonMasterName;

    @QueryProjection
    public PocketMonMasterDto(Long pocketMonId, String name, int level, Long pocketMonMasterId, String pocketMonMasterName) {
        this.pocketMonId = pocketMonId;
        this.name = name;
        this.level = level;
        this. pocketMonMasterId = pocketMonMasterId;
        this.pocketMonMasterName = pocketMonMasterName;
    }
}
