package com.example.moneo.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "categories", indexes = {
        @Index(name = "idx_categories_user_id", columnList = "user_id"),
        @Index(name = "idx_categories_is_default", columnList = "isDefault"),
        @Index(name = "idx_categories_user_default", columnList = "user_id, isDefault")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String icon;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private boolean isDefault = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @ToString.Exclude
    private UserEntity user;

    @OneToMany(mappedBy = "categoryEntity", cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<TransactionEntity> transactions;

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }
}