package com.liuxy.campushub.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleException(Exception e) {
        Map<String, String> response = new HashMap<>();
        response.put("success", "false");
        response.put("message", "服务器内部错误: " + e.getMessage());
        return ResponseEntity.internalServerError().body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("success", "false");
        response.put("message", "参数验证失败: " + 
            ex.getBindingResult().getAllErrors().get(0).getDefaultMessage());
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, String>> handleConstraintViolation(ConstraintViolationException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("success", "false");
        response.put("message", "参数验证失败: " + ex.getMessage());
        return ResponseEntity.badRequest().body(response);
    }
} 