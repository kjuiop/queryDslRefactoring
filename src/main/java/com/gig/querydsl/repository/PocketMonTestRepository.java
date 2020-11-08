package com.gig.querydsl.repository;

import com.gig.querydsl.domain.PocketMon;
import com.gig.querydsl.domain.QPocketMon;
import com.gig.querydsl.dto.PocketMonSearchCondition;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.data.repository.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.gig.querydsl.domain.QPocketMon.pocketMon;
import static com.gig.querydsl.domain.QPocketMonMaster.pocketMonMaster;
import static com.querydsl.jpa.JPAExpressions.select;
import static com.querydsl.jpa.JPAExpressions.selectFrom;
import static org.springframework.util.StringUtils.applyRelativePath;
import static org.springframework.util.StringUtils.hasText;

@Repository
public class PocketMonTestRepository extends QuerydslRepositorySupport {

    public PocketMonTestRepository() {
        super(PocketMon.class);
    }

    public List<PocketMon> basicSelect() {
        return select(pocketMon)
                .from(pocketMon)
                .fetch();
    }

    public List<PocketMon> basicSelectFrom() {
        return selectFrom(pocketMon)
                .fetch();
    }

    public Page<PocketMon> searchPageByApplyPage(PocketMonSearchCondition condition, Pageable pageable) {
        JPAQuery<PocketMon> query = (JPAQuery<PocketMon>) selectFrom(pocketMon)
                .where(
                        nameEq(condition.getPocketMonName()),
                        pocketMonMasterNameEq(condition.getPocketMonMasterName()),
                        levelGoe(condition.getLevelGoe()),
                        levelLoe(condition.getLevelLoe())
                );

        List<PocketMon> content = getQuerydsl().applyPagination(pageable, query).fetch();
        return PageableExecutionUtils.getPage(content, pageable, query::fetchCount);
    }

//    public Page<PocketMon> applyPagination(PocketMonSearchCondition condition, Pageable pageable) {
//        Page<PocketMon> result = applyPagination(pageable, query -> query.selectFrom(pocketMon)
//                        .where(
//                                nameEq(condition.getPocketMonName()),
//                                pocketMonMasterNameEq(condition.getPocketMonMasterName()),
//                                levelGoe(condition.getLevelGoe()),
//                                levelLoe(condition.getLevelLoe())
//                        ));
//
//    }






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
