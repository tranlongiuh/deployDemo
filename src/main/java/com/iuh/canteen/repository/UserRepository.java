package com.iuh.canteen.repository;

import com.iuh.canteen.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    String getUsernameByEmail(String email);

    Optional<User> findByEmail(String email);

    Optional<User> findByVerificationCode(String code);

    User findByUsername(String username);

    Boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByVerificationCode(String code);
}
