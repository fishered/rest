package com.asset.rest.biz.core;

import com.asset.rest.enums.Handler;

public interface HandlerPostProcessor extends AroundHandler{

    /**
     * post processor handler type
     * @return
     */
    Handler getHandler();

}
