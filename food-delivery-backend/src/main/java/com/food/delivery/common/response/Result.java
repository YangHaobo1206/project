package com.food.delivery.common.response;

import lombok.Data;

@Data
public class Result<T> {
    private Integer code;
    private String msg;
    private T data;

    public static <T> Result<T> success(T data) {
        Result<T> r = new Result<>();
        r.setCode(200);
        r.setMsg("success");
        r.setData(data);
        return r;
    }

    public static Result<?> success() {
        Result<?> r = new Result<>();
        r.setCode(200);
        r.setMsg("success");
        return r;
    }

    public static Result<?> fail(String msg) {
        Result<?> r = new Result<>();
        r.setCode(500);
        r.setMsg(msg);
        return r;
    }

    public static Result<?> fail(Integer code, String msg) {
        Result<?> r = new Result<>();
        r.setCode(code);
        r.setMsg(msg);
        return r;
    }

    public static Result<?> unauthorized() {
        return fail(401, "Unauthorized");
    }
}
