package org.ofz.payment.repository;

import org.ofz.payment.entity.PaymentHistory;
import org.ofz.repayment.dto.response.PaymentHistoriesResponse.PaymentHistoryDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PaymentHistoryRepository extends JpaRepository<PaymentHistory, Long> {

    @Query("SELECT new org.ofz.repayment.dto.response.PaymentHistoriesResponse$PaymentHistoryDTO(p.id, p.paymentAmount, p.createdAt, f.name) " +
            "FROM PaymentHistory p " +
            "JOIN Franchise f ON p.franchise.id = f.id " +
            "WHERE p.userId = :userId AND FUNCTION('MONTH', p.createdAt) = :month " +
            "ORDER BY p.createdAt DESC")
    List<PaymentHistoryDTO> findPaymentHistoryByUserIdAndMonth(@Param("userId") Long userId, @Param("month") int month);

    @Query("SELECT new org.ofz.repayment.dto.response.PaymentHistoriesResponse$PaymentHistoryDTO(p.id, p.paymentAmount, p.createdAt, f.name) " +
            "FROM PaymentHistory p " +
            "JOIN Franchise f ON p.franchise.id = f.id " +
            "WHERE p.userId = :userId " +
            "ORDER BY p.createdAt DESC")
    List<PaymentHistoryDTO> findPaymentHistoryByUserId(@Param("userId") Long userId, Pageable pageable);
}
