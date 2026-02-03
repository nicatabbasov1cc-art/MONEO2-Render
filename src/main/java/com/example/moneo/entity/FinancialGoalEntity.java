package com.example.moneo.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "financial_goals")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FinancialGoalEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private BigDecimal targetAmount;
    private BigDecimal currentAmount;
    private String goalType;
    private LocalDate targetDate;

    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}