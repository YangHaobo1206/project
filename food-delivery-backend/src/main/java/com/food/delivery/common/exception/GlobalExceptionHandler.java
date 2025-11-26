package com.food.delivery.common.exception;

import com.food.delivery.common.constant.ErrorCode;
import com.food.delivery.common.response.Result;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BizException.class)
    public Result<?> handleBiz(BizException e) {
        return Result.fail(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<?> handleValid(MethodArgumentNotValidException e) {
        String msg = e.getBindingResult().getFieldError().getDefaultMessage();
        return Result.fail(ErrorCode.BAD_REQUEST, msg);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public Result<?> handleConstraint(ConstraintViolationException e) {
        return Result.fail(ErrorCode.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler({ExpiredJwtException.class, JwtException.class})
    public Result<?> handleJwt(Exception e) {
        return Result.unauthorized();
    }

    @ExceptionHandler(Exception.class)
    public Result<?> handleOther(Exception e) {
        e.printStackTrace();
        return Result.fail(ErrorCode.SERVER_ERROR, "系统异常");
    }
}
