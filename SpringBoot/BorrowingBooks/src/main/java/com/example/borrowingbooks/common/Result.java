package com.example.borrowingbooks.common;

import java.io.Serializable;

public class Result<T> implements Serializable {
    private int code;
    private String message;
    private T data;
    private final long timestamp;

    // 常用状态码
    public static final int SUCCESS_CODE = 200;
    public static final int BAD_REQUEST_CODE = 400;
    public static final int UNAUTHORIZED_CODE = 401;
    public static final int FORBIDDEN_CODE = 403;
    public static final int NOT_FOUND_CODE = 404;
    public static final int ERROR_CODE = 500;

    public Result() {
        this.timestamp = System.currentTimeMillis();
    }

    public static <T> Result<T> ok() {
        return new Result<T>()
                .code(SUCCESS_CODE)
                .message("操作成功");
    }

    public static <T> Result<T> ok(T data) {
        return (Result<T>) ok().data(data);
    }

    public static <T> Result<T> fail() {
        return new Result<T>()
                .code(ERROR_CODE)
                .message("操作失败");
    }

    public static <T> Result<T> fail(String message) {
        return (Result<T>) fail().message(message);
    }

    public static <T> Result<T> status(boolean success) {
        return success ? ok() : fail();
    }

    public Result<T> code(int code) {
        this.code = code;
        return this;
    }

    public Result<T> message(String message) {
        this.message = message;
        return this;
    }

    public Result<T> data(T data) {
        this.data = data;
        return this;
    }

    // 以下是状态码快捷方法
    public static <T> Result<T> badRequest() {
        return new Result<T>()
                .code(BAD_REQUEST_CODE)
                .message("请求参数错误");
    }

    public static <T> Result<T> unauthorized() {
        return new Result<T>()
                .code(UNAUTHORIZED_CODE)
                .message("未授权访问");
    }

    public static <T> Result<T> forbidden() {
        return new Result<T>()
                .code(FORBIDDEN_CODE)
                .message("禁止访问");
    }

    public static <T> Result<T> notFound() {
        return new Result<T>()
                .code(NOT_FOUND_CODE)
                .message("资源未找到");
    }

    // getter 方法
    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }

    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "Result{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", data=" + data +
                ", timestamp=" + timestamp +
                '}';
    }
}
