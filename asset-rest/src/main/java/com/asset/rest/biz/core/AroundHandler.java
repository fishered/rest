package com.asset.rest.biz.core;

import com.asset.rest.biz.context.handler.HandlerContext;

public interface AroundHandler<E extends HandlerContext> {

    /**
     * handler 的处理
     * @param e
     */
    void handler(E e);

}
