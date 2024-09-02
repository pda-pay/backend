package org.ofz.management;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import org.ofz.user.User;

@Entity
@Getter
@Table(name = "mortgaged_stock")
public class MortgagedStock {
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

    public MortgagedStock() {}

    @Builder
    public MortgagedStock(int quantity, String accountNumber, String companyCode, String stockCode, User user) {
        this.quantity = quantity;
        this.accountNumber = accountNumber;
        this.companyCode = companyCode;
        this.stockCode = stockCode;
        this.user = user;
    }
}
