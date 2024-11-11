package com.kosa.backend.user.entity;

import com.kosa.backend.common.entity.Auditable;
import jakarta.persistence.*;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name="ADDRESS")
public class Address extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id", updatable=false)
    private int id;

    // 우편번호
    @Column(nullable = false)
    private String zonecode;

    // 도로명주소
    @Column(nullable = false)
    private String addr;

    // 도로명주소 영문
    @Column
    private String addrEng;

    // 상세주소
    @Column(nullable = false)
    private String detailAddr;

    // 기본 주소 여부
    @Column(nullable = false)
    private boolean isDefault;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
