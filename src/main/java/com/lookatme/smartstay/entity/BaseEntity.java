package com.lookatme.smartstay.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class BaseEntity {

    @Column(updatable = false)
    @CreatedDate
    private LocalDateTime reg_date;

    @LastModifiedBy
    private LocalDateTime modi_date;

    @Column(updatable = false)
    @CreatedBy
    private String create_by;

    @LastModifiedBy
    private String modified_by;
}
