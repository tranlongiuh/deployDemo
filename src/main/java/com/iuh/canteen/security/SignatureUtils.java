package com.iuh.canteen.security;

import org.apache.commons.codec.binary.Hex;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

public class SignatureUtils {

    private static final String SECRET_KEY = "your-secret-keyyour-secret-keyyour-secret-key"; // Khóa bí mật giống với khóa bên React Native

    public static String generateSignature(String data) throws Exception {

        Mac sha256HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKey = new SecretKeySpec(SECRET_KEY.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        sha256HMAC.init(secretKey);
        byte[] hash = sha256HMAC.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return Hex.encodeHexString(hash);
    }

    public static boolean verifySignature(String data, String receivedSignature) throws Exception {

        String calculatedSignature = generateSignature(data);
        return calculatedSignature.equals(receivedSignature);
    }
}
