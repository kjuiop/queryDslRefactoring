package com.gig.querydsl.repository;

import com.gig.querydsl.domain.PocketMon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PocketMonRepository extends JpaRepository<PocketMon, Long>, PocketMonRepositoryCustom {
    List<PocketMon> findByName(String name);
}
