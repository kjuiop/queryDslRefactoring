package com.gig.querydsl.repository;

import com.gig.querydsl.domain.PocketMon;
import com.gig.querydsl.domain.QPocketMon;
import com.gig.querydsl.domain.QPocketMonMaster;
import com.gig.querydsl.dto.PocketMonMasterDto;
import com.gig.querydsl.dto.PocketMonSearchCondition;
import com.gig.querydsl.dto.QPocketMonMasterDto;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

import static com.gig.querydsl.domain.QPocketMon.pocketMon;
import static com.gig.querydsl.domain.QPocketMonMaster.pocketMonMaster;
import static org.springframework.util.StringUtils.hasText;

@Repository
@RequiredArgsConstructor
public class PocketMonJpaRepository {

    private final EntityManager em;
    private final JPAQueryFactory queryFactory;

//    public PocketMonJpaRepository(EntityManager em, JPAQueryFactory queryFactory) {
//        this.em = em;
//        this.queryFactory = queryFactory;
//    }

    public void save(PocketMon pocketMon) {
        em.persist(pocketMon);
    }

    public Optional<PocketMon> findById(Long id) {
        PocketMon findPocketMon = em.find(PocketMon.class, id);
        return Optional.ofNullable(findPocketMon);
    }

    public List<PocketMon> findAll() {
        return em.createQuery("select p from PocketMon p", PocketMon.class)
                .getResultList();
    }

    public List<PocketMon> findAll_Querydsl() {
        return queryFactory
                .selectFrom(pocketMon)
                .fetch();
    }

    public List<PocketMon> findByName(String name) {
        return em.createQuery("select p from PocketMon p where p.name = :name", PocketMon.class)
                .setParameter("name", name)
                .getResultList();
    }

    public List<PocketMon> findByName_Querydsl(String name) {
        return queryFactory
                .selectFrom(pocketMon)
                .where(pocketMon.name.eq(name))
                .fetch();
    }

    /**
     * condition 이 없을 때는 데이터를 모두 가져옴으로 limit 가 있는 것이 좋다.
     */
    public List<PocketMonMasterDto> searchByBuilder(PocketMonSearchCondition condition) {

        BooleanBuilder builder = new BooleanBuilder();
        if (hasText(condition.getPocketMonName())) {
            builder.and(pocketMon.name.eq(condition.getPocketMonName()));
        }
        if (hasText(condition.getPocketMonMasterName())) {
            builder.and(pocketMonMaster.name.eq(condition.getPocketMonMasterName()));
        }
        if (condition.getLevelGoe() != null) {
            builder.and(pocketMon.level.goe(condition.getLevelGoe()));
        }
        if (condition.getLevelLoe() != null) {
            builder.and(pocketMon.level.loe(condition.getLevelLoe()));
        }

        return queryFactory
                .select(new QPocketMonMasterDto(
                        pocketMon.id.as("pocketMonId"),
                        pocketMon.name.as("name"),
                        pocketMon.level.as("level"),
                        pocketMon.pocketMonMaster.id.as("pocketMonMasterId"),
                        pocketMon.pocketMonMaster.name.as("pocketMonMasterName")
                ))
                .from(pocketMon)
                .leftJoin(pocketMon.pocketMonMaster, pocketMonMaster)
                .where(builder)
                .fetch();
    }


    /**
     * where 절 파라미터를 이용한 search
     */
    public List<PocketMonMasterDto> search(PocketMonSearchCondition condition) {
        return queryFactory
                .select(new QPocketMonMasterDto(
                        pocketMon.id.as("pocketMonId"),
                        pocketMon.name.as("name"),
                        pocketMon.level.as("level"),
                        pocketMon.pocketMonMaster.id.as("pocketMonMasterId"),
                        pocketMon.pocketMonMaster.name.as("pocketMonMasterName")
                ))
                .from(pocketMon)
                .leftJoin(pocketMon.pocketMonMaster, pocketMonMaster)
                .where(
                        nameEq(condition.getPocketMonName()),
                        pocketMonMasterNameEq(condition.getPocketMonMasterName()),
                        levelGoe(condition.getLevelGoe()),
                        levelLoe(condition.getLevelLoe())
                )
                .fetch();
    }

    /**
     * where param 은 재사용 가능
     */
    public List<PocketMon> searchPocketMon(PocketMonSearchCondition condition) {
        return queryFactory
                .selectFrom(pocketMon)
                .leftJoin(pocketMon.pocketMonMaster, pocketMonMaster)
                .where(
                        nameEq(condition.getPocketMonName()),
                        pocketMonMasterNameEq(condition.getPocketMonMasterName()),
                        levelGoe(condition.getLevelGoe()),
                        // levelLoe(condition.getLevelLoe()),
                        levelBetween(condition.getLevelGoe(), condition.getLevelGoe())
                )
                .fetch();
    }

    private BooleanExpression nameEq(String pocketMonName) {
        return hasText(pocketMonName) ? pocketMon.name.eq(pocketMonName) : null;
    }

    private BooleanExpression pocketMonMasterNameEq(String pocketMonMasterName) {
        return hasText(pocketMonMasterName) ? pocketMonMaster.name.eq(pocketMonMasterName) : null;
    }

    private BooleanExpression levelGoe(Integer levelGoe) {
        return levelGoe != null ? pocketMon.level.goe(levelGoe) : null;
    }

    private BooleanExpression levelLoe(Integer levelLoe) {
        return levelLoe != null ? pocketMon.level.loe(levelLoe) : null;
    }

    /**
     * 조립 가능
     */
    private BooleanExpression levelBetween(int levelGoe, int levelLoe) {
        return levelGoe(levelGoe).and(levelLoe(levelLoe));
    }

}
