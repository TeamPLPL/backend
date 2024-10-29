package com.kosa.backend.funding.support.entity;

import com.kosa.backend.common.entity.AuditableEntity;
import com.kosa.backend.user.entity.MakerEntity;
import com.kosa.backend.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "FOLLOW")
public class FollowEntity extends AuditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "maker_id", nullable = false)
    private MakerEntity followedUser;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity followingUser;
}
