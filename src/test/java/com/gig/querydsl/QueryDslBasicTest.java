package com.gig.querydsl;

import com.gig.querydsl.domain.PocketMon;
import com.gig.querydsl.domain.PocketMonMaster;
import com.gig.querydsl.domain.QPocketMon;
import com.gig.querydsl.domain.QPocketMonMaster;
import com.querydsl.core.QueryResults;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnit;
import java.util.List;

import static com.gig.querydsl.domain.QPocketMon.pocketMon;
import static com.gig.querydsl.domain.QPocketMonMaster.pocketMonMaster;
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
        PocketMon pocketMon3 = new PocketMon("pocketmon3", 5, masterB);
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

    @Test
    public void aggregation() {
        List<Tuple> result = queryFactory
                .select(pocketMon.count(),
                        pocketMon.level.sum(),
                        pocketMon.level.avg(),
                        pocketMon.level.max(),
                        pocketMon.level.min()
                )
                .from(pocketMon)
                .fetch();

        Tuple tuple = result.get(0);
        assertThat(tuple.get(pocketMon.count())).isEqualTo(4);
        assertThat(tuple.get(pocketMon.level.sum())).isEqualTo(40);
        assertThat(tuple.get(pocketMon.level.avg())).isEqualTo(10);
        assertThat(tuple.get(pocketMon.level.max())).isEqualTo(23);
        assertThat(tuple.get(pocketMon.level.min())).isEqualTo(5);
    }

    /**
     * 포켓몬마스터의 이름과 평균 레벨을 구해라
     * @throws Exception
     */
    @Test
    public void group() throws Exception {
        List<Tuple> result = queryFactory
                .select(pocketMonMaster.name, pocketMon.level.avg())
                        .from(pocketMon)
                        .join(pocketMon.pocketMonMaster, pocketMonMaster)
                        .groupBy(pocketMonMaster.name)
                        .fetch();

        Tuple masterA = result.get(0);
        Tuple masterB = result.get(1);

        assertThat(masterA.get(pocketMonMaster.name)).isEqualTo("masterA");
        assertThat(masterA.get(pocketMon.level.avg())).isEqualTo(15);

        assertThat(masterB.get(pocketMonMaster.name)).isEqualTo("masterB");
        assertThat(masterB.get(pocketMon.level.avg())).isEqualTo(35);

    }

    @Test
    public void join() {
        List<PocketMon> result = queryFactory
                .selectFrom(pocketMon)
                // .leftJoin(pocketMon.pocketMonMaster, pocketMonMaster)
                .join(pocketMon.pocketMonMaster, pocketMonMaster)
                .where(pocketMonMaster.name.eq("masterA"))
                .fetch();

        assertThat(result)
                .extracting("name")
                .containsExactly("pocketmon1", "pocketmon2");

    }

    /**
     * 세타 조인
     * 연관관계가 없어도 조인 가능
     */
    @Test
    public void theta_join() {
        em.persist(new PocketMon("masterA"));
        em.persist(new PocketMon("masterB"));

        List<PocketMon> result = queryFactory.select(pocketMon)
                .from(pocketMon, pocketMonMaster)
                .where(pocketMon.name.eq(pocketMonMaster.name))
                .fetch();

        assertThat(result)
                .extracting("name")
                .containsExactly("masterA", "masterB");

    }

    /**
     * 예) 포켓몬과 마스터를 조인하면서, 마스터 이름이 masterA인 마스터만 조인, 포켓몬 모두 조회
     * select p, m from Pocketmon p left join p.master m on o.name = 'masterA'
     */
    @Test
    public void join_on_filtering() {
        List<Tuple> result = queryFactory
                .select(pocketMon, pocketMonMaster)
                .from(pocketMon)
                // .join(pocketMon.pocketMonMaster, pocketMonMaster)
                .leftJoin(pocketMon.pocketMonMaster, pocketMonMaster)
                .on(pocketMonMaster.name.eq("masterA"))
                // .where(pocketMonMaster.name.eq("masterA"))
                .fetch();

        for (Tuple tuple : result) {
            System.out.println("tuple = " + tuple);
        }

    }

    /**
     * 세타 조인
     * 연관관계가 없는 외부 조인
     *
     */
    @Test
    public void join_on_no_realation() {
        em.persist(new PocketMon("masterA"));
        em.persist(new PocketMon("masterB"));
        em.persist(new PocketMon("masterC"));

        List<Tuple> result = queryFactory
                .select(pocketMon, pocketMonMaster)
                .from(pocketMon)
                // pocketmon.pocketmonmaster  ( 자동으로 on 절에 pocketmon 의 master 를 찾음
                .leftJoin(pocketMonMaster).on(pocketMon.name.eq(pocketMonMaster.name))
                .fetch();

        for (Tuple tuple : result) {
            System.out.println("tuple = " + tuple);
        }

    }

    @PersistenceUnit
    EntityManagerFactory emf;

    @Test
    public void fetchJoinNo() {
        em.flush();
        em.clear();

        PocketMon findPocketMon = queryFactory
                .selectFrom(pocketMon)
                .where(pocketMonMaster.name.eq("pocketmon1"))
                .fetchOne();

        boolean loaded = emf.getPersistenceUnitUtil().isLoaded(findPocketMon.getPocketMonMaster());
        assertThat(loaded).as("패치 조인 미적용").isFalse();

    }

    @Test
    public void fetchJoinUse() {
        em.flush();
        em.clear();

        PocketMon findPocketMon = queryFactory
                .selectFrom(pocketMon)
                .join(pocketMon.pocketMonMaster, pocketMonMaster).fetchJoin()
                .where(pocketMon.name.eq("pocketmon1"))
                .fetchOne();

        boolean loaded = emf.getPersistenceUnitUtil().isLoaded(findPocketMon.getPocketMonMaster());
        assertThat(loaded).as("패치 조인 미적용").isTrue();

    }

    /**
     * 레벨이 가장 높은 포켓몬 조회
     */
    @Test
    public void subQuery() {

        QPocketMon pocketMonSub = new QPocketMon("pocketmonSub");

        List<PocketMon> result = queryFactory
                .selectFrom(pocketMon)
                .where(pocketMon.level.eq(
                        JPAExpressions.select(pocketMonSub.level.max())
                        .from(pocketMonSub)
                ))
                .fetch();

        assertThat(result).extracting("level")
                .containsExactly(23);
    }

    /**
     * 레벨이 가장 높은 포켓몬 조회
     */
    @Test
    public void subQueryGoe() {

        QPocketMon pocketMonSub = new QPocketMon("pocketmonSub");

        List<PocketMon> result = queryFactory
                .selectFrom(pocketMon)
                .where(pocketMon.level.goe(
                        JPAExpressions
                                .select(pocketMonSub.level.avg())
                                .from(pocketMonSub)
                ))
                .fetch();

        assertThat(result).extracting("level")
                .containsExactly(23, 12);
    }

    /**
     * 레벨이 가장 높은 포켓몬 조회
     */
    @Test
    public void subQueryIn() {

        QPocketMon pocketMonSub = new QPocketMon("pocketmonSub");

        List<PocketMon> result = queryFactory
                .selectFrom(pocketMon)
                .where(pocketMon.level.in(
                        JPAExpressions
                                .select(pocketMonSub.level)
                                .from(pocketMonSub)
                                .where(pocketMonSub.level.gt(10))
                ))
                .fetch();

        assertThat(result).extracting("level")
                .containsExactly(23, 12);
    }

    @Test
    public void selectSubQuery() {
        QPocketMon pocketMonSub = new QPocketMon("pocketmonSub");

        // JPAExpressions static으로 뺄 수 있음

        List<Tuple> result = queryFactory
                .select(pocketMon.name,
                        JPAExpressions
                            .select(pocketMonSub.level.avg())
                            .from(pocketMonSub))
                .from(pocketMon)
                .fetch();

        for (Tuple tuple : result) {
            System.out.println("tuple = " + tuple);
        }


    }


    @Test
    public void basicCase() {
        List<String> result = queryFactory
                .select(pocketMon.level
                .when(10).then("초급")
                .when(20).then("중급")
                .otherwise("기타"))
                .from(pocketMon)
                .fetch();

        for (String s : result) {
            System.out.println("tuple = " + s);
        }
    }

    @Test
    public void complexCase() {
        List<String> result = queryFactory
                .select(new CaseBuilder()
                    .when(pocketMon.level.between(0, 20)).then("초급")
                    .when(pocketMon.level.between(21, 30)).then("중급")
                    .otherwise("기타")
                ).from(pocketMon)
                .fetch();

        for (String s : result) {
            System.out.println("tuple = " + s);
        }

    }

    @Test
    public void constant() {
        List<Tuple> result = queryFactory
                .select(pocketMon.name, Expressions.constant("A"))
                .from(pocketMon)
                .fetch();

        for (Tuple tuple : result) {
            System.out.println("tuple = " + tuple);
        }

    }

    @Test
    public void concat() {

            List<String> result = queryFactory
                .select(pocketMon.name.concat("_").concat(pocketMon.level.stringValue()))
                .from(pocketMon)
                .where(pocketMon.name.eq("pocketmon1"))
                .fetch();

            for (String s : result) {
                System.out.println("s = " + s);
            }
    }



}

