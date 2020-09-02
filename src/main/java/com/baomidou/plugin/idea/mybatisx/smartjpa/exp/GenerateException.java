package com.baomidou.plugin.idea.mybatisx.smartjpa.exp;

/**
 * 生成代码异常
 */
public class GenerateException extends RuntimeException{
    public GenerateException() {
    }

    public GenerateException(String message) {
        super(message);
    }

    public GenerateException(String message, Throwable cause) {
        super(message, cause);
    }

    public GenerateException(Throwable cause) {
        super(cause);
    }

    public GenerateException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
