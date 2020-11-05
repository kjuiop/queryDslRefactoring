package com.gig.querydsl.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PocketMonBaseDto {

    private String name;
    private int level;

    /**
     * 이 어노테이션을 쓰면 DTO 도 Q파일이 생성된다.
     */
    @QueryProjection
    public PocketMonBaseDto(String name, int level) {
        this.name = name;
        this.level = level;
    }
}
