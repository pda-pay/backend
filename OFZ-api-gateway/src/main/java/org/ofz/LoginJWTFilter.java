package org.ofz;

import org.ofz.jwt.JwtTokenProvider;
import org.ofz.user.User;
import org.ofz.user.UserRepository;
import org.springframework.boot.web.server.Cookie;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;


@Component
public class LoginJWTFilter extends AbstractGatewayFilterFactory<LoginJWTFilter.Config> {
    public static class Config {}
    public LoginJWTFilter(JwtTokenProvider jwtTokenProvider, UserRepository userRepository) {
        super(Config.class);
        this.jwtTokenProvider = jwtTokenProvider;
        this.userRepository = userRepository;
    }

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {

            String token = getToken(exchange.getRequest());

            if(token == null) return onError(exchange);
            if(!jwtTokenProvider.validateToken(token)) return onError(exchange);

            String loginId = jwtTokenProvider.parseUserId(token);

            if(!userRepository.existsByLoginId(loginId)) return onError(exchange);

            User user = userRepository.findByLoginId(loginId).orElseThrow(RuntimeException::new);

            ServerHttpRequest request = exchange.getRequest().mutate()
                    .header("X-USER-ID", user.getId().toString())
                    .header("X-LOGIN-ID", loginId)
                    .build();


            exchange.mutate().request(request).build();

            return chain.filter(exchange);
        };
    }

    private String getToken(ServerHttpRequest request){
        HttpCookie cookie = request.getCookies().getFirst("accessToken");

        if(cookie == null) return null;

        return cookie.getValue();
    }

    private Mono<Void> onError(ServerWebExchange exchange){
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatusCode.valueOf(401));
        return response.setComplete();
    }
}
