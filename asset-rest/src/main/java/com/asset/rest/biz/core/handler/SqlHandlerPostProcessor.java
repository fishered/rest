package com.asset.rest.biz.core.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.asset.rest.biz.context.handler.HandlerContext;
import com.asset.rest.biz.context.handler.LinkedHandlerContext;
import com.asset.rest.biz.core.HandlerPostProcessor;
import com.asset.rest.enums.Handler;
import com.xiong.cheetah.appservice.domain.AppInterfaceBean;
import com.xiong.cheetah.appservice.service.SqlAppservice;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Map;

/**
 * @author fisher
 * @date 2023-09-19: 10:36
 */
@Component
@Slf4j
public class SqlHandlerPostProcessor<E extends LinkedHandlerContext> implements HandlerPostProcessor {

    @Autowired
    private SqlAppservice sqlAppservice;

    @Override
    public Handler getHandler() {
        return Handler.SQL;
    }

    /**
     * 默认的sql通用handler处理
     */
    @Override
    public void handler(HandlerContext handlerContext){
        isSupport(handlerContext);
        AppInterfaceBean appInterfaceBean = new AppInterfaceBean();
        appInterfaceBean.setInterfacetype(handlerContext.getOperaType());
        appInterfaceBean.setInterfacedata(handlerContext.getHandlerData());

        JSONObject data = new JSONObject();
        Map params = handlerContext.getParams();
        if (!CollectionUtils.isEmpty(params)){
            JSONObject input = new JSONObject(params);
            data.put("input", input);
            data.put("inputstr", input.toJSONString());
//            beforedata.put("meta", yibaoMetaData);
        }
        String resp = handlerContext.getResp();
        if (StringUtils.isNotEmpty(resp)){
            data.put("outstr", resp);
            data.put("output", JSON.parseObject(resp));
        }
        JSONObject res = sqlAppservice.doexec(data, appInterfaceBean);
    }

    private void isSupport(HandlerContext handlerContext){
        handlerContext.isSupportThrowEx();
    }

}
