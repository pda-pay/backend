package org.ofz.management.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
@Table(name = "stock_information")
public class StockInformation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "stock_code")
    private String stockCode;
    private String name;
    private int stabilityLevel;
}
