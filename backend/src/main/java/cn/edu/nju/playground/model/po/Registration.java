package cn.edu.nju.playground.model.po;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import cn.edu.nju.playground.enums.RegistrationStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "registrations",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_activity_user", columnNames = {"activityId", "userId"})
        },
        indexes = {
                @Index(name = "idx_registration_activity_status", columnList = "activityId, status"),
                @Index(name = "idx_registration_user", columnList = "userId"),
                @Index(name = "idx_registration_waitlist", columnList = "activityId, isWaitlist")
        }
)
public class Registration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "activityId", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Activity activity;

    @JoinColumn(name = "userId", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RegistrationStatus status;

    @Column(nullable = false, precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal feeAmount = BigDecimal.ZERO;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isWaitlist = false;

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime registeredAt;

    private LocalDateTime cancelledAt;
}