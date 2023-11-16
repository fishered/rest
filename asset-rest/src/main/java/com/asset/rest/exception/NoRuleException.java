package com.asset.rest.exception;

/**
 * @author fisher
 * @date 2023-09-14: 10:10
 * 未获取到规则异常
 */
public class NoRuleException extends RuntimeException{

    public NoRuleException(String message) {
        super(message);
    }

    public String getErrorMessage() {
        return getMessage();
    }

}
