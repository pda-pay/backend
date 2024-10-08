package org.ofz.user;

import lombok.RequiredArgsConstructor;
import org.ofz.jwt.JwtTokenProvider;
import org.ofz.redis.RedisUtil;
import org.ofz.management.entity.Stock;
import org.ofz.management.repository.StockRepository;
import org.ofz.user.dto.*;
import org.ofz.user.exception.InvalidCredentialsException;
import org.ofz.user.exception.SignupDuplicationException;
import org.ofz.user.exception.SignupPartnerApiCallException;
import org.ofz.user.exception.SignupStockDataSaveException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;


@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final RedisUtil redisUtil;
    private final StockRepository stockRepository;
    private final WebClient webClient;

    @Value("${partner.api.signup-stock-data}")
    private String url;

    @Transactional
    public boolean isAvailableLoginId(UserValidateLoginIdReq userValidateLoginIdReq) {
        boolean isExist = userRepository.existsByLoginId(userValidateLoginIdReq.getLoginId());
        return !isExist;
    }

    @Transactional
    public void signup(UserSignupReq userSignupReq) {
        boolean isExistingLoginId = userRepository.existsByLoginId(userSignupReq.getLoginId());
        boolean isExistingPhoneNumber = userRepository.existsByPhoneNumber(userSignupReq.getPhoneNumber());

        if(isExistingLoginId && isExistingPhoneNumber) throw new SignupDuplicationException("회원가입 실패. 아이디 및 전화번호 중복");
        if(isExistingLoginId) throw new SignupDuplicationException("회원가입 실패. 아이디 중복");
        if(isExistingPhoneNumber) throw new SignupDuplicationException("회원가입 실패. 전화번호 중복");

        String encodedPassword = passwordEncoder.encode(userSignupReq.getPassword());
        User user = userSignupReq.toEntity(encodedPassword);
        user = userRepository.save(user);

        fetchAndSavePartnerDataAsync(user);
    }

    public void fetchAndSavePartnerDataAsync(User user) {

        UserSignupStockDataReq request = new UserSignupStockDataReq(user.getName(), user.getPhoneNumber());

        webClient.post()
                .uri(url)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(UserSignupStockDataRes.class)
                .doOnError(ex -> {
                    throw new SignupPartnerApiCallException("파트너 API 호출 중 오류 발생", ex);
                })
                .subscribe(response -> {
                    try {
                        saveStocks(response, user);
                    } catch (Exception e) {
                        throw new SignupStockDataSaveException("데이터 저장 중 오류 발생", e);
                    }
                });
    }

    private void saveStocks(UserSignupStockDataRes response, User user) {
        response.getAccounts().forEach(account -> {
            account.getStocks().forEach(stock -> {
                try {
                    Stock newStock = Stock.builder()
                            .quantity(stock.getQuantity())
                            .accountNumber(account.getAccountNumber())
                            .stockCode(stock.getStockCode())
                            .companyCode(account.getCompanyCode())
                            .user(user)
                            .build();
                    stockRepository.save(newStock);
                } catch (Exception e) {
                    throw new SignupStockDataSaveException("데이터 저장 중 오류 발생: " + e.getMessage(), e);
                }
            });
        });
    }

    @Transactional
    public UserLoginRes login(UserLoginReq userLoginReq){
        User user = userRepository.findByLoginId(userLoginReq.getLoginId())
                .filter(u -> passwordEncoder.matches(userLoginReq.getPassword(), u.getPassword()))
                .orElseThrow(() -> new InvalidCredentialsException("유효하지 않은 아이디입니다."));
        return UserLoginRes.builder()
                .loginId(user.getLoginId())
                .name(user.getName())
                .build();
    }

    @Transactional
    public void logout(String accessToken){
        Long expiration = jwtTokenProvider.getExpiration(accessToken);
        redisUtil.addBlackList(accessToken, expiration);
    }
}