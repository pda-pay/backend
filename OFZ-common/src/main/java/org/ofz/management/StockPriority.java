package org.ofz.management;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import org.ofz.user.User;

@Entity
@Getter
@Table(name = "stock_priority")
public class StockPriority {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int stockRank;
    private int quantity;
    private String accountNumber;
    private String companyCode;
    private String stockCode;
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public StockPriority() {}

    @Builder
    public StockPriority(String accountNumber, int stockRank, String stockCode, int quantity, String companyCode, User user) {
        this.accountNumber = accountNumber;
        this.quantity = quantity;
        this.companyCode = companyCode;
        this.stockCode = stockCode;
        this.stockRank = stockRank;
        this.user = user;
    }
}
