package com.asset.rest.biz.context.handler;

import cn.hutool.core.lang.Assert;
import com.asset.rest.enums.Handler;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.Map;

/**
 * @author fisher
 * @date 2023-09-13: 15:00
 */
@Data
public class HandlerContext {

    private Handler handler;

    /**
     * 更详细的handler类型 例如handler 是SQL handlerType就是对应的operaType
     */
    private String operaType;

    /**
     * handler的值 具体自定义 例如基于sql的handler可以是sql语句...
     */
    private String handlerData;

    /**
     * handler的排序
     */
    private Integer sort;

    /**
     * 处理完成后的请求参数
     */
    private Map params;

    /**
     * 处理完成后的返回结果
     */
    private String resp;

    @JsonIgnore
    public void isSupportThrowEx(){
        Assert.notNull(handler, "[handlerContext] handler is null!");
        Assert.notEmpty(operaType, "[handlerContext] operaType is null!");
        Assert.notEmpty(handlerData, "[handlerContext] handlerData is null!");
    }

}
