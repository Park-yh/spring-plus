package org.example.expert.domain.log.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Log {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long requesterId;

    @Column(nullable = false)
    private Long targetTodoId;

    @Column(nullable = false)
    private Long targetUserId;

    @CreatedDate
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;

    private Log(Long requesterId, Long targetTodoId, Long targetUserId) {
        this.requesterId = requesterId;
        this.targetTodoId = targetTodoId;
        this.targetUserId = targetUserId;
    }

    public static Log of(long requesterId, long targetTodoId, long targetUserId) {
        return new Log(requesterId, targetTodoId, targetUserId);
    }
}