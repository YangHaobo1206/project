package com.example.fooddelivery.common;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BizException.class)
    public Result<Void> handleBiz(BizException ex) {
        return Result.failure(ex.getCode(), ex.getMessage());
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, ConstraintViolationException.class, HttpMessageNotReadableException.class})
    public Result<Void> handleValidation(Exception ex) {
        return Result.failure(400, ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public Result<Void> handleOther(Exception ex) {
        ex.printStackTrace();
        return Result.failure(500, "internal_error: " + ex.getMessage());
    }
}
