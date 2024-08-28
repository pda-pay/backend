package org.ofz.payment.service;

import lombok.RequiredArgsConstructor;
import org.ofz.payment.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;


}
