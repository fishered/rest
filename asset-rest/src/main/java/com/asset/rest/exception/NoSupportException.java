package com.asset.rest.exception;

/**
 * @author fisher
 * @date 2023-09-14: 10:10
 * 当前处理不支持异常
 */
public class NoSupportException extends RuntimeException{

    public NoSupportException(String message) {
        super(message);
    }

    public String getErrorMessage() {
        return getMessage();
    }

}
