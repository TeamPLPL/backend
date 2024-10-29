package com.kosa.backend.funding.support.entity;

import com.kosa.backend.common.entity.Auditable;
import com.kosa.backend.funding.project.entity.Funding;
import com.kosa.backend.user.entity.User;
import jakarta.persistence.*;

@Entity
@Table(name = "WISHLIST")
public class Wishlist extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "funding_id", nullable = false)
    private Funding funding;

    // getters and setters
}
