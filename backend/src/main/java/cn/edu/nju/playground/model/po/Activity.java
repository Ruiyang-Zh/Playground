package cn.edu.nju.playground.model.po;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import cn.edu.nju.playground.enums.SportsType;
import cn.edu.nju.playground.enums.ActivityStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "activities", indexes = {
        @Index(name = "idx_activity_type_time", columnList = "type, startTime"),
        @Index(name = "idx_activity_location", columnList = "province, city, district"),
        @Index(name = "idx_activity_status", columnList = "status"),
        @Index(name = "idx_activity_creator", columnList = "creatorId")
})
public class Activity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "creatorId", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private User creator;

    @Column(nullable = false)
    private String contactInfo;

    @Column(nullable = false, length = 200)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SportsType type;

    @Lob
    @Column(nullable = false)
    private String description;

    @ElementCollection
    @Builder.Default
    private List<String> images = List.of();

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;

    @Column(nullable = false, length = 50)
    private String province;

    @Column(nullable = false, length = 50)
    private String city;

    @Column(nullable = false, length = 50)
    private String district;

    @Column(nullable = false, length = 500)
    private String venue;

    @Column(nullable = false)
    private Integer minParticipants;

    @Column(nullable = false)
    private Integer maxParticipants;

    @Column(nullable = false)
    @Builder.Default
    private Integer currentParticipants = 0;

    @Column(nullable = false, precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal fee = BigDecimal.ZERO;

    @Column(nullable = false)
    private LocalDateTime registrationDeadline;

    @Lob
    private String requirements;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private ActivityStatus status = ActivityStatus.RECRUITING;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}