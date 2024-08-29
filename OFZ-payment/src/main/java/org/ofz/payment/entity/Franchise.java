package org.ofz.payment.entity;

import jakarta.persistence.*;
import lombok.Getter;

import java.util.List;

@Entity
@Getter
@Table(name = "Franchise")
public class Franchise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @Column(unique = true)
    private int code;

    @OneToMany(mappedBy = "franchise")
    private List<PaymentHistory> paymentHistories;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private Owner owner;

    public Franchise() {}

    public Franchise(Long id, String name, int code, List<PaymentHistory> paymentHistories, Owner owner) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.paymentHistories = paymentHistories;
        this.owner = owner;
    }
}
