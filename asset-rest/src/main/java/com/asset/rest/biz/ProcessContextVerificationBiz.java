package com.asset.rest.biz;

import cn.hutool.core.lang.Assert;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.asset.rest.biz.context.NeedfulParam;
import com.asset.rest.biz.context.ProcessContext;
import com.asset.rest.biz.context.auth.AuthContext;
import com.asset.rest.biz.context.handler.LinkedHandlerContext;
import com.asset.rest.biz.context.header.HeaderContext;
import com.asset.rest.biz.context.param.BodyContext;
import com.asset.rest.biz.context.param.ParamFix;
import com.asset.rest.biz.context.param.TemplateContext;
import com.asset.rest.biz.util.ContextUtil;
import com.asset.rest.domain.RestInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * @author fisher
 * @date 2023-09-19: 16:03
 */
@Component
public class ProcessContextVerificationBiz {


    /**
     * 检验基于字符串配置的参数
     */
    public void verifyConfigThrowEx(String json){
        Assert.notEmpty(json, "[verify] 传入的参数是空的！");
        RestInfo restInfo = JSONObject.parseObject(json, RestInfo.class);
        verifyThrowEx(generalContext(restInfo, null));
    }

    public void verifyRestInfoThrowEx(RestInfo restInfo){
        Assert.notNull(restInfo, "[verify] 传入的参数是空的！");
        verifyThrowEx(generalContext(restInfo, null));
    }

    /**
     * 检验上下文的非法参数
     * @param processContext
     */
    public void verifyThrowEx(ProcessContext processContext){
        processContext.validateCode();
        NeedfulParam needfulParam = processContext.getNeedfulParam();
        Assert.notNull(needfulParam, "Missing needful parameters!");
        //TODO rest的本质是做rest操作 现在它可能最后不需要做rest操作了 所以这里的校验需要重新考虑 是否需要放在core下
//        needfulParam.checkThrowEx();

        AuthContext authContext = processContext.getAuthContext();
        if (authContext != null){
            authContext.isSupportThrowEx();
        }
        BodyContext bodyContext = processContext.getBodyContext();
        if (bodyContext != null){
            bodyContext.isSupportThrowEx();
        }
        HeaderContext headerContext = processContext.getHeaderContext();
        if (headerContext != null){
            bodyContext.isSupportThrowEx();
        }
        LinkedHandlerContext beforeHandlerContexts = processContext.getBeforeHandlerContexts();
        if (beforeHandlerContexts != null){
            beforeHandlerContexts.isSupportThrowEx();
        }
        LinkedHandlerContext afterHandlerContexts = processContext.getAfterHandlerContexts();
        if (afterHandlerContexts != null){
            afterHandlerContexts.isSupportThrowEx();
        }
    }

    /**
     * 根据配置生成context
     * @param restInfo
     * @return
     */
    public ProcessContext generalContext(RestInfo restInfo, ProcessContext processContext){
        Assert.notNull(restInfo, "[general] 传入的restInfo是空的！");
        if (processContext == null){
            processContext = ProcessContext.builder().code(restInfo.getCode()).build();
        }
        //设置认证上下文
        if (StringUtils.isNotEmpty(restInfo.getAuth())){
            processContext.setAuthContext(ContextUtil.generalAuthData(restInfo.getAuth()));
        }
        //设置请求头
        if (StringUtils.isNotEmpty(restInfo.getHeader())){
            processContext.setHeaderContext(ContextUtil.generalHeader(restInfo.getHeader()));
        }
        //设置签名
        if (StringUtils.isNotEmpty(restInfo.getSign())){
            processContext.setSignContext(ContextUtil.generalSign(restInfo.getSign()));
        }
        //设置请求参数
        BodyContext bodyContext = new BodyContext();
        if (StringUtils.isNotEmpty(restInfo.getParamsFix())){
            bodyContext.setParamFixes(JSONArray.parseArray(restInfo.getParamsFix(), ParamFix.class));
        }
        if (StringUtils.isNotBlank(restInfo.getParamTemplate())){
            bodyContext.setTemplate(restInfo.getParamTemplate());
//            bodyContext.setTemplateContexts(JSONArray.parseArray(restInfo.getParamTemplate(), TemplateContext.class));
        }
        //模拟请求过来的参数
        if (StringUtils.isNotBlank(processContext.getParams())){
            bodyContext.setParam(processContext.getParams());
        }
        processContext.setBodyContext(bodyContext);

        //设置必要的参数
        processContext.setNeedfulParam(NeedfulParam.builder()
                .ip(restInfo.getIp())
                .port(restInfo.getPort())
                .suffixUrl(restInfo.getSuffixUrl())
                .isHttps(restInfo.isHttps())
                .restType(restInfo.getRestType())
                .contentType(restInfo.getContentType())
                .build());

        //设置前置和后置handler
        if (StringUtils.isNotEmpty(restInfo.getBeforeHandler())){
            processContext.setBeforeHandlerContexts(ContextUtil.generalHandlers(restInfo.getBeforeHandler()));
        }
        if (StringUtils.isNotEmpty(restInfo.getAfterHandler())){
            processContext.setAfterHandlerContexts(ContextUtil.generalHandlers(restInfo.getAfterHandler()));
        }
        return processContext;
    }
}
