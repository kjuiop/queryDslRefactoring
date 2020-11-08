package com.gig.querydsl.repository;

import com.gig.querydsl.domain.PocketMon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PocketMonRepository extends JpaRepository<PocketMon, Long>, PocketMonRepositoryCustom, QuerydslPredicateExecutor {
    List<PocketMon> findByName(String name);
}
