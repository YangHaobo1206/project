package com.food.delivery.common.utils;

/**
 * 简易密码工具类（当前版本不加密，保证项目立即可跑）
 */
public class PasswordUtil {

    public static String encode(String raw) {
        return raw; // 不加密
    }

    public static boolean matches(String raw, String encoded) {
        if (raw == null && encoded == null) return true;
        if (raw == null || encoded == null) return false;
        return raw.equals(encoded);
    }
}
