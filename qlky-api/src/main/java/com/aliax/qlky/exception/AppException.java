package com.aliax.qlky.exception;

public class AppException extends RuntimeException {
    private int errorCode;     // 错误码
    private String errorMessage; // 错误消息

    // 无参数构造器
    public AppException() {
        super();
    }

    // 只带错误消息的构造器
    public AppException(String errorMessage) {
        super(errorMessage);
        this.errorMessage = errorMessage;
    }

    // 带错误码和错误消息的构造器
    public AppException(int errorCode, String errorMessage) {
        super(errorMessage);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    // 获取错误码
    public int getErrorCode() {
        return errorCode;
    }

    // 设置错误码
    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    // 获取错误消息
    public String getErrorMessage() {
        return errorMessage;
    }

    // 设置错误消息
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    // 覆盖toString方法，提供更多异常信息
    @Override
    public String toString() {
        return "AppException{errorCode=" + errorCode + ", errorMessage='" + errorMessage + "'}";
    }
}
