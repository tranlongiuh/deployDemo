package com.iuh.canteen.service.impl;

import com.iuh.canteen.entity.PasswordResetToken;
import com.iuh.canteen.repository.PasswordResetTokenRepository;
import com.iuh.canteen.service.PasswordResetTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PasswordResetTokenServiceImpl implements PasswordResetTokenService {

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    /**
     * deleteByEmail Xóa Mã đặt lại mật khẩu theo Email đăng ký
     *
     * @param email Email đăng ký
     * @return true - xóa thành công
     */
    @Override
    public Boolean deleteByEmail(String email) {

        try {
            int result = passwordResetTokenRepository.deleteByEmail(email);
            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * saveToken Lưu Mã đặt lại mật khẩu
     *
     * @param resetToken Mã đặt lại mật khẩu
     * @return true - lưu thành công
     */
    @Override
    public Boolean saveToken(PasswordResetToken resetToken) {

        try {
            passwordResetTokenRepository.save(resetToken);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * existsByToken tồn tại bởi Mã xác thực
     *
     * @param token Mã xác thực
     * @return true - nếu tồn tại
     */
    @Override
    public Boolean existsByToken(String token) {

        try {
            return passwordResetTokenRepository.existsByToken(token);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * findByToken Lấy Mã đặt lại mật khẩu theo Mã xác thực
     *
     * @param token Mã xác thực
     * @return PasswordResetToken
     */
    @Override
    public PasswordResetToken findByToken(String token) {

        try {
            return passwordResetTokenRepository.findByToken(token);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
