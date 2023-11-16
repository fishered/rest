package com.asset.rest.biz.context;

import cn.hutool.core.lang.Assert;
import com.asset.rest.biz.context.auth.AuthContext;
import com.asset.rest.biz.context.handler.LinkedHandlerContext;
import com.asset.rest.biz.context.header.HeaderContext;
import com.asset.rest.biz.context.param.BodyContext;
import com.asset.rest.biz.context.sign.SignContext;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author fisher
 * @date 2023-09-14: 9:53
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ProcessContext {

    /**
     * 接口的编码
     */
    private String code;

    /**
     * 传入的json参数
     */
    private String params;

    /**
     * 组装认证的上下文
     */
    private AuthContext authContext;

    /**
     * 请求头的参数处理
     */
    private HeaderContext headerContext;

    /**
     * 签名的处理
     */
    private SignContext signContext;

    /**
     * 请求体参数
     */
    private BodyContext bodyContext;

    /**
     * 组装的必须的参数
     */
    private NeedfulParam needfulParam;

    /**
     * 前置的handler上下文
     */
//    private HandlerContext beforeHandlerContext;
    private LinkedHandlerContext beforeHandlerContexts;

    /**
     * 后置的handler上下文
     */
//    private HandlerContext afterHandlerContext;
    private LinkedHandlerContext afterHandlerContexts;

    @JsonIgnore
    public void validateCode(){
        Assert.notEmpty(code, "rest process code must not be null!");
    }
}
