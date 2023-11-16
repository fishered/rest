package com.asset.rest.exception;

/**
 * @author fisher
 * @date 2023-09-14: 10:10
 * 未获取到规则异常
 */
public class NotFoundSourceException extends RuntimeException{

    public NotFoundSourceException(String message) {
        super(message);
    }

    public String getErrorMessage() {
        return getMessage();
    }

}
