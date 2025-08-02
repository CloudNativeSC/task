package com.project.rooton.domain.task.entity;


import com.project.rooton.domain.task.enums.Priority;
import com.project.rooton.domain.task.enums.RecurringPattern;
import com.project.rooton.domain.task.enums.TaskStatus;
import com.project.rooton.domain.task.enums.TemplateType;
import jakarta.persistence.*;
import lombok.*;
import com.project.rooton.global.entity.BaseTimeEntity;
import com.project.rooton.domain.schedule.entity.ScheduleBlock;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime; //수동 추가

@Entity
@Table(name = "task_templates")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class TaskTemplate extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "template_name", nullable = false, length = 100)
    private String templateName;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "estimated_pomodoros")
    private Integer estimatedPomodoros;

    @Column(columnDefinition = "JSON")
    private String tags;

    @Column(name = "is_ai_generated")
    @Builder.Default
    private Boolean isAiGenerated = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "template_type")
    @Builder.Default
    private TemplateType templateType = TemplateType.CUSTOM;

    @Column(name = "usage_count")
    @Builder.Default
    private Integer usageCount = 0;

    @Column(name = "category_id", nullable = false)
    private Long categoryId;

    // N:1 관계 - 여러 TaskTemplate은 하나의 Category에 속함
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", insertable = false, updatable = false)
    private Category category;

    // 비즈니스 메소드
    public void incrementUsage() {
        this.usageCount = (this.usageCount == null ? 0 : this.usageCount) + 1;
    }

    public Task createTaskFromTemplate(String userId, String taskId) {
        return Task.builder()
                .id(taskId)
                .userId(userId)
                .categoryId(String.valueOf(this.categoryId))
                .title(this.title)
                .description(this.description)
                .estimatedPomodoros(this.estimatedPomodoros)
                .build();

    }
}