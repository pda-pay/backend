package org.ofz.payment.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
@Table(name = "User")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "login_id")
    private String loginId;

    private String password;
    private String name;

    @Column(name = "phone_number")
    private String phoneNumber;

    public User() {}

    public User(Long id, String loginId, String password, String name, String phoneNumber) {
        this.id = id;
        this.loginId = loginId;
        this.password = password;
        this.name = name;
        this.phoneNumber = phoneNumber;
    }
}
