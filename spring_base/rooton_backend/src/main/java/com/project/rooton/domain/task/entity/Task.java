package com.project.rooton.domain.task.entity;

import com.project.rooton.domain.task.enums.Priority;
import com.project.rooton.domain.task.enums.RecurringPattern;
import com.project.rooton.domain.task.enums.TaskStatus;
import jakarta.persistence.*;
import lombok.*;
import com.project.rooton.global.entity.BaseTimeEntity;
import com.project.rooton.domain.schedule.entity.ScheduleBlock;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime; //수동 추가



@Entity
@Table(name = "tasks", indexes = {
        @Index(name = "idx_tasks_user_id", columnList = "user_id"),
        @Index(name = "idx_tasks_status", columnList = "status"),
        @Index(name = "idx_tasks_due_at", columnList = "due_at")
})
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Task extends BaseTimeEntity {

    @Id
    private String id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "category_id", nullable = false)
    private String categoryId;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Priority priority = Priority.MEDIUM;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private TaskStatus status = TaskStatus.TODO;

    @Column(name = "estimated_pomodoros")
    private Integer estimatedPomodoros;

    @Column(name = "completed_pomodoros")
    @Builder.Default
    private Integer completedPomodoros = 0;

    @Column(name = "due_at")
    private LocalDateTime dueAt;

    @Column(name = "is_recurring")
    @Builder.Default
    private Boolean isRecurring = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "recurring_pattern")
    private RecurringPattern recurringPattern;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    // N:1 관계 - 여러 Task는 하나의 Category에 속함
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", insertable = false, updatable = false)
    private Category category;

    // 1:N 관계 - 하나의 Task는 여러 WorkSession을 가질 수 있음
    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WorkSession> workSessions = new ArrayList<>();

    // 1:N 관계 - 하나의 Task는 여러 ScheduleBlock에서 사용될 수 있음
    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL)
    private List<ScheduleBlock> scheduleBlocks = new ArrayList<>();

    // 비즈니스 메소드
    public void markAsCompleted() {
        this.status = TaskStatus.COMPLETED;
        this.completedAt = LocalDateTime.now();
    }

    public void addCompletedPomodoro() {
        this.completedPomodoros = (this.completedPomodoros == null ? 0 : this.completedPomodoros) + 1;
    }

    public void updateStatus(TaskStatus newStatus) {
        this.status = newStatus;
        if (newStatus == TaskStatus.COMPLETED && this.completedAt == null) {
            this.completedAt = LocalDateTime.now();
        }
    }
}

