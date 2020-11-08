package com.gig.querydsl.service;

import com.gig.querydsl.domain.PocketMon;
import com.gig.querydsl.domain.PocketMonMaster;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Profile("local")
@Component
@RequiredArgsConstructor
public class InitData {

    private final initPocketMonService initPocketMonService;

    // @PostConstruct 와 @Transactional 이 동시 적용 안됨
    @PostConstruct
    public void init() {
        initPocketMonService.init();
    }

    @Component
    static class initPocketMonService {
        @PersistenceContext
        private EntityManager em;

        @Transactional
        public void init() {
            PocketMonMaster masterA = new PocketMonMaster("masterA");
            PocketMonMaster masterB = new PocketMonMaster("masterB");

            em.persist(masterA);
            em.persist(masterB);

            for (int i=0; i<100; i++) {
                PocketMonMaster master = i % 2 == 0 ? masterA : masterB;
                em.persist(new PocketMon("pocketmon" + i, i, master));
            }
        }
    }

}
