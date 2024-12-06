package com.iuh.canteen.service.impl;

import com.iuh.canteen.dto.UserDTO;
import com.iuh.canteen.entity.PasswordResetToken;
import com.iuh.canteen.entity.User;
import com.iuh.canteen.repository.UserRepository;
import com.iuh.canteen.security.JwtUtil;
import com.iuh.canteen.service.PasswordResetTokenService;
import com.iuh.canteen.service.UserService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordResetTokenService passwordResetTokenService;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private ModelMapper modelMapper;

    /**
     * registerUser Đăng ký Tài Khoản cho Thực khách
     *
     * @param newUser Dữ liệu Tài khoản
     * @return true - đăng ký thành công
     */
    @Override
    public Boolean registerUser(UserDTO newUser) {

        boolean result = false;
        try {
            User user = modelMapper.map(newUser, User.class);
            user.setEmail(newUser.getEmail());
            user.setPhone(newUser.getPhone());
            user.setUsername(newUser.getUsername());
            user.setPassword(newUser.getPassword());
            user.setBalance(BigDecimal.ZERO);
            // Encode the password
            String encodedPassword = new BCryptPasswordEncoder().encode(user.getPassword());
            user.setPassword(encodedPassword);
            // Generate verification code
            String randomCode = UUID.randomUUID()
                                    .toString();
            user.setVerificationCode(randomCode);
            user.setRoles("ROLE_CUSTOMER");
            // Save user
            userRepository.save(user);
            // Send verification email
            sendVerificationEmail(user);
            result = true;
        } catch (Exception e) {
            e.printStackTrace(); // Log the full stack trace
        }
        return result;
    }

    /**
     * resetPassword Đặt lại mật khẩu
     *
     * @param token       Mã xác thực
     * @param newPassword Mật khẩu mới
     * @return true - đặt lại thành công
     */
    @Override
    public Boolean resetPassword(String token, String newPassword) {

        PasswordResetToken resetToken = passwordResetTokenService.findByToken(token);
        if (resetToken != null) {
            if (resetToken.getExpiration()
                          .isBefore(LocalDateTime.now())) {
                return false;
                //Token has expired.
            }
            // Find the user by email and update the password
            updateUserPassword(resetToken.getEmail(), newPassword);
            return passwordResetTokenService.deleteByEmail(resetToken.getEmail());
        } else {
            //Invalid token.
            return false;
        }
    }

    /**
     * sendVerificationEmail Gửi email xác thực đến Email của người đăng ký
     *
     * @param user Tài khoản đăng ký
     */
    private void sendVerificationEmail(User user) {

        String toAddress = user.getEmail();
        String subject = "Vui lòng xác minh đăng ký của bạn";
        String senderName = "Canteen Industrial University of HCMC";
        String content = "Xin chào [[name]],<br>"
                + "Vui lòng nhấp vào liên kết bên dưới để xác minh đăng ký của bạn:<br>"
                + "<h3><a href=\"[[URL]]\" target=\"_self\">Xác Thực</a></h3>"
                + "Trân trọng,<br>"
                + "Canteen Industrial University of HCMC.";
        content = content.replace("[[name]]", user.getUsername());
        String verifyURL = "http://localhost:8080/api/auth/enable?code=" + user.getVerificationCode();
        content = content.replace("[[URL]]", verifyURL);
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        try {
            helper.setFrom("tranlongiuh@gmail.com", senderName);
            helper.setTo(toAddress);
            helper.setSubject(subject);
            helper.setText(content, true);
            mailSender.send(message);
        } catch (MessagingException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    /**
     * enable Kích hoạt Tài khoản
     *
     * @param verificationCode Mã kích hoạt
     * @return true - kích hoạt thành công
     */
    @Override
    public Boolean enable(String verificationCode) {

        User user = userRepository.findByVerificationCode(verificationCode)
                                  .orElse(null);
        if (user == null || user.isEnabled()) {
            return false;
        } else {
            user.setVerificationCode(null);
            user.setEnabled(true);
            userRepository.save(user);
            return true;
        }
    }

    /**
     * updateUserPassword Cập nhật mật khẩu
     *
     * @param email       Email đăng ký
     * @param newPassword Mật khẩu mới
     */
    @Override
    public void updateUserPassword(String email, String newPassword) {

        User user = userRepository.findByEmail(email)
                                  .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Override
    public boolean updatePassword(String username, String newPassword) {

        boolean rs = false;
        try {
            User user = userRepository.findByUsername(username);
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
            rs = true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return rs;
    }

    /**
     * loadUsernameByEmail Lấy Tên Tài khoản với Email đăng ký
     *
     * @param email Email đăng ký
     * @return String
     */
    @Override
    public String loadUsernameByEmail(String email) {

        User user = userRepository.findByEmail(email)
                                  .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
        return user.getUsername();
    }

    /**
     * login khởi tạo jwt token
     *
     * @param username Tên Tài khoản
     * @return String
     */
    @Override
    public String login(String username) {

        return jwtUtil.generateToken(username);
    }

    /**
     * verify Xác thực Tài khoản đăng nhập
     *
     * @param username Tên Tài khoản
     * @param password Mật khẩu
     * @return true - xác thực thành công
     */
    @Override
    public Boolean verify(String username, String password) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        username, password
                )
        );
        System.err.println("authentication " + authentication);
        if (authentication.isAuthenticated()) {
            User user = userRepository.findByUsername(username);
            System.err.println("user.isEnabled() " + user.isEnabled());
            return user.isEnabled();
        }
        return false;
    }

    /**
     * existsByEmail Tồn tại với Email
     *
     * @param email Email đăng ký
     * @return true - có tồn tại
     */
    @Override
    public Boolean existsByEmail(String email) {

        boolean result = false;
        if (email != null && !email.isEmpty()) {
            result = userRepository.existsByEmail(email);
        }
        return result;
    }

    /**
     * existsByUsername Tồn tại với Tên tài khoản
     *
     * @param username Tên tài khoản
     * @return true - có tồn tại
     */
    @Override
    public Boolean existsByUsername(String username) {

        boolean result = false;
        if (username != null && !username.isEmpty()) {
            result = userRepository.existsByUsername(username);
        }
        return result;
    }

    /**
     * sendMailResetPassword Gửi email đặt lại Mật khẩu
     *
     * @param email Email đăng ký tài khoản
     * @return true - gửi thành công
     */
    @Override
    public Boolean sendMailResetPassword(String email) {

        String username = loadUsernameByEmail(email);
        if (existsByUsername(username)) {
            String token = UUID.randomUUID()
                               .toString();
            String resetLink = "https://canteen-iuh-manage.vercel.app/reset?token=" + token;
            // Save token to the database
            saveToken(email, token);
            sendResetPasswordEmail(email, resetLink);
            return true;
        } else {
            return false;
        }
    }

    /**
     * sendResetPasswordEmail Tạo email đặt lại mật khẩu
     *
     * @param to        Email đăng ký tài khoản
     * @param resetLink Link đặt lại mật khẩu
     */
    private void sendResetPasswordEmail(String to, String resetLink) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Đặt lại mật khẩu");
        message.setText("Để đặt lại mật khẩu, hãy bấm vào link này: " + resetLink);
        mailSender.send(message);
    }

    /**
     * saveToken Lưu mã đặt lại mật khẩu
     *
     * @param email Email đăng ký tài khoản
     * @param token Mã đặt lại mật khẩu
     */
    private void saveToken(String email, String token) {

        LocalDateTime expiration = LocalDateTime.now()
                                                .plusHours(1); // Token hết hạn sau 1 giờ
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setEmail(email);
        resetToken.setToken(token);
        resetToken.setExpiration(expiration);
        resetToken.setCreatedAt(LocalDateTime.now());
        passwordResetTokenService.saveToken(resetToken);
    }

    /**
     * existsByVerificationCode tồn tại Mã đặt lại mật khẩu
     *
     * @param code Mã đặt lại mật khẩu
     * @return true - có tồn tại
     */
    @Override
    public Boolean existsByVerificationCode(String code) {

        boolean result = false;
        if (code != null && !code.isEmpty()) {
            result = userRepository.existsByVerificationCode(code);
        }
        return result;
    }

    /**
     * isEmpty Không có tài khoản nào trong hệ thống
     *
     * @return true - nếu không có tài khoản nào trong hệ thống
     */
    @Override
    public Boolean isEmpty() {

        return userRepository.findAll()
                             .isEmpty();
    }

    /**
     * getIdByUsername Lấy Mã Tài Khoản với Tên Tài Khoản
     *
     * @param username Tên Tài Khoản
     * @return Long
     */
    @Override
    public Long getIdByUsername(String username) {

        if (userRepository.existsByUsername(username)) {
            System.out.println("existsByUsername true");
            return userRepository.findByUsername(username)
                                 .getId();
        }
        return null;
    }

    /**
     * loadUserByUsername Lấy Tài khoản với Tên Tài khoản
     *
     * @param username Tên Tài khoản
     * @return User
     */
    @Override
    public User loadUserByUsername(String username) {

        if (userRepository.existsByUsername(username)) {
            User result = userRepository.findByUsername(username);
            return result;
        }
        return null;
    }

    /**
     * findById Lấy Tài khoản với Mã Tài khoản
     *
     * @param idUser Mã Tài khoản
     * @return User
     */
    @Override
    public User findById(Long idUser) {

        return userRepository.findById(idUser)
                             .orElse(null);
    }

    /**
     * save Lưu Tài khoản
     *
     * @param result Tài khoản
     * @return User
     */
    @Override
    public User save(User result) {

        return userRepository.save(result);
    }

    /**
     * getResponseRole Trả về Quyền truy cập của Tài Khoản
     *
     * @param username Tên Tài khoản
     * @return String
     */
    @Override
    public String
    getResponseRole(String username) {

        User user = userRepository.findByUsername(username);
        if (user != null && !user.getRoles()
                                 .isEmpty()) {
            if (user.getRoles()
                    .contains("ROLE_ADMIN")) {
                return "ROLE_ADMIN";
            }
            if (user.getRoles()
                    .contains("ROLE_CASHIER")) {
                return "ROLE_CASHIER";
            }
            if (user.getRoles()
                    .contains("ROLE_MANAGER")) {
                return "ROLE_MANAGER";
            }
            if (user.getRoles()
                    .contains("ROLE_CUSTOMER")) {
                if (user.getPassword2() == null
                        || user.getPassword2()
                               .isEmpty()) {
                    return "ROLE_CUSTOMER_1";
                }
                return "ROLE_CUSTOMER";
            }
        }
        return "";
    }

    /**
     * countCustomers Tính số Thực khách trong hệ thống
     *
     * @return long
     */
    @Override
    public long countCustomers() {

        return userRepository.findAll()
                             .stream()
                             .filter(user -> user.getRoles()
                                                 .equals("ROLE_CUSTOMER"))
                             .count();
    }

    /**
     * setPassword2 Lưu mật khẩu cấp 2
     *
     * @param username  Tên Tài khoản
     * @param password2 Mật khẩu cấp 2
     * @return true - lưu thành công
     */
    @Override
    public boolean setPassword2(String username, String password2) {

        boolean result = false;
        try {
            if (userRepository.existsByUsername(username)) {
                User user = userRepository.findByUsername(username);
                user.setPassword2(password2);
                userRepository.save(user);
                result = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public UserDTO getUserDetails(String username) {

        UserDTO rs;
        try {
            User user = userRepository.findByUsername(username);
            rs = modelMapper.map(user, UserDTO.class);
            rs.setPassword("");
            rs.setPassword2("");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return rs;
    }

    @Override
    public boolean updateUserPhone(String username, String phone) {

        boolean rs = false;
        try {
            User user = userRepository.findByUsername(username);
            user.setPhone(phone);
            userRepository.save(user);
            rs = true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return rs;
    }

    @Override
    public boolean updateUserEmail(String username, String email) {

        boolean rs = false;
        try {
            User user = userRepository.findByUsername(username);
            user.setEmail(email);
            userRepository.save(user);
            rs = true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return rs;
    }

    @Override
    public boolean verifyPass2(String username, String confirmPassword) {

        boolean rs = false;
        try {
            User user = userRepository.findByUsername(username);
            if (user.getPassword2() != null && user.getPassword2()
                                                   .equals(confirmPassword)) {
                rs = true;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return rs;
    }

    @Override
    public boolean checkAmount(String username, BigDecimal amount) {

        try {
            User user = userRepository.findByUsername(username);
            if (
                    user.getBalance()
                        .compareTo(amount) >= 0
            ) {
                return true;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    @Override
    public boolean updateUserBalance(String username, long amount) {

        try {
            User user = userRepository.findByUsername(username);
            BigDecimal balance = user.getBalance();
            user.setBalance(balance.add(new BigDecimal(amount)));
            user = userRepository.save(user);
            if (
                    user.getBalance()
                        .compareTo(balance) >= 0
            ) {
                return true;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return false;
    }
}
