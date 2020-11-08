package com.gig.querydsl.repository;

import com.gig.querydsl.domain.PocketMon;
import com.gig.querydsl.dto.PocketMonMasterDto;
import com.gig.querydsl.dto.PocketMonSearchCondition;
import com.gig.querydsl.dto.QPocketMonMasterDto;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.repository.support.PageableExecutionUtils;

import javax.persistence.EntityManager;
import java.awt.print.Pageable;
import java.util.List;

import static com.gig.querydsl.domain.QPocketMon.pocketMon;
import static com.gig.querydsl.domain.QPocketMonMaster.pocketMonMaster;
import static org.springframework.util.StringUtils.hasText;

public class PocketMonRepositoryImpl implements PocketMonRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    public PocketMonRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
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

    @Override
    public Page<PocketMonMasterDto> searchPageSimple(PocketMonSearchCondition condition, org.springframework.data.domain.Pageable pageable) {
        QueryResults<PocketMonMasterDto> results = queryFactory
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
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        List<PocketMonMasterDto> content = results.getResults();
        long total = results.getTotal();

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public Page<PocketMonMasterDto> searchPageComplex(PocketMonSearchCondition condition, org.springframework.data.domain.Pageable pageable) {

        List<PocketMonMasterDto> content = queryFactory
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
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<PocketMon> countQuery = queryFactory.select(pocketMon)
                .from(pocketMon)
                .leftJoin(pocketMon.pocketMonMaster, pocketMonMaster)
                .where(
                        nameEq(condition.getPocketMonName()),
                        pocketMonMasterNameEq(condition.getPocketMonMasterName()),
                        levelGoe(condition.getLevelGoe()),
                        levelLoe(condition.getLevelLoe())
                );

        // content 가 pageSize 보다 작을 때 total count 쿼리를 실행 안함. 현재 페이지 사이즈만 호출해도 되니
        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchCount);
//        return new PageImpl<>(content, pageable, total);
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
}
