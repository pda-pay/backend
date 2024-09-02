package org.ofz.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByLoginId(String loginId);
    boolean existsByLoginId(String loginId);
    boolean existsByPhoneNumber(String phoneNumber);

    @Query("SELECT u.name AS name, u.phoneNumber AS phoneNumber FROM User u WHERE u.loginId = :loginId")
    Optional<NameAndPhoneNumberProjection> findNameAndPhoneNumberByLoginId(@Param("loginId") String loginId);
}
