package com.kosa.backend.funding.support.entity;

import com.kosa.backend.common.entity.Auditable;
import com.kosa.backend.user.entity.Maker;
import com.kosa.backend.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "FOLLOW")
public class Follow extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "maker_id", nullable = false)
    private Maker followedUser;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User followingUser;
}
