package cn.edu.nju.playground.model.po;

import cn.edu.nju.playground.enums.PaymentMethod;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import cn.edu.nju.playground.enums.TransactionStatus;
import cn.edu.nju.playground.enums.TransactionType;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "transactions", indexes = {
        @Index(name = "idx_transaction_user_type", columnList = "userId, type"),
        @Index(name = "idx_transaction_created", columnList = "createdAt"),
        @Index(name = "idx_transaction_related", columnList = "relatedId"),
        @Index(name = "idx_transaction_external", columnList = "externalOrderId")
})
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "userId", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, length = 200)
    private String description;

    private Long relatedId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private TransactionStatus status = TransactionStatus.PENDING;

    @Column(length = 50)
    private PaymentMethod paymentMethod;

    @Column(length = 100)
    private String externalOrderId;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
}