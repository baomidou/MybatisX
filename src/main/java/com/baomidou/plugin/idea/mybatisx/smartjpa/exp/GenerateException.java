package com.baomidou.plugin.idea.mybatisx.smartjpa.exp;

/**
 * 生成代码异常
 */
public class GenerateException extends RuntimeException {
    /**
     * Instantiates a new Generate exception.
     */
    public GenerateException() {
    }

    /**
     * Instantiates a new Generate exception.
     *
     * @param message the message
     */
    public GenerateException(String message) {
        super(message);
    }

    /**
     * Instantiates a new Generate exception.
     *
     * @param message the message
     * @param cause   the cause
     */
    public GenerateException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Instantiates a new Generate exception.
     *
     * @param cause the cause
     */
    public GenerateException(Throwable cause) {
        super(cause);
    }

    /**
     * Instantiates a new Generate exception.
     *
     * @param message            the message
     * @param cause              the cause
     * @param enableSuppression  the enable suppression
     * @param writableStackTrace the writable stack trace
     */
    public GenerateException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
