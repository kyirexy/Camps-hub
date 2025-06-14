package com.liuxy.campushub.aiutil;

import cn.hutool.core.codec.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;

/**
 * API认证工具类
 * 用于生成讯飞开放平台API调用所需的签名信息
 * 包含MD5和HMAC-SHA1加密算法的实现
 *
 * @author ydwang16
 * @version 2023/06/19 14:56
 **/
public class ApiAuthUtil {
    /**
     * MD5加密使用的16进制字符表
     */
    private static final char[] MD5_TABLE = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    /**
     * 获取API调用签名
     * 签名生成规则：
     * 1. 先对appId和时间戳进行MD5加密
     * 2. 再使用secret对MD5结果进行HMAC-SHA1加密
     *
     * @param appId  应用ID
     * @param secret 应用密钥
     * @param ts     时间戳（秒）
     * @return 返回Base64编码的签名字符串，如果生成失败返回null
     */
    public static String getSignature(String appId, String secret, long ts) {
        try {
            String auth = md5(appId + ts);
            return hmacSHA1Encrypt(auth, secret);
        } catch (SignatureException e) {
            return null;
        }
    }

    /**
     * 使用HMAC-SHA1算法进行加密
     * 
     * @param encryptText 需要加密的文本
     * @param encryptKey  加密密钥
     * @return Base64编码的加密结果
     * @throws SignatureException 当加密过程出现错误时抛出
     */
    private static String hmacSHA1Encrypt(String encryptText, String encryptKey) throws SignatureException {
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
        return Base64.encode(rawHmac);
    }

    /**
     * 计算字符串的MD5值
     * 
     * @param cipherText 需要计算MD5的字符串
     * @return 32位小写MD5字符串，如果计算失败返回null
     */
    private static String md5(String cipherText) {
        try {
            byte[] data = cipherText.getBytes();
            // 信息摘要是安全的单向哈希函数，它接收任意大小的数据，并输出固定长度的哈希值。
            MessageDigest mdInst = MessageDigest.getInstance("MD5");

            // MessageDigest对象通过使用update方法处理数据，使用指定的byte数组更新摘要
            mdInst.update(data);

            // 摘要更新之后，通过调用digest()执行哈希计算，获得密文
            byte[] md = mdInst.digest();

            // 把密文转换成十六进制的字符串形式
            int j = md.length;
            char[] str = new char[j * 2];
            int k = 0;
            for (byte byte0 : md) {
                str[k++] = MD5_TABLE[byte0 >>> 4 & 0xf]; // 取字节高4位
                str[k++] = MD5_TABLE[byte0 & 0xf];       // 取字节低4位
            }
            // 返回经过加密后的字符串
            return new String(str);
        } catch (Exception e) {
            return null;
        }
    }
}