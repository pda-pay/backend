package org.ofz.management.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.ofz.user.User;


@Entity
@Getter
@Table(name = "stock")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Stock {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int quantity;
    private String accountNumber;
    private String companyCode;
    private String stockCode;
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public void minusQuantity(int deductionQuantity) {
        this.quantity -= deductionQuantity;
    }
}
