package com.food.delivery.common.jwt;

import lombok.Data;

@Data
public class LoginUser {
    private Long id;
    private String role;

    private static final ThreadLocal<LoginUser> TL = new ThreadLocal<>();

    public static void set(LoginUser user) {
        TL.set(user);
    }

    public static LoginUser get() {
        return TL.get();
    }

    public static void clear() {
        TL.remove();
    }
}
