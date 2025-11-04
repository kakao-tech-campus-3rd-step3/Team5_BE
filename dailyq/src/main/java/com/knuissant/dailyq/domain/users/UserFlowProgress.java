package com.knuissant.dailyq.domain.users;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.knuissant.dailyq.domain.common.BaseTimeEntity;
import com.knuissant.dailyq.domain.questions.FlowPhase;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
@Table(name = "user_flow_progress")
public class UserFlowProgress extends BaseTimeEntity {

    @Id
    @Column(name = "user_id")
    private Long userId;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "fk_flow_progress_user"))
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "next_phase", nullable = false, columnDefinition = "VARCHAR(20)")
    private FlowPhase nextPhase;

    /**
     * 다음 phase로 진행합니다.
     */
    public void moveToNextPhase() {
        FlowPhase nextPhase = this.nextPhase.next();
        if (nextPhase != null) {
            this.nextPhase = nextPhase;
        }
    }
}


