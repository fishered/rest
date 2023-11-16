package com.asset.rest.exception;

/**
 * @author fisher
 * @date 2023-09-14: 10:10
 * 未获取到url异常
 */
public class NoUrlException extends RuntimeException{

    public NoUrlException(String message) {
        super(message);
    }

    public String getErrorMessage() {
        return getMessage();
    }

}
