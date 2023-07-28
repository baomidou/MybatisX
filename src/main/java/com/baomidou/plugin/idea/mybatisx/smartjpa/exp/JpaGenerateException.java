package com.baomidou.plugin.idea.mybatisx.smartjpa.exp;

public class JpaGenerateException extends RuntimeException {
    public JpaGenerateException() {
    }

    public JpaGenerateException(String message) {
        super(message);
    }

    public JpaGenerateException(String message, Throwable cause) {
        super(message, cause);
    }

    public JpaGenerateException(Throwable cause) {
        super(cause);
    }

    public JpaGenerateException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
