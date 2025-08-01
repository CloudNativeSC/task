package com.project.rooton.domain.task.entity;

import com.project.rooton.domain.task.enums.*;
import jakarta.persistence.*;
import lombok.*;
import com.project.rooton.global.entity.BaseTimeEntity;
import com.project.rooton.domain.schedule.entity.ScheduleBlock;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime; //수동 추가

@Entity
@Table(name = "work_sessions", indexes = {
        @Index(name = "idx_work_sessions_user_id", columnList = "user_id"),
        @Index(name = "idx_work_sessions_task_id", columnList = "task_id"),
        @Index(name = "idx_work_sessions_start_time", columnList = "start_time")
})
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class WorkSession extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "session_id")
    private Long sessionId;

    @Column(name = "block_id")
    private Long blockId;

    @Enumerated(EnumType.STRING)
    @Column(name = "session_type", nullable = false)
    private SessionType sessionType;

    @Column(name = "duration_minutes", nullable = false)
    private Integer durationMinutes;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private SessionStatus status = SessionStatus.ACTIVE;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(columnDefinition = "JSON")
    private String interruptions;

    @Column(name = "focus_score")
    private Integer focusScore;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "task_id", nullable = false)
    private Long taskId;

    // N:1 관계 - 여러 WorkSession은 하나의 Task에 속함
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", insertable = false, updatable = false)
    private Task task;

    // 비즈니스 메소드
    public void completeSession() {
        this.status = SessionStatus.COMPLETED;
        this.endTime = LocalDateTime.now();
    }

    public void pauseSession() {
        this.status = SessionStatus.PAUSED;
    }

    public void cancelSession() {
        this.status = SessionStatus.CANCELLED;
        this.endTime = LocalDateTime.now();
    }

    public boolean isWorkSession() {
        return this.sessionType == SessionType.WORK;
    }

    public Integer getActualDurationMinutes() {
        if (endTime != null && startTime != null) {
            return (int) Duration.between(startTime, endTime).toMinutes();
        }
        return null;
    }
}
