package com.lookatme.smartstay.repository;

import com.lookatme.smartstay.dto.PageRequestDTO;
import com.lookatme.smartstay.entity.Notice;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;

public interface NoticeRepository extends JpaRepository<Notice, Long> {

}


