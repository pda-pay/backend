package org.ofz.payment.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.List;

@Entity
@Getter
@Table(name = "Owner")
public class Owner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "account_number")
    private String accountNumber;

    @JsonIgnore
    @OneToMany(mappedBy = "owner")
    private List<Franchise> franchises;

    public Owner() {}

    public Owner(Long id, String name, String phoneNumber, String accountNumber, List<Franchise> franchises) {
        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.accountNumber = accountNumber;
        this.franchises = franchises;
    }
}
