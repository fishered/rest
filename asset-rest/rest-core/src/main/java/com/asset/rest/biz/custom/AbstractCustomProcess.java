package com.asset.rest.biz.custom;

import com.asset.rest.biz.context.ProcessParam;
import com.asset.rest.biz.context.auth.AuthContext;
import com.asset.rest.biz.context.header.HeaderContext;
import com.asset.rest.biz.context.param.BodyContext;

/**
 * @author fisher
 * @date 2023-10-19: 17:02
 */
public abstract class AbstractCustomProcess implements CustomProcess{

    @Override
    public void authProcess(AuthContext authContext, ProcessParam param) {

    }

    @Override
    public void headerProcess(HeaderContext headerContext, ProcessParam param) {

    }

    @Override
    public void paramProcess(BodyContext bodyContext, ProcessParam param) {

    }

}
