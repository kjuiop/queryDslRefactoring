package com.gig.querydsl.repository;

import com.gig.querydsl.domain.PocketMon;
import com.gig.querydsl.domain.PocketMonMaster;
import com.gig.querydsl.dto.PocketMonMasterDto;
import com.gig.querydsl.dto.PocketMonSearchCondition;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class PocketMonJpaRepositoryTest {

    @Autowired
    EntityManager em;

    @Autowired
    PocketMonJpaRepository pocketMonJpaRepository;

    JPAQueryFactory queryFactory;

    @BeforeEach
    public void before() {
        queryFactory = new JPAQueryFactory(em);

        PocketMonMaster masterA = new PocketMonMaster("masterA");
        PocketMonMaster masterB = new PocketMonMaster("masterB");
        em.persist(masterA);
        em.persist(masterB);

        PocketMon pocketMon1 = new PocketMon("pocketmon1", 10, masterA);
        PocketMon pocketMon2 = new PocketMon("pocketmon2", 20, masterA);
        PocketMon pocketMon3 = new PocketMon("pocketmon3", 30, masterB);
        PocketMon pocketMon4 = new PocketMon("pocketmon4", 40, masterB);

        em.persist(pocketMon1);
        em.persist(pocketMon2);
        em.persist(pocketMon3);
        em.persist(pocketMon4);

    }

    @Test
    public void basicTest() {
        PocketMon pocketMon = new PocketMon("pocketmon1", 10);
        pocketMonJpaRepository.save(pocketMon);

        PocketMon findPocketMon = pocketMonJpaRepository.findById(pocketMon.getId()).get();
        assertThat(findPocketMon).isEqualTo(pocketMon);

        List<PocketMon> result1 = pocketMonJpaRepository.findAll();
        assertThat(result1).containsExactly(pocketMon);

        List<PocketMon> result2 = pocketMonJpaRepository.findByName("pocketmon1");
        assertThat(result2).containsExactly(pocketMon);
    }

    @Test
    public void basicQuerydslTest() {
        PocketMon pocketMon = new PocketMon("pocketmon1", 10);
        pocketMonJpaRepository.save(pocketMon);

        PocketMon findPocketMon = pocketMonJpaRepository.findById(pocketMon.getId()).get();
        assertThat(findPocketMon).isEqualTo(pocketMon);

        List<PocketMon> result1 = pocketMonJpaRepository.findAll_Querydsl();
        assertThat(result1).containsExactly(pocketMon);

        List<PocketMon> result2 = pocketMonJpaRepository.findByName_Querydsl("pocketmon1");
        assertThat(result2).containsExactly(pocketMon);
    }

    @Test
    public void searchTest() {
        PocketMonSearchCondition condition = new PocketMonSearchCondition();
        condition.setLevelGoe(35);
        condition.setLevelLoe(40);
        condition.setPocketMonMasterName("masterB");

        List<PocketMonMasterDto> result = pocketMonJpaRepository.searchByBuilder(condition);

        assertThat(result).extracting("pocketMonMasterName").containsExactly("masterB");
    }

    @Test
    public void searchByWhereParam() {
        PocketMonSearchCondition condition = new PocketMonSearchCondition();
        condition.setLevelGoe(35);
        condition.setLevelLoe(40);
        condition.setPocketMonMasterName("masterB");

        List<PocketMonMasterDto> result = pocketMonJpaRepository.search(condition);

        assertThat(result).extracting("pocketMonMasterName").containsExactly("masterB");
    }

}