package com.gig.querydsl;

import com.gig.querydsl.domain.PocketMon;
import com.gig.querydsl.domain.QPocketMon;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
@Commit
public class QuerydslApplicationTests {

    @PersistenceContext
    EntityManager em;

    @Test
    void contextLoads() {
        PocketMon pocketMon = new PocketMon();
        em.persist(pocketMon);

        JPAQueryFactory query = new JPAQueryFactory(em);
        QPocketMon qPocketMon = new QPocketMon("p");

        PocketMon result = query.selectFrom(qPocketMon).fetchOne();
        Assertions.assertThat(result).isEqualTo(pocketMon);
        Assertions.assertThat(result.getId()).isEqualTo(pocketMon.getId());
    }
}
