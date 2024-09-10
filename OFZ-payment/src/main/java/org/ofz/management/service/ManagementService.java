package org.ofz.management.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.ofz.management.entity.MortgagedStock;
import org.ofz.management.entity.StockPriority;
import org.ofz.management.dto.api.request.*;
import org.ofz.management.dto.common.MortgagedStockDto;
import org.ofz.management.dto.common.StockMortgagedStockDto;
import org.ofz.management.dto.common.StockPriorityDto;
import org.ofz.management.dto.common.AccountDto;
import org.ofz.management.dto.api.response.*;
import org.ofz.management.projection.UserStockProjection;
import org.ofz.management.dto.partners.request.UserAccountRequest;
import org.ofz.management.dto.partners.request.UserAccountsRequest;
import org.ofz.management.dto.partners.response.UserAccountResponse;
import org.ofz.management.dto.partners.response.UserAccountsResponse;
import org.ofz.management.entity.StockInformation;
import org.ofz.management.exception.StockInformationNotFoundException;
import org.ofz.management.exception.UserNotFoundException;
import org.ofz.management.repository.MortgagedStockRepository;
import org.ofz.management.repository.StockInformationRepository;
import org.ofz.management.repository.StockPriorityRepository;
import org.ofz.management.repository.StockRepository;
import org.ofz.management.utils.BankCategory;
import org.ofz.management.utils.SecuritiesCategory;
import org.ofz.management.utils.StockStability;
import org.ofz.payment.Payment;
import org.ofz.payment.PaymentRepository;
import org.ofz.payment.exception.payment.PaymentNotFoundException;
import org.ofz.user.NameAndPhoneNumberProjection;
import org.ofz.user.User;
import org.ofz.user.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.util.*;

@RequiredArgsConstructor
@Service
public class ManagementService {
    private final UserRepository userRepository;
    private final StockRepository stockRepository;
    private final MortgagedStockRepository mortgagedStockRepository;
    private final StockPriorityRepository stockPriorityRepository;
    private final PaymentRepository paymentRepository;
    private final StockInformationRepository stockInformationRepository;
    private final WebClient webClient;
    private final ManagementCacheService cacheService;
    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${webclient.base-url}")
    private String baseUrl;

    @Transactional
    public UserStockResponse getUserStocks(String userLoginId) {
        PaymentUser paymentUser = checkPaymentUser(userLoginId);
        User user = paymentUser.getUser();
        Long userId = user.getId();

        UserStockResponse userStockResponse = new UserStockResponse(new ArrayList<>(), 0);
        List<UserStockProjection> UserStockProjections = stockRepository.findUserStocksByUserId(userId);
        List<MortgagedStockDto> mortgagedStockDtos = cacheService.getCachedMortgagedStocks(userLoginId);

        for (UserStockProjection userStockProjection : UserStockProjections) {
            final String stockCode = userStockProjection.getStockCode();
            final String companyCode = userStockProjection.getCompanyCode();
            final int previousStockPrice = fetchStoredPrice(stockCode);
            StockInformation stockInformation = stockInformationRepository.findByStockCode(stockCode)
                    .orElseThrow(() -> new StockInformationNotFoundException("증권 정보를 찾지 못했음."));
            final int stabilityLevel = stockInformation.getStabilityLevel();
            int mortgagedQuantity = 0;

            if (paymentUser.isJoined()) {
                mortgagedQuantity = userStockProjection.getMortgagedQuantity();
            } else {
                mortgagedQuantity = checkCacheMortgagedStock(mortgagedStockDtos, userStockProjection.getStockCode(), userStockProjection.getAccountNumber());
            }

            StockMortgagedStockDto stockMortgagedStockDto = StockMortgagedStockDto.builder()
                    .accountNumber(userStockProjection.getAccountNumber())
                    .quantity(userStockProjection.getQuantity())
                    .mortgagedQuantity(mortgagedQuantity)
                    .stockCode(stockCode)
                    .stockName(stockInformation.getName())
                    .companyCode(companyCode)
                    .companyName(SecuritiesCategory.getCompanyNamefromCode(companyCode))
                    .stabilityLevel(stabilityLevel)
                    .stockPrice(previousStockPrice)
                    .limitPrice(StockStability.calculateLimitPrice(stabilityLevel, previousStockPrice))
                    .build();
            userStockResponse.addStockMortgagedStock(stockMortgagedStockDto);
        }

        final int totalDebt = paymentRepository.findTotalDebtByUserId(userId);
        userStockResponse.setTotalDebt(totalDebt);

        return userStockResponse;
    }

    @Transactional
    public SavedResponse saveMortgagedStockInformation(SaveMortgagedStockRequest saveMortgagedStockRequest) {
        PaymentUser paymentUser = checkPaymentUser(saveMortgagedStockRequest.getLoginId());
        User user = paymentUser.getUser();
        String loginId = saveMortgagedStockRequest.getLoginId();
        List<MortgagedStockDto> mortgagedStockDtos = saveMortgagedStockRequest.getMortgagedStocks();

        if (paymentUser.isJoined()) {
            Payment payment = paymentUser.getPayment();
            List<MortgagedStock> mortgagedStocks = mortgagedStockRepository.findAllMortgagedStocksByUserId(user.getId());
            List<StockPriority> stockPriorities = stockPriorityRepository.findStockPrioritiesByUserIdOrderByStockRank(user.getId());
            payment.changeCreditLimit(0);
            stockPriorityRepository.deleteAll(stockPriorities);
            mortgagedStockRepository.deleteAll(mortgagedStocks);
            paymentRepository.save(payment);

            saveMortgagedStocks(mortgagedStockDtos, user);
        } else {
            cacheService.cacheMortgagedStocks(loginId, mortgagedStockDtos);
            cacheService.deleteCachedStockPriorities(loginId);
            cacheService.deleteCachedLimit(loginId);
        }

        return SavedResponse.success(saveMortgagedStockRequest.getLoginId());
    }

    @Transactional
    public UserMortgagedStockStockPriorityResponse getUserMortgagedStockStockPriority(String userLoginId) {
        PaymentUser paymentUser = checkPaymentUser(userLoginId);
        User user = paymentUser.getUser();

        if (paymentUser.isJoined()) {
            List<MortgagedStock> mortgagedStocks = mortgagedStockRepository.findAllMortgagedStocksByUserId(user.getId());
            List<StockPriority> stockPriorities = stockPriorityRepository.findAllStockPrioritiesByUserId(user.getId());
            UserMortgagedStockStockPriorityResponse userMortgagedStockStockPriorityResponse = new UserMortgagedStockStockPriorityResponse(new ArrayList<>(), new ArrayList<>());

            for (MortgagedStock mortgagedStock : mortgagedStocks) {
                final String stockCode = mortgagedStock.getStockCode();
                final String companyCode = mortgagedStock.getCompanyCode();
                final int previousStockPrice = fetchStoredPrice(stockCode);
                StockInformation stockInformation = stockInformationRepository.findByStockCode(stockCode)
                        .orElseThrow(() -> new StockInformationNotFoundException("증권 정보를 찾지 못했음."));
                final int stabilityLevel = stockInformation.getStabilityLevel();
                MortgagedStockDto mortgagedStockDto = MortgagedStockDto.builder()
                        .accountNumber(mortgagedStock.getAccountNumber())
                        .quantity(mortgagedStock.getQuantity())
                        .stockCode(mortgagedStock.getStockCode())
                        .stockName(stockInformation.getName())
                        .companyCode(companyCode)
                        .companyName(SecuritiesCategory.getCompanyNamefromCode(companyCode))
                        .stabilityLevel(stabilityLevel)
                        .stockPrice(previousStockPrice)
                        .limitPrice(StockStability.calculateLimitPrice(stabilityLevel, previousStockPrice))
                        .build();

                userMortgagedStockStockPriorityResponse.addMortgagedStock(mortgagedStockDto);
            }

            for (StockPriority stockPriority : stockPriorities) {
                final String stockCode = stockPriority.getStockCode();
                final int previousStockPrice = fetchStoredPrice(stockCode);
                final String companyCode = stockPriority.getCompanyCode();
                StockInformation stockInformation = stockInformationRepository.findByStockCode(stockCode)
                        .orElseThrow(() -> new StockInformationNotFoundException("증권 정보를 찾지 못했음."));
                final int stabilityLevel = stockInformation.getStabilityLevel();

                StockPriorityDto stockPriorityDto = StockPriorityDto.builder()
                        .accountNumber(stockPriority.getAccountNumber())
                        .quantity(stockPriority.getQuantity())
                        .stockCode(stockPriority.getStockCode())
                        .stockName(stockInformation.getName())
                        .stockRank(stockPriority.getStockRank())
                        .companyCode(companyCode)
                        .companyName(SecuritiesCategory.getCompanyNamefromCode(companyCode))
                        .stabilityLevel(stabilityLevel)
                        .stockPrice(previousStockPrice)
                        .limitPrice(StockStability.calculateLimitPrice(stabilityLevel, previousStockPrice))
                        .build();

                userMortgagedStockStockPriorityResponse.addStockPriority(stockPriorityDto);
            }

            return userMortgagedStockStockPriorityResponse;
        } else {
            List<MortgagedStockDto> mortgagedStockDtos = cacheService.getCachedMortgagedStocks(userLoginId);
            return new UserMortgagedStockStockPriorityResponse(mortgagedStockDtos, new ArrayList<>());
        }
    }

    @Transactional
    public SavedResponse saveStockPriorityInformation(SaveStockPriorityRequest saveStockPriorityRequest) {
        PaymentUser paymentUser = checkPaymentUser(saveStockPriorityRequest.getLoginId());
        User user = paymentUser.getUser();
        List<StockPriorityDto> stockPriorityDtos = saveStockPriorityRequest.getStockPriorities();

        if (paymentUser.isJoined()) {
            List<StockPriority> stockPriorities = stockPriorityRepository.findAllStockPrioritiesByUserId(user.getId());
            stockPriorityRepository.deleteAll(stockPriorities);
            saveStockPriorities(stockPriorityDtos, user);
        } else {
            cacheService.cacheStockPriorities(user.getLoginId(), stockPriorityDtos);
        }

        return SavedResponse.success(saveStockPriorityRequest.getLoginId());
    }

    @Transactional
    public UserLimitResponse getUserLimitInformation(String userLoginId) {
        final PaymentUser paymentUser = checkPaymentUser(userLoginId);
        final User user = paymentUser.getUser();
        final long userId = user.getId();
        double totalLimit = 0;
        int totalMortgagedPrice = 0;
        int currentLimit = 0;
        int totalPaymentAmount = 0;

        if (paymentUser.isJoined()) {
            Payment payment = paymentUser.getPayment();
            List<MortgagedStock> mortgagedStocks = mortgagedStockRepository.findAllMortgagedStocksByUserId(user.getId());
            for (MortgagedStock mortgagedStock : mortgagedStocks) {
                final String stockCode = mortgagedStock.getStockCode();
                final int stockPreviousPrice = fetchStoredPrice(stockCode);
                final int mortgagedStockQuantity = mortgagedStock.getQuantity();
                StockInformation stockInformation = stockInformationRepository.findByStockCode(stockCode)
                        .orElseThrow(() -> new StockInformationNotFoundException("증권 정보를 찾지 못했음."));
                final double stockMaxLimit = StockStability.calculateLimitPrice(stockInformation.getStabilityLevel(), stockPreviousPrice);
                totalLimit += stockMaxLimit * mortgagedStockQuantity;
                totalMortgagedPrice += stockPreviousPrice * mortgagedStockQuantity;
            }
            currentLimit = paymentUser.getPayment().getCreditLimit();
            totalPaymentAmount = payment.getPreviousMonthDebt() + payment.getCurrentMonthDebt();

            return UserLimitResponse.builder()
                    .currentLimit(currentLimit)
                    .totalLimit(totalLimit)
                    .totalMortgagedPrice(totalMortgagedPrice)
                    .totalPaymentAmount(totalPaymentAmount)
                    .build();
        } else {
            List<MortgagedStockDto> mortgagedStockDtos = cacheService.getCachedMortgagedStocks(userLoginId);
            for (MortgagedStockDto mortgagedStockDto : mortgagedStockDtos) {
                final String stockCode = mortgagedStockDto.getStockCode();
                final int stockPreviousPrice = fetchStoredPrice(stockCode);
                final int mortgagedStockQuantity = mortgagedStockDto.getQuantity();
                StockInformation stockInformation = stockInformationRepository.findByStockCode(stockCode)
                        .orElseThrow(() -> new StockInformationNotFoundException("증권 정보를 찾지 못했음."));
                final double stockMaxLimit = StockStability.calculateLimitPrice(stockInformation.getStabilityLevel(), stockPreviousPrice);
                totalLimit += stockMaxLimit * mortgagedStockQuantity;
                totalMortgagedPrice += stockPreviousPrice * mortgagedStockQuantity;
            }

            currentLimit = (int) totalLimit;

            return UserLimitResponse.builder()
                    .currentLimit(currentLimit)
                    .totalLimit(totalLimit)
                    .totalMortgagedPrice(totalMortgagedPrice)
                    .totalPaymentAmount(totalPaymentAmount)
                    .build();
        }
    }

    @Transactional
    public SavedResponse saveLimitInformation(SaveLimitInformationRequest saveLimitInformationRequest) {
        final String userLoginId = saveLimitInformationRequest.getLoginId();
        final PaymentUser paymentUser = checkPaymentUser(userLoginId);
        final User user = paymentUser.getUser();
        final int currentLimit = saveLimitInformationRequest.getCurrentLimit();

        if (paymentUser.isJoined()) {
            Payment payment = paymentUser.getPayment();
            payment.changeCreditLimit(currentLimit);
            payment.changePayFlag(true);
            payment.changeRateFlag(true);
            paymentRepository.save(payment);
        } else {
            cacheService.cacheLimit(userLoginId, currentLimit);
        }

        return SavedResponse.success(userLoginId);
    }

    @Transactional
    public UserAccountInformationResponse getUserAccountInformation(String userLoginId) {
        NameAndPhoneNumberProjection nameAndPhoneNumberProjection = userRepository.findNameAndPhoneNumberByLoginId(userLoginId)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾지 못했습니다."));
        UserAccountsRequest userAccountsRequest = new UserAccountsRequest(nameAndPhoneNumberProjection.getName(), nameAndPhoneNumberProjection.getPhoneNumber());
        UserAccountsResponse userAccountsResponse = fetchUserAccounts(userAccountsRequest);
        UserAccountInformationResponse userAccountInformationResponse = new UserAccountInformationResponse(new ArrayList<>());

        for (UserAccountsResponse.UserAccountDto userAccountDto : userAccountsResponse.getBankAccounts()) {
            final String companyCode = userAccountDto.getCompanyCode();
            userAccountInformationResponse.addAccount(AccountDto.builder()
                    .accountNumber(userAccountDto.getAccountNumber())
                    .companyCode(companyCode)
                    .companyName(BankCategory.fromCode(companyCode))
                    .category(userAccountDto.getCategory())
                    .build());
        }

        return userAccountInformationResponse;
    }

    @Transactional
    public SavedResponse saveRepaymentAccount(SaveRepaymentAccountRequest saveRepaymentAccountRequest) {
        final String userLoginId = saveRepaymentAccountRequest.getLoginId();
        final PaymentUser paymentUser = checkPaymentUser(userLoginId);
        final User user = paymentUser.getUser();
        final AccountDto accountDto = saveRepaymentAccountRequest.getRepaymentAccount();

        if (paymentUser.isJoined()) {
            Payment payment = getPaymentByUserId(user.getId());
            payment.changeRepaymentAccountNumber(accountDto.getAccountNumber());
            paymentRepository.save(payment);

        } else {
            cacheService.cacheAccount(userLoginId, accountDto);
        }

        return SavedResponse.success(userLoginId);
    }

    @Transactional
    public SavedResponse saveRepaymentDate(SaveRepaymentDateRequest saveRepaymentDateRequest) {
        final String userLoginId = saveRepaymentDateRequest.getLoginId();
        final PaymentUser paymentUser = checkPaymentUser(userLoginId);
        final User user = paymentUser.getUser();
        final int repaymentDate = saveRepaymentDateRequest.getRepaymentDate();

        if (paymentUser.isJoined()) {
            Payment payment = getPaymentByUserId(user.getId());
            payment.changeRepaymentDate(repaymentDate);
            paymentRepository.save(payment);

        } else {
            cacheService.cacheDate(userLoginId, repaymentDate);
        }

        return SavedResponse.success(userLoginId);
    }

    @Transactional
    public PaymentInformationResponse getPaymentInformation(String userLoginId) {
        final PaymentUser paymentUser = checkPaymentUser(userLoginId);
        final User user = paymentUser.getUser();

        if (paymentUser.isJoined()) {
            Payment payment = paymentUser.getPayment();
            String repaymentAccount = payment.getRepaymentAccountNumber();
            UserAccountResponse userAccountResponse = fetchUserAccount(new UserAccountRequest(repaymentAccount));
            String accountCompanyCode = userAccountResponse.getCompanyCode();
            AccountDto accountDto = AccountDto.builder()
                    .accountNumber(repaymentAccount)
                    .companyCode(accountCompanyCode)
                    .companyName(BankCategory.fromCode(accountCompanyCode))
                    .category("01")
                    .build();


            PaymentInformationResponse paymentInformationResponse = PaymentInformationResponse.builder()
                    .repaymentAccount(accountDto)
                    .repaymentDate(payment.getRepaymentDate())
                    .currentLimit(payment.getCreditLimit())
                    .mortgagedStocks(new ArrayList<>())
                    .stockPriorities(new ArrayList<>())
                    .build();

            List<MortgagedStock> mortgagedStocks = mortgagedStockRepository.findAllMortgagedStocksByUserId(user.getId());
            List<StockPriority> stockPriorities = stockPriorityRepository.findAllStockPrioritiesByUserId(user.getId());


            for (MortgagedStock mortgagedStock : mortgagedStocks) {
                final String stockCode = mortgagedStock.getStockCode();
                final String companyCode = mortgagedStock.getCompanyCode();
                final int previousStockPrice = fetchStoredPrice(stockCode);
                StockInformation stockInformation = stockInformationRepository.findByStockCode(stockCode)
                        .orElseThrow(() -> new StockInformationNotFoundException("증권 정보를 찾지 못했음."));
                final int stabilityLevel = stockInformation.getStabilityLevel();
                MortgagedStockDto mortgagedStockDto = MortgagedStockDto.builder()
                        .accountNumber(mortgagedStock.getAccountNumber())
                        .quantity(mortgagedStock.getQuantity())
                        .stockCode(mortgagedStock.getStockCode())
                        .stockName(stockInformation.getName())
                        .companyCode(companyCode)
                        .companyName(SecuritiesCategory.getCompanyNamefromCode(companyCode))
                        .stabilityLevel(stabilityLevel)
                        .stockPrice(previousStockPrice)
                        .limitPrice(StockStability.calculateLimitPrice(stabilityLevel, previousStockPrice))
                        .build();

                paymentInformationResponse.addMortgagedStock(mortgagedStockDto);
            }

            for (StockPriority stockPriority : stockPriorities) {
                final String stockCode = stockPriority.getStockCode();
                final int previousStockPrice = fetchStoredPrice(stockCode);
                final String companyCode = stockPriority.getCompanyCode();
                StockInformation stockInformation = stockInformationRepository.findByStockCode(stockCode)
                        .orElseThrow(() -> new StockInformationNotFoundException("증권 정보를 찾지 못했음."));
                final int stabilityLevel = stockInformation.getStabilityLevel();

                StockPriorityDto stockPriorityDto = StockPriorityDto.builder()
                        .accountNumber(stockPriority.getAccountNumber())
                        .quantity(stockPriority.getQuantity())
                        .stockCode(stockPriority.getStockCode())
                        .stockName(stockInformation.getName())
                        .stockRank(stockPriority.getStockRank())
                        .companyCode(companyCode)
                        .companyName(SecuritiesCategory.getCompanyNamefromCode(companyCode))
                        .stabilityLevel(stabilityLevel)
                        .stockPrice(previousStockPrice)
                        .limitPrice(StockStability.calculateLimitPrice(stabilityLevel, previousStockPrice))
                        .build();

                paymentInformationResponse.addStockPriority(stockPriorityDto);
            }

            return paymentInformationResponse;
        } else {
            return PaymentInformationResponse.builder()
                    .repaymentAccount(cacheService.getCachedAccount(userLoginId))
                    .repaymentDate(cacheService.getCachedDate(userLoginId))
                    .currentLimit(cacheService.getCachedLimit(userLoginId))
                    .mortgagedStocks(cacheService.getCachedMortgagedStocks(userLoginId))
                    .stockPriorities(cacheService.getCachedStockPriorities(userLoginId))
                    .build();
        }
    }

    @Transactional
    public CheckUserJoinedPaymentServiceResponse checkUserJoinedPaymentService(String userLoginId) {
        final PaymentUser paymentUser = checkPaymentUser(userLoginId);
        final User user = paymentUser.getUser();

        if (paymentUser.isJoined()) {
            Payment payment = paymentUser.getPayment();
            boolean isPaymentAccess = payment.isPayFlag() && payment.isRateFlag();
            return new CheckUserJoinedPaymentServiceResponse(userLoginId, user.getName(), paymentUser.isJoined(), isPaymentAccess);
        } else {
           return new CheckUserJoinedPaymentServiceResponse(userLoginId, user.getName(), false, false);
        }
    }

    @Transactional
    public SavedResponse joinPaymentService(JoinPaymentServiceRequest joinPaymentServiceRequest) {
        final String userLoginId = joinPaymentServiceRequest.getLoginId();
        final User user = findUserbyLoginId(userLoginId);
        Payment payment = Payment.builder()
                .user(user)
                .creditLimit(cacheService.getCachedLimit(userLoginId))
                .repaymentDate(cacheService.getCachedDate(userLoginId))
                .password(joinPaymentServiceRequest.getPaymentPassword())
                .repaymentAccountNumber(cacheService.getCachedAccount(userLoginId).getAccountNumber())
                .build();

        paymentRepository.save(payment);
        saveMortgagedStocks(cacheService.getCachedMortgagedStocks(userLoginId), user);
        saveStockPriorities(cacheService.getCachedStockPriorities(userLoginId), user);

        return SavedResponse.success(userLoginId);
    }

    private int checkCacheMortgagedStock(List<MortgagedStockDto> mortgagedStockDtos, String stockCode, String accountNumber) {
        if (mortgagedStockDtos == null) {
            return 0;
        }

        for (MortgagedStockDto mortgagedStockDto : mortgagedStockDtos) {
            if (mortgagedStockDto.getStockCode() == stockCode
                    && mortgagedStockDto.getAccountNumber() == accountNumber) {
                return mortgagedStockDto.getQuantity();
            }
        }

        return 0;
    }

    private Payment getPaymentByUserId(Long userId) {
        return paymentRepository.findPaymentByUserId(userId)
                .orElseThrow(() -> new PaymentNotFoundException("결제 서비스 이용자를 찾지 못했습니다."));
    }

    private User findUserbyLoginId(String userId) {
        return userRepository.findByLoginId(userId)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾지 못했습니다."));
    }

    private UserAccountsResponse fetchUserAccounts(UserAccountsRequest userAccountsRequest) {
        Mono<UserAccountsResponse> userAccountsResponseMono = webClient.post()
                .uri("{baseUrl}/mydata/accounts", baseUrl)
                .bodyValue(userAccountsRequest)
                .retrieve()
                .bodyToMono(UserAccountsResponse.class);

        UserAccountsResponse userAccountsResponse = userAccountsResponseMono.block();

        return userAccountsResponse;
    }

    private UserAccountResponse fetchUserAccount(UserAccountRequest userAccountRequest) {
        Mono<UserAccountResponse> userAccountResponseMono = webClient.post()
                .uri("{baseUrl}/mydata/accounts/deposits", baseUrl)
                .bodyValue(userAccountRequest)
                .retrieve()
                .bodyToMono(UserAccountResponse.class);

        UserAccountResponse userAccountResponse = userAccountResponseMono.block();

        return userAccountResponse;
    }

    private void saveMortgagedStocks(List<MortgagedStockDto> mortgagedStockDtos, User user) {
        List<MortgagedStock> mortgagedStocks = new ArrayList<>();
        for (MortgagedStockDto mortgagedStockDto : mortgagedStockDtos) {
            mortgagedStocks.add(MortgagedStock.builder()
                    .accountNumber(mortgagedStockDto.getAccountNumber())
                    .quantity(mortgagedStockDto.getQuantity())
                    .stockCode(mortgagedStockDto.getStockCode())
                    .companyCode(mortgagedStockDto.getCompanyCode())
                    .user(user)
                    .build());
        }

        mortgagedStockRepository.saveAll(mortgagedStocks);
    }

    private void saveStockPriorities(List<StockPriorityDto> stockPriorityDtos, User user) {
        List<StockPriority> stockPriorities = new ArrayList<>();
        for (StockPriorityDto stockPriorityDto : stockPriorityDtos) {
            stockPriorities.add(StockPriority.builder()
                        .accountNumber(stockPriorityDto.getAccountNumber())
                        .stockRank(stockPriorityDto.getStockRank())
                        .quantity(stockPriorityDto.getQuantity())
                        .stockCode(stockPriorityDto.getStockCode())
                        .companyCode(stockPriorityDto.getCompanyCode())
                        .user(user)
                        .build());
        }

        stockPriorityRepository.saveAll(stockPriorities);
    }

    private Integer fetchStoredPrice(String stockCode) {
        String key = "price:" + stockCode;
        return (Integer) redisTemplate.opsForValue().get(key);
    }

    private PaymentUser checkPaymentUser(String loginId) {
        User user = findUserbyLoginId(loginId);
        PaymentUser paymentUser = new PaymentUser(true, null, user);

        Payment payment = paymentRepository.findPaymentByUserId(user.getId())
                .orElseGet(() -> {
                    paymentUser.setJoined(false);
                    return null;
                });
        paymentUser.setPayment(payment);

        return paymentUser;
    }

    @Setter
    @Getter
    @AllArgsConstructor
    private class PaymentUser {
        private boolean isJoined;
        private Payment payment;
        private User user;
    }
}