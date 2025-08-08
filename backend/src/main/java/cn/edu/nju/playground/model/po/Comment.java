package cn.edu.nju.playground.model.po;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "comments", indexes = {
        @Index(name = "idx_comment_activity", columnList = "activityId"),
        @Index(name = "idx_comment_user", columnList = "userId"),
        @Index(name = "idx_comment_parent", columnList = "parentId"),
        @Index(name = "idx_comment_created", columnList = "createdAt")
})
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "activityId", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Activity activity;

    @JoinColumn(name = "userId", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @JoinColumn(name = "parentId")
    @ManyToOne(fetch = FetchType.LAZY)
    private Comment parent;

    @Column(length = 1000)
    private String content;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
}