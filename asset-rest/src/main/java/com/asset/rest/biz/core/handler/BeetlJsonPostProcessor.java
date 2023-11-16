//package com.asset.rest.biz.core.handler;
//
//import cn.hutool.core.lang.Assert;
//import com.asset.rest.biz.TemplateBiz;
//import com.asset.rest.biz.context.handler.HandlerContext;
//import com.asset.rest.biz.context.handler.LinkedHandlerContext;
//import com.asset.rest.biz.core.HandlerPostProcessor;
//import com.asset.rest.enums.Handler;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import java.io.IOException;
//
///**
// * @author fisher
// * @date 2023-11-02: 21:33
// */
//@Component
//@Slf4j
//public class BeetlJsonPostProcessor<E extends LinkedHandlerContext> implements HandlerPostProcessor {
//
//    @Autowired
//    private TemplateBiz templateBiz;
//
//    @Override
//    public void handler(HandlerContext handlerContext) {
//        log.info("[beetl] handlerContext: {}", handlerContext);
//        try {
//            //认为template是一个json
//            String template = templateBiz.template(handlerContext.getParams(), handlerContext.getHandlerData());
//            log.info("[rest] beetl handler result: " + template);
//
//        } catch (IOException e) {
//            log.info("[rest] beetl template has a error: " + e.getMessage());
//            throw new RuntimeException(e);
//        }
//    }
//
//    @Override
//    public Handler getHandler() {
//        return Handler.BEETL;
//    }
//
//    private void isSupportThrowEx(HandlerContext handlerContext){
//        Assert.notBlank(handlerContext.getHandlerData(), "beetl config url not null!");
//        Assert.notNull(handlerContext.getParams(), "beetl config param not null!");
//    }
//}
