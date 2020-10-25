package com.gig.querydsl.domain;

import lombok.*;
import org.springframework.web.bind.annotation.Mapping;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(of = {"id", "name"})
public class PocketMonMaster {

    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "pocketmon_master_id")
    private Long id;

    private String name;

    @OneToMany(mappedBy = "pocketMonMaster")
    private List<PocketMon> pocketMons = new ArrayList<>();

    public PocketMonMaster(String name) {
        this.name = name;
    }

}
