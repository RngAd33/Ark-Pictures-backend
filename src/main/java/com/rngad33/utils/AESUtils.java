package com.yupi.usercenter.utils;

import com.yupi.usercenter.constant.AESConstant;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * AES加解密
 */
public class AESUtils {

    /**
     * 加密入口
     *
     * @param originalText 明文
     * @return 密文
     * @throws Exception
     */
    public static String doEncrypt(String originalText) throws Exception {
        // 从文件加载密钥和IV
        SecretKey loadedKey = loadKey();
        byte[] loadedIV = loadIV();
        if (loadedKey == null) {
            System.out.println("——！！密钥加载失败，请检查文件路径是否正确或文件是否存在！！——");
            return null;
        }
        if (loadedIV == null) {
            System.out.println("——！！IV加载失败，请检查文件路径是否正确或文件是否存在！！——");
            return null;
        }
        // 加密
        String encryptedText = encrypt(originalText, loadedKey, loadedIV);

        // 校验
        if (originalText.equals(decrypt(encryptedText, loadedKey, loadedIV))) {
            return encryptedText;
        } else {
            System.out.println("————！！！加密失败，请检查代码AESUtil.java是否出错！！！————");
            return null;
        }
    }

    /**
     * 规定密钥长度
     */
    private static final int KEY_SIZE = 256;

    /**
     * 生成AES密钥
     *
     * @return 密钥
     * @throws Exception
     */
    private static SecretKey generateKey() throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(AESConstant.ALGORITHM);
        keyGenerator.init(KEY_SIZE);
        return keyGenerator.generateKey();
    }

    /**
     * 生成初始化向量(IV)
     *
     * @return iv
     */
    private static byte[] generateIV() {
        byte[] iv = new byte[16]; // AES块大小是16字节
        new SecureRandom().nextBytes(iv);
        return iv;
    }

    /**
     * 加密
     *
     * @param plaintext 明文
     * @param key 密钥
     * @param iv
     * @return 密文
     * @throws Exception
     */
    private static String encrypt(String plaintext, SecretKey key, byte[] iv) throws Exception {
        Cipher cipher = Cipher.getInstance(AESConstant.TRANSFORMATION);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);

        byte[] encryptedBytes = cipher.doFinal(plaintext.getBytes("UTF-8"));
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    /**
     * 解密
     *
     * @param ciphertext 密文
     * @param key 密钥
     * @param iv
     * @return 明文
     * @throws Exception
     */
    private static String decrypt(String ciphertext, SecretKey key, byte[] iv) throws Exception {
        Cipher cipher = Cipher.getInstance(AESConstant.TRANSFORMATION);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);

        byte[] decodedBytes = Base64.getDecoder().decode(ciphertext);
        byte[] decryptedBytes = cipher.doFinal(decodedBytes);
        return new String(decryptedBytes, "UTF-8");
    }

    /**
     * 保存密钥到文件
     *
     * @param key
     * @throws IOException
     */
    private static void saveKey(SecretKey key) throws IOException {
        byte[] keyBytes = key.getEncoded();
        try (FileOutputStream fos = new FileOutputStream(AESConstant.KEY_FILE_PATH)) {
            fos.write(keyBytes);
        }
    }

    /**
     * 从文件加载密钥
     *
     * @return key
     * @throws IOException
     */
    private static SecretKey loadKey() throws IOException {
        File keyFile = new File(AESConstant.KEY_FILE_PATH);
        if (!keyFile.exists()) {
            return null;
        }
        byte[] keyBytes = new byte[(int) keyFile.length()];
        try (FileInputStream fis = new FileInputStream(keyFile)) {
            fis.read(keyBytes);
        }
        return new SecretKeySpec(keyBytes, AESConstant.ALGORITHM);
    }

    /**
     * 保存 IV 到文件
     *
     * @param iv
     * @throws IOException
     */
    private static void saveIV(byte[] iv) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(AESConstant.IV_FILE_PATH)) {
            fos.write(iv);
        }
    }

    /**
     * 从文件加载 IV
     *
     * @return iv
     * @throws IOException
     */
    private static byte[] loadIV() throws IOException {
        File ivFile = new File(AESConstant.IV_FILE_PATH);
        if (!ivFile.exists()) {
            return null;
        }
        byte[] iv = new byte[(int) ivFile.length()];
        try (FileInputStream fis = new FileInputStream(ivFile)) {
            fis.read(iv);
        }
        return iv;
    }

    /**
     * 测试方法
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        /* 生成随机密钥和IV
        SecretKey key = generateKey();
        byte[] iv = generateIV();

        // 保存密钥和IV到文件
        saveKey(key);
        System.out.println("密钥已保存到文件。");
        saveIV(iv);
        System.out.println("IV已保存到文件。");
         */

        // 从文件加载密钥
        SecretKey loadedKey = loadKey();
        if (loadedKey == null) {
            System.out.println("密钥加载失败，请检查文件路径是否正确或文件是否存在。");
            return;
        }
        System.out.println("密钥已从文件加载。");

        // 从文件加载IV
        byte[] loadedIV = loadIV();
        if (loadedIV == null) {
            System.out.println("IV加载失败，请检查文件路径是否正确或文件是否存在。");
            return;
        }
        System.out.println("IV已从文件加载。");

        // 测试明文
        String originalText = "恐猿莱欧斯吓s泰拉";
        System.out.println("原始文本: " + originalText);

        // 使用加载的密钥进行加密
        String encryptedText = encrypt(originalText, loadedKey, loadedIV);
        System.out.println("加密后: " + encryptedText);

        // 使用加载的密钥进行解密
        String decryptedText = decrypt(encryptedText, loadedKey, loadedIV);
        System.out.println("解密后: " + decryptedText);

        // 验证解密结果
        System.out.println("解密结果验证: " + originalText.equals(decryptedText));
    }
}