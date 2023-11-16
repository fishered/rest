package com.asset.rest.biz;

import com.asset.rest.biz.context.handler.HandlerContext;
import com.asset.rest.biz.context.handler.LinkedHandlerContext;
import com.asset.rest.biz.core.HandlerPostProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Comparator;
import java.util.List;

/**
 * @author fisher
 * @date 2023-09-19: 14:09
 */
@Component
public class HandlerBiz {

    @Autowired(required = false)
    private List<HandlerPostProcessor> handlerPostProcessors;

    /**
     * 对于handler来说 本身不区分前置后置
     * @param linkedHandlerContext
     */
    public void processHandler(LinkedHandlerContext linkedHandlerContext){
        if (isSupport(linkedHandlerContext)){
            List<HandlerContext> handlerContexts = linkedHandlerContext.getHandlerContexts();
            handlerContexts.stream()
                    .sorted(Comparator.comparing(HandlerContext::getSort))
                    .forEach(e -> handler(e));
        }
    }

    /**
     * is allow
     * @return
     */
    private boolean isSupport(LinkedHandlerContext linkedHandlerContext){
        if (CollectionUtils.isEmpty(handlerPostProcessors)){
            throw new IllegalArgumentException("没有支持的增强handler实现！");
        }
        if (linkedHandlerContext == null || CollectionUtils.isEmpty(linkedHandlerContext.getHandlerContexts())){
            return false;
        }
        return true;
    }

    /**
     * 处理的handler
     * @param context
     */
    private void handler(HandlerContext context){
        context.isSupportThrowEx();
        HandlerPostProcessor handlerPostProcessor = handlerPostProcessors.stream()
                .filter(e -> e.getHandler().equals(context.getHandler()))
                .findFirst()
                .get();
        if (handlerPostProcessor == null){
            throw new IllegalArgumentException(String.format("当前handler:{%s}未实现！", context.getHandler()));
        }
        handlerPostProcessor.handler(context);
    }

}
