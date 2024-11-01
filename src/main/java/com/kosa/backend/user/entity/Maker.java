package com.kosa.backend.user.entity;

import com.kosa.backend.common.entity.Auditable;
import jakarta.persistence.*;

@Entity
@Table(name = "MAKER")
public class Maker extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String userContent;
}
