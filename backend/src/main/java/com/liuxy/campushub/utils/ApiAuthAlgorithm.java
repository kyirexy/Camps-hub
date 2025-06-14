package com.liuxy.campushub.utils;

import java.util.Base64;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.Objects;

public class ApiAuthAlgorithm {
    private static final char[] MD5_TABLE = {
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
        'a', 'b', 'c', 'd', 'e', 'f'
    };

    /**
     * 获取签名
     *
     * @param appId    应用ID
     * @param secret   应用密钥
     * @param ts       时间戳
     * @return 签名
     */
    public static String getSignature(String appId, String secret, long ts) {
        try {
            if (appId == null || secret == null) {
                throw new IllegalArgumentException("appId and secret cannot be null");
            }
            String auth = md5(appId + ts);
            if (auth == null) {
                throw new SignatureException("Failed to generate MD5 hash");
            }
            return hmacSHA1Encrypt(auth, secret);
        } catch (SignatureException e) {
            return null;
        }
    }

    /**
     * HmacSHA1加密
     */
    private static String hmacSHA1Encrypt(String encryptText, String encryptKey)
            throws SignatureException {
        Objects.requireNonNull(encryptText, "encryptText cannot be null");
        Objects.requireNonNull(encryptKey, "encryptKey cannot be null");
        
        byte[] rawHmac;
        try {
            byte[] data = encryptKey.getBytes(StandardCharsets.UTF_8);
            SecretKeySpec secretKey = new SecretKeySpec(data, "HmacSHA1");
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(secretKey);
            byte[] text = encryptText.getBytes(StandardCharsets.UTF_8);
            rawHmac = mac.doFinal(text);
        } catch (InvalidKeyException e) {
            throw new SignatureException("InvalidKeyException:" + e.getMessage());
        } catch (NoSuchAlgorithmException e) {
            throw new SignatureException("NoSuchAlgorithmException:" + e.getMessage());
        }
        return Base64.getEncoder().encodeToString(rawHmac);
    }

    /**
     * MD5加密
     */
    private static String md5(String cipherText) {
        try {
            if (cipherText == null) {
                return null;
            }
            byte[] data = cipherText.getBytes(StandardCharsets.UTF_8);
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            mdInst.update(data);
            byte[] md = mdInst.digest();

            int j = md.length;
            char[] str = new char[j * 2];
            int k = 0;
            for (byte byte0 : md) {
                str[k++] = MD5_TABLE[byte0 >>> 4 & 0xf];
                str[k++] = MD5_TABLE[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            return null;
        }
    }
} 