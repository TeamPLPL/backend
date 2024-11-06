package com.kosa.backend.common.entity;

import com.kosa.backend.common.entity.enums.ImgType;
import com.kosa.backend.funding.project.entity.Funding;
import com.kosa.backend.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@Entity
@Table(name = "FILES")
@NoArgsConstructor
@AllArgsConstructor
public class Files extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String path;

    @Column(nullable = false)
    private String originalNm;

    @Column(nullable = false)
    private String savedNm;

    @Enumerated(EnumType.STRING)
    private ImgType imgType;

    @ManyToOne
    @JoinColumn(name = "funding_id")
    private Funding funding;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
