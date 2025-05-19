package com.example.borrowingbooks.exception;
import com.example.borrowingbooks.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 捕获所有运行时异常
    @ExceptionHandler(RuntimeException.class)
    public Result handleRuntimeException(RuntimeException ex) {
        log.error("运行时异常：", ex);
        return Result.fail("系统开小差了，请稍后再试~");
    }

    // 捕获所有异常（最后兜底）
    @ExceptionHandler(Exception.class)
    public Result handleException(Exception ex) {
        log.error("全局异常：", ex);
        return Result.fail("发生未知错误，联系管理员吧！");
    }
}
