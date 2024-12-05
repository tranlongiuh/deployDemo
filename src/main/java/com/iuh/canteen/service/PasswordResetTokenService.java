package com.iuh.canteen.service;

import com.iuh.canteen.entity.PasswordResetToken;
import org.springframework.stereotype.Service;

@Service
public interface PasswordResetTokenService {

    Boolean deleteByEmail(String email);

    Boolean saveToken(PasswordResetToken resetToken);

    Boolean existsByToken(String token);

    PasswordResetToken findByToken(String token);
}
