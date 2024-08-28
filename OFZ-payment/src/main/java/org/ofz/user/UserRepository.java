package org.ofz.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByLoginId(String loginId);
    Optional<User> findByPhoneNumber(String phoneNumber);
    boolean existsByLoginId(String loginId);
    boolean existsByPhoneNumber(String phoneNumber);
}
