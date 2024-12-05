package com.iuh.canteen.controller;

import com.iuh.canteen.dto.UserDTO;
import com.iuh.canteen.security.JwtUtil;
import com.iuh.canteen.service.impl.UserServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * UserController Bộ điều khiển dành cho người dùng
 */
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Value("${vnpay.tmnCode}")
    private String tmnCode;

    @Value("${vnpay.hashSecret}")
    private String hashSecret;

    @Value("${vnpay.url}")
    private String vnpayUrl;

    @Value("${vnpay.returnUrl}")
    private String returnUrl;

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping
    ResponseEntity<?> getUserDetails(HttpServletRequest request) {

        UserDTO result;
        try {
            String jwt = jwtUtil.getJwtFromRequest(request);
            String username = jwtUtil.extractUsername(jwt);
            result = userService.getUserDetails(username);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.badRequest()
                             .build();
    }

    @PostMapping("/updatePhone")
    ResponseEntity<?> updateUserPhone(HttpServletRequest request, @RequestParam("phone") String phone) {

        try {
            String jwt = jwtUtil.getJwtFromRequest(request);
            String username = jwtUtil.extractUsername(jwt);
            if (userService.updateUserPhone(username, phone)) {
                return ResponseEntity.ok()
                                     .build();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.badRequest()
                             .build();
    }

    @PostMapping("/updateEmail")
    ResponseEntity<?> updateUserEmail(HttpServletRequest request, @RequestParam("email") String email) {

        try {
            String jwt = jwtUtil.getJwtFromRequest(request);
            String username = jwtUtil.extractUsername(jwt);
            if (userService.updateUserEmail(username, email)) {
                return ResponseEntity.ok()
                                     .build();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.badRequest()
                             .build();
    }

    @PostMapping("/updatePassword")
    ResponseEntity<?> updatePassword(HttpServletRequest request, @RequestParam("oldPassword") String oldPassword,
                                     @RequestParam("newPassword") String newPassword) {

        try {
            String jwt = jwtUtil.getJwtFromRequest(request);
            String username = jwtUtil.extractUsername(jwt);
            if (userService.verify(username, oldPassword)) {
                if (userService.updatePassword(username, newPassword)) {
                    return ResponseEntity.ok()
                                         .build();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.badRequest()
                             .build();
    }

    @PostMapping("/verifyPass2")
    ResponseEntity<?> verifyPass2(HttpServletRequest request,
                                  @RequestParam("confirmPassword") String confirmPassword) {

        try {
            String jwt = jwtUtil.getJwtFromRequest(request);
            String username = jwtUtil.extractUsername(jwt);
            if (userService.verifyPass2(username, confirmPassword)) {
                return ResponseEntity.ok()
                                     .build();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.badRequest()
                             .build();
    }

    @PostMapping("/create-payment")
    public ResponseEntity<?> createPayment(HttpServletRequest request, @RequestParam("amount") BigDecimal amount,
                                           @RequestParam("ipAddr") String ipAddr) {

        System.out.println(
                "createPayment amount " + amount + " ipAddr " + ipAddr + " tmnCode " + tmnCode + " hashSecret " + hashSecret);
        try {
            String jwt = jwtUtil.getJwtFromRequest(request);
            String username = jwtUtil.extractUsername(jwt);
            String orderId = username + "-" + System.currentTimeMillis(); // Định danh duy nhất cho giao dịch
            String orderInfo = "Nạp tiền cho tài khoản: " + username;
//            String ipAddr = "127.0.0.1"; // IP client
            Map<String, String> vnpParams = new HashMap<>();
            vnpParams.put("vnp_Version", "2.1.0");
            vnpParams.put("vnp_Command", "pay");
            vnpParams.put("vnp_TmnCode", tmnCode);
            vnpParams.put("vnp_Amount", String.valueOf(Long.parseLong(amount.toString()) * 100)); // VNĐ x 100
            vnpParams.put("vnp_CurrCode", "VND");
            vnpParams.put("vnp_TxnRef", orderId); // Mã giao dịch chứa username
            vnpParams.put("vnp_OrderInfo", orderInfo);
            vnpParams.put("vnp_OrderType", "other");
            vnpParams.put("vnp_Locale", "vn");
            vnpParams.put("vnp_ReturnUrl", returnUrl);
            vnpParams.put("vnp_IpAddr", ipAddr);
            vnpParams.put("vnp_CreateDate", new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
            String signData = vnpParams.entrySet()
                                       .stream()
                                       .sorted(Map.Entry.comparingByKey())
                                       .map(e -> e.getKey() + "=" + e.getValue())
                                       .collect(Collectors.joining("&"));
            String vnpSecureHash = hmacSHA512(hashSecret, signData);
            vnpParams.put("vnp_SecureHash", vnpSecureHash);
            String queryUrl = vnpParams.entrySet()
                                       .stream()
                                       .map(e -> e.getKey() + "=" + URLEncoder.encode(e.getValue(),
                                               StandardCharsets.UTF_8))
                                       .collect(Collectors.joining("&"));
            String paymentUrl = vnpayUrl + "?" + queryUrl;
            return ResponseEntity.ok(paymentUrl);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String hmacSHA512(String key, String data) throws NoSuchAlgorithmException, InvalidKeyException {

        try {
            if (key == null || data == null) {
                throw new NullPointerException();
            }
            final Mac hmac512 = Mac.getInstance("HmacSHA512");
            byte[] hmacKeyBytes = key.getBytes();
            final SecretKeySpec secretKey = new SecretKeySpec(hmacKeyBytes, "HmacSHA512");
            hmac512.init(secretKey);
            byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
            byte[] result = hmac512.doFinal(dataBytes);
            StringBuilder sb = new StringBuilder(2 * result.length);
            for (byte b : result) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();
        } catch (Exception ex) {
            return "";
        }
    }

    @PostMapping("/vnpay-callback")
    public ResponseEntity<?> handleCallback(
            HttpServletRequest request) throws NoSuchAlgorithmException, InvalidKeyException {

        Map<String, String> vnpParams = new HashMap<>();
        request.getParameterMap()
               .forEach((key, value) -> vnpParams.put(key, value[0]));
        String receivedHash = vnpParams.remove("vnp_SecureHash");
        String signData = vnpParams.entrySet()
                                   .stream()
                                   .sorted(Map.Entry.comparingByKey())
                                   .map(e -> e.getKey() + "=" + e.getValue())
                                   .collect(Collectors.joining("&"));
        String calculatedHash = hmacSHA512(hashSecret, signData);
        if (calculatedHash.equals(receivedHash)) {
            String status = vnpParams.get("vnp_TransactionStatus");
            if ("00".equals(status)) { // Thanh toán thành công
                String txnRef = vnpParams.get("vnp_TxnRef"); // Lấy mã giao dịch
                String username = txnRef.split("-")[0]; // Lấy username từ mã giao dịch
                long amount = Long.parseLong(vnpParams.get("vnp_Amount")) / 100; // Số tiền nạp
                updateUserBalance(username, amount); // Cập nhật số dư
                return ResponseEntity.ok("Transaction success for user: " + username);
            } else {
                return ResponseEntity.badRequest()
                                     .body("Transaction failed");
            }
        } else {
            return ResponseEntity.badRequest()
                                 .body("Invalid hash");
        }
    }

    private void updateUserBalance(String username, long amount) {
        // Logic cập nhật số dư trong cơ sở dữ liệu
        userService.updateUserBalance(username, amount);
    }
}
