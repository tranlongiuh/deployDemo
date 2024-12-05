package com.iuh.canteen.repository;

import com.iuh.canteen.entity.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    PasswordResetToken findByToken(String token);

    @Modifying
    @Query("DELETE FROM PasswordResetToken p WHERE p.email = :email")
    int deleteByEmail(@Param("email") String email);

    boolean existsByToken(String token);
}

