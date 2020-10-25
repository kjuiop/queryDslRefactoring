package com.gig.querydsl.domain;

import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(of = {"id", "name", "level"})
public class PocketMon {

    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "pocketmon_id")
    private Long id;

    private String name;

    @ColumnDefault("1")
    private int level;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pocketmon_master_id")
    private PocketMonMaster pocketMonMaster;

    public PocketMon(String name, int level, PocketMonMaster pocketMonMaster) {
        this.name = name;
        this.level = level;
        if (pocketMonMaster != null) {
            changePocketMonMaster(pocketMonMaster);
        }
    }

    public void changePocketMonMaster(PocketMonMaster pocketMonMaster) {
        this.pocketMonMaster = pocketMonMaster;
        pocketMonMaster.getPocketMons().add(this);
    }

    public PocketMon(String name, int level) {
        this(name, level, null);
    }

    public PocketMon(String name) {
        this(name, 1, null);
    }
}
