package com.gig.querydsl;

import com.gig.querydsl.domain.PocketMon;
import com.gig.querydsl.domain.PocketMonMaster;
import com.gig.querydsl.domain.QPocketMon;
import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

import static com.gig.querydsl.domain.QPocketMon.pocketMon;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class QueryDslBasicTest {

    @PersistenceContext
    EntityManager em;

    JPAQueryFactory queryFactory;

    @BeforeEach
    public void before() {
        queryFactory = new JPAQueryFactory(em);

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

    }

    @Test
    public void startJPQL() {

        String qlString =
                "select p from PocketMon p where p.name = :name";

        PocketMon findPocketMon = em.createQuery(qlString, PocketMon.class)
                .setParameter("name", "pocketmon1")
                .getSingleResult();

        assertThat(findPocketMon.getName()).isEqualTo("pocketmon1");
    }

    @Test
    public void startQueryDsl() {
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);

        PocketMon findPocketMon = queryFactory
                .select(pocketMon)
                .from(pocketMon)
                .where(pocketMon.name.eq("pocketmon1"))
                .fetchOne();

        assertThat(findPocketMon.getName()).isEqualTo("pocketmon1");
    }

    @Test
    public void search() {
        PocketMon findPocketMon = queryFactory
                .selectFrom(pocketMon)
                .where(
                        pocketMon.name.eq("pocketmon1").and(pocketMon.level.eq(23))
                )
                .fetchOne();

        assertThat(findPocketMon.getName()).isEqualTo("pocketmon1");
    }

    @Test
    public void searchAndParam() {
        // 이 방법으로 할 시 파라미터가 null 로 들어가면 무시함
        PocketMon findPocketMon = queryFactory
                .selectFrom(pocketMon)
                .where(
                        pocketMon.name.eq("pocketmon1"),
                        pocketMon.level.between(20,30))
                .fetchOne();

        assertThat(findPocketMon.getName()).isEqualTo("pocketmon1");
    }

    @Test
    public void resultFetch() {
//        List<PocketMon> fetch = queryFactory
//                .select(pocketMon)
//                .fetch();
//
//        PocketMon fetchOne = queryFactory
//                .selectFrom(QPocketMon.pocketMon)
//                .fetchOne();
//
//        PocketMon fetchFirst = queryFactory
//                .selectFrom(pocketMon)
//                .fetchFirst();

        QueryResults<PocketMon> results = queryFactory
                .selectFrom(pocketMon)
                .fetchResults();

        results.getTotal();
        List<PocketMon> content = results.getResults();

        long total = queryFactory
                .selectFrom(pocketMon)
                .fetchCount();


    }

    /**
     * 포켓몬 정렬순서
     * 1. 레벨 내림차순 (DESC)
     * 2. 이름 올림차순 (ASC)
     * 단 2에서 포켓몬 이름이 없으면 마지막에 출력 (nulls last)
     */
    @Test
    public void sort() {
        em.persist(new PocketMon(null, 100));
        em.persist(new PocketMon("pocketmon5", 100));
        em.persist(new PocketMon("pocketmon6", 100));

        List<PocketMon> result = queryFactory
                .selectFrom(pocketMon)
                .where(pocketMon.level.eq(100))
                .orderBy(pocketMon.level.desc(), pocketMon.name.asc().nullsLast())
                .fetch();


        PocketMon pocketMon5 = result.get(0);
        PocketMon pocketMon6 = result.get(1);
        PocketMon pocketMonNull = result.get(2);
        assertThat(pocketMon5.getName()).isEqualTo("pocketmon5");
        assertThat(pocketMon6.getName()).isEqualTo("pocketmon6");
        assertThat(pocketMonNull.getName()).isNull();
    }

    @Test
    public void paging1() {
        List<PocketMon> result = queryFactory
                .selectFrom(pocketMon)
                .orderBy(pocketMon.name.desc())
                .offset(1)
                .limit(2)
                .fetch();

        assertThat(result.size()).isEqualTo(2);
    }

    @Test
    public void paging2() {
        QueryResults<PocketMon> queryResults = queryFactory
                .selectFrom(pocketMon)
                .orderBy(pocketMon.name.desc())
                .offset(1)
                .limit(2)
                .fetchResults();

        assertThat(queryResults.getTotal()).isEqualTo(4);
        assertThat(queryResults.getLimit()).isEqualTo(2);
        assertThat(queryResults.getOffset()).isEqualTo(1);
        assertThat(queryResults.getResults().size()).isEqualTo(2);
    }
}
