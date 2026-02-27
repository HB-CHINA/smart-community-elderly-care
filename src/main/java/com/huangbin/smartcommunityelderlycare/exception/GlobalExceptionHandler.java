package com.huangbin.smartcommunityelderlycare.exception;

import com.huangbin.smartcommunityelderlycare.common.Result;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理验证失败异常（@Valid注解触发的错误）
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<String> handleValidationException(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.toList());

        // 将多个错误信息用分号连接
        String errorMessage = String.join("; ", errors);
        return Result.error(errorMessage);
    }

    /**
     * 处理其他绑定异常
     */
    @ExceptionHandler(BindException.class)
    public Result<String> handleBindException(BindException ex) {
        // 获取第一个错误信息
        if (!ex.getBindingResult().getFieldErrors().isEmpty()) {
            String errorMessage = ex.getBindingResult()
                    .getFieldErrors()
                    .get(0)  // 获取第一个错误
                    .getDefaultMessage();
            return Result.error(errorMessage);
        }
        return Result.error("参数绑定错误");
    }

    /**
     * 处理通用运行时异常
     */
    @ExceptionHandler(RuntimeException.class)
    public Result<String> handleRuntimeException(RuntimeException ex) {
        return Result.error(ex.getMessage());
    }

    /**
     * 处理通用异常
     */
    @ExceptionHandler(Exception.class)
    public Result<String> handleException(Exception ex) {
        ex.printStackTrace(); // 打印异常堆栈（开发时）
        return Result.error("系统繁忙，请稍后重试");
    }
}