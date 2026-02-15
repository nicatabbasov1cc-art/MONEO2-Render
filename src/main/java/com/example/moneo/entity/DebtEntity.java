package com.example.moneo.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "debts")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DebtEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private BigDecimal totalAmount;
    private BigDecimal remainingAmount;
    private BigDecimal monthlyPayment;
    private LocalDate startDate;
    private LocalDate dueDate;
    private Integer durationMonths;
    private String icon;
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}