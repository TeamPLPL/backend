package com.kosa.backend.common.entity;

import jakarta.persistence.*;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/*
    엔티티의 생성 및 수정 정보를 감사(audit)하는 역할을 할 때 사용되는 공통 entity
    created_at과 updated_at 필드를 관리하는 목적

    추가시 Application.java에 @EnableJpaAuditing 추가해야함.
*/
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
public class Auditable {
    /*
        TemporalType.Date : 년-월-일
        TemporalType.Date : 시-분-초
        TemporalType.TIMESTAMP : 년-월-일-시-분-초
     */
    @Column(nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    private LocalDateTime createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    @LastModifiedDate
    private LocalDateTime updatedAt;
}
