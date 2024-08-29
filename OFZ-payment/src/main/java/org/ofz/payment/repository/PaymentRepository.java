package org.ofz.payment.repository;

import org.ofz.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findPaymentByUserId(Long userId);
}
