package com.iuh.canteen.service;

import com.iuh.canteen.dto.UserDTO;
import com.iuh.canteen.entity.User;

import java.math.BigDecimal;

public interface UserService {

    Boolean registerUser(UserDTO newUser);

    Boolean resetPassword(String token, String newPassword);

    Boolean enable(String verificationCode);

    void updateUserPassword(String email, String newPassword);

    boolean updatePassword(String username, String newPassword);

    String loadUsernameByEmail(String email);

    String login(String username);

    Boolean verify(String username, String password);

    Boolean existsByEmail(String email);

    Boolean existsByUsername(String username);

    Boolean sendMailResetPassword(String email);

    Boolean existsByVerificationCode(String code);

    Boolean isEmpty();

    Long getIdByUsername(String username);

    User loadUserByUsername(String username);

    User findById(Long idUser);

    User save(User result);

    String getResponseRole(String username);

    long countCustomers();

    boolean setPassword2(String username, String password2);

    UserDTO getUserDetails(String username);

    boolean updateUserPhone(String username, String phone);

    boolean updateUserEmail(String username, String email);

    boolean verifyPass2(String username, String confirmPassword);

    boolean checkAmount(String username, BigDecimal amount);

    boolean updateUserBalance(String username, long amount);
}
