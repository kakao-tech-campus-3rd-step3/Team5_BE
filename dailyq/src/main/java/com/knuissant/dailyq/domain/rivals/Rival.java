package com.knuissant.dailyq.domain.rivals;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.knuissant.dailyq.domain.users.User;
import com.knuissant.dailyq.exception.BusinessException;
import com.knuissant.dailyq.exception.ErrorCode;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
@Table(name = "rivals",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_rivals_pair",
                        columnNames = {"sender_id", "receiver_id"}
                )
        },
        indexes = {
                @Index(name = "idx_rivals_receiver_status", columnList = "receiver_id, status")
        }
)
public class Rival {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rival_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiver;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private RivalStatus status;

    public static Rival create(User sender, User receiver) {
        return Rival.builder()
                .sender(sender)
                .receiver(receiver)
                .status(RivalStatus.WAITING)
                .build();
    }

    public void accept() {
        if (this.status != RivalStatus.WAITING) {
            throw new BusinessException(ErrorCode.INVALID_RIVAL_REQUEST_STATUS);
        }
        this.status = RivalStatus.ACCEPTED;
    }

}
