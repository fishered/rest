package com.asset.rest.biz.custom;

import com.asset.rest.biz.context.ProcessParam;
import com.asset.rest.biz.context.auth.AuthContext;
import com.asset.rest.biz.context.header.HeaderContext;
import com.asset.rest.biz.context.param.BodyContext;

public interface CustomProcess {

    /**
     * 认证的定制化处理
     * @param authContext
     * @param param
     */
    void authProcess(AuthContext authContext, ProcessParam param);

    /**
     * header的定制化处理
     * @param headerContext
     * @param param
     */
    void headerProcess(HeaderContext headerContext, ProcessParam param);

    /**
     * param的定制化处理
     * @param bodyContext
     * @param param
     */
    void paramProcess(BodyContext bodyContext, ProcessParam param);

    /**
     * 定制的类型
     * @return
     */
    String type();



}
