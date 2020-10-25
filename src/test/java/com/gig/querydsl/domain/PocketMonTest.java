package com.gig.querydsl.domain;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class PocketMonTest {

    @PersistenceContext
    EntityManager em;

    @Test
    public void testPocketMonEntity() {
        PocketMonMaster masterA = new PocketMonMaster("masterA");
        PocketMonMaster masterB = new PocketMonMaster("masterB");
        em.persist(masterA);
        em.persist(masterB);

        PocketMon pocketMon1 = new PocketMon("pocketmon1", 23, masterA);
        PocketMon pocketMon2 = new PocketMon("pocketmon2", 12, masterA);
        PocketMon pocketMon3 = new PocketMon("pocketmon3", 11, masterB);
        PocketMon pocketMon4 = new PocketMon("pocketmon4", 5, masterB);

        em.persist(pocketMon1);
        em.persist(pocketMon2);
        em.persist(pocketMon3);
        em.persist(pocketMon4);

        // 초기화
        em.flush();
        em.clear();

        List<PocketMon> pocketMons = em.createQuery("select p from PocketMon p", PocketMon.class)
                .getResultList();

        for (PocketMon pocketMon : pocketMons) {
            System.out.println("pocketmon = " + pocketMon);
            System.out.println("-> master = " + pocketMon.getPocketMonMaster());
        }


    }
}