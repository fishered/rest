package com.asset.rest.biz.context.handler;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;

/**
 * @author fisher
 * @date 2023-09-19: 10:41
 */
@Data
public class LinkedHandlerContext {

    private List<HandlerContext> handlerContexts;

    @JsonIgnore
    public void isSupportThrowEx(){
        if (!CollectionUtils.isEmpty(handlerContexts)){
            handlerContexts.stream().forEach(e -> e.isSupportThrowEx());
        }
    }

    /**
     * 给所有的handler添加param
     */
    @JsonIgnore
    public void addParam(Map param){
        if (CollectionUtils.isEmpty(param)){
            return;
        }
        if (!CollectionUtils.isEmpty(handlerContexts)){
            handlerContexts.stream().forEach(e -> e.setParams(param));
        }
    }


}
