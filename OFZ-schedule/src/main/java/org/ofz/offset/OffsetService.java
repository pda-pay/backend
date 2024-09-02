package org.ofz.offset;

import lombok.RequiredArgsConstructor;
import org.ofz.offset.dto.PaymentOverdueDebtDto;
import org.ofz.offset.projection.MortgagedStockProjection;
import org.ofz.offset.repository.MortgagedStockRepository;
import org.ofz.payment.Payment;
import org.ofz.payment.PaymentRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OffsetService {
    private final PaymentRepository paymentRepository;
    private final MortgagedStockRepository mortgagedStockRepository;

    public PaymentOverdueDebtDto getOverdueAndDebt(Long userId){
        Payment payment = paymentRepository
                .findPaymentByUserId(userId)
                .orElseThrow(() -> new RuntimeException("결제 정보를 찾을 수 없습니다."));
        return new PaymentOverdueDebtDto(payment.getPreviousMonthDebt(), payment.getOverdueDay());
    }

    public List<MortgagedStockProjection> getSortedMortgagedStock(Long userId){
        List<MortgagedStockProjection> sortedPriorityMortgagedStocks = mortgagedStockRepository.sortPriorityMortgage(userId);
        List<MortgagedStockProjection> sortedNonPriorityMortgagedStocks = mortgagedStockRepository.sortNonPriorityMortgage(userId);

        List<MortgagedStockProjection> sortedMortgagedStocks = new ArrayList<>();
        sortedMortgagedStocks.addAll(sortedPriorityMortgagedStocks);
        sortedMortgagedStocks.addAll(sortedNonPriorityMortgagedStocks);

        return sortedMortgagedStocks;
    }
}
