package com.asset.rest.biz.util;

import com.alibaba.fastjson.JSONObject;
import com.asset.rest.biz.context.auth.AuthContext;
import com.asset.rest.biz.context.handler.HandlerContext;
import com.asset.rest.biz.context.handler.LinkedHandlerContext;
import com.asset.rest.biz.context.header.HeaderContext;
import com.asset.rest.biz.context.sign.SignContext;

/**
 * @author fisher
 * @date 2023-09-14: 10:40
 */
public class ContextUtil {

    /**
     * 将auth的规则转换为auth
     * @param auth
     * @return
     */
    public static AuthContext generalAuthData(String auth){
        return JSONObject.parseObject(auth, AuthContext.class);
    }

    public static HeaderContext generalHeader(String header){
        return JSONObject.parseObject(header, HeaderContext.class);
    }

    public static HandlerContext generalHandler(String handler){
        return JSONObject.parseObject(handler, HandlerContext.class);
    }

    public static LinkedHandlerContext generalHandlers(String handler){
        return JSONObject.parseObject(handler, LinkedHandlerContext.class);
    }

    public static SignContext generalSign(String sign){
        return JSONObject.parseObject(sign, SignContext.class);
    }

}
