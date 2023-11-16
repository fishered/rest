package com.asset.rest.biz.core;

import cn.hutool.core.lang.Assert;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.asset.rest.biz.HandlerBiz;
import com.asset.rest.biz.ProcessContextVerificationBiz;
import com.asset.rest.biz.SourceCache;
import com.asset.rest.biz.TemplateBiz;
import com.asset.rest.biz.context.NeedfulParam;
import com.asset.rest.biz.context.ProcessContext;
import com.asset.rest.biz.context.ProcessParam;
import com.asset.rest.biz.context.auth.AuthContext;
import com.asset.rest.biz.context.handler.LinkedHandlerContext;
import com.asset.rest.biz.context.header.HeaderContext;
import com.asset.rest.biz.context.param.BodyContext;
import com.asset.rest.biz.context.param.ParamFix;
import com.asset.rest.biz.context.param.TemplateContext;
import com.asset.rest.biz.context.sign.SignContext;
import com.asset.rest.biz.custom.AbstractCustomProcess;
import com.asset.rest.contrast.BizContrast;
import com.asset.rest.domain.RestInfo;
import com.asset.rest.exception.NoRuleException;
import com.asset.rest.service.RestInfoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.*;

/**
 * @author fisher
 * @date 2023-09-13: 15:29
 */
@Slf4j
public abstract class AbstractProcess implements Process {

    @Autowired
    private HandlerBiz handlerBiz;

    @Autowired
    private ProcessContextVerificationBiz verificationBiz;

    @Autowired
    private RestInfoService restInfoService;

    @Autowired
    private SourceCache sourceCache;

    @Autowired
    private TemplateBiz templateBiz;

    @Autowired(required = false)
    private List<AbstractCustomProcess> customProcesses;

    private HookProcess hookProcess;

    private CoreProcess coreProcess;

    /**
     * 注入钩子实现
     * @param hook
     */
    public void setHook(HookProcess hook) {
        this.hookProcess = hook;
    }
    /**
     * 注入核心实现
     */
    public void setCore(CoreProcess core) {
        this.coreProcess = core;
    }

    protected void isSupportThrowEx(ProcessContext processContext){
        processContext.validateCode();
        NeedfulParam needfulParam = processContext.getNeedfulParam();
        Assert.notNull(needfulParam, "Missing needful parameters!");
        Assert.notEmpty(needfulParam.getIp(), "Missing needful parameters : ip!");
        Assert.notEmpty(needfulParam.getPort(), "Missing needful parameters : port!");
        Assert.notEmpty(needfulParam.getSuffixUrl(), "Missing needful parameters : suffixUrl!");
        Assert.notEmpty(needfulParam.getContentType(), "Missing needful parameters : contentType!");
        Assert.notNull(needfulParam.getRestType(), "Missing needful parameters : restType!");
        Assert.notNull(needfulParam.getIsHttps(), "Missing needful parameters : isHttps!");

        AuthContext authContext = processContext.getAuthContext();
        if (authContext != null){
            authContext.isSupportThrowEx();
        }
    }

    protected boolean prepareUrl(NeedfulParam needfulParam, ProcessParam param){
        //检验是否需要支持http核心处理
        try {
            needfulParam.checkThrowEx();
        } catch (Exception e) {
            log.info(String.format("[rest]当前配置无法支持http操作:" + e.getMessage()));
            return false;
        }

        StringBuilder sb = new StringBuilder(
                needfulParam.getIsHttps()?"https":"http"
        );
        sb.append("://");
        sb.append(needfulParam.getIp());
        sb.append(":");
        sb.append(needfulParam.getPort());
        sb.append("/");
        sb.append(needfulParam.getSuffixUrl());
        param.setUrl(sb.toString());
        param.setIsHttps(needfulParam.getIsHttps());
        param.setRestType(needfulParam.getRestType());
        param.putHeaders(BizContrast.HEADER_TYPE_KEY, needfulParam.getContentType());
        return true;
    }

    protected void prepareHeader(HeaderContext headerContext, ProcessParam param){
        if (Objects.isNull(headerContext)){
            return;
        }
//        String contentType = headerContext.getContentType();
//        if (StringUtils.isNotEmpty(contentType)){
//            param.putHeaders(BizContrast.HEADER_TYPE_KEY, contentType);
//        }
        Map<String, Object> headerJson2Map = headerContext.headerJson2Map();
        if (!CollectionUtils.isEmpty(headerJson2Map)){
            headerJson2Map.entrySet().forEach(entry -> {
                param.putHeaders(entry.getKey(), entry.getValue());
            });
        }
    }

    protected void prepareSign(SignContext signContext, ProcessParam param){
        if (signContext != null && signContext.isStatus()){
            param.putParams("sign", signContext.generalSign());
        }
    }

    /**
     * 处理前置handler
     * @param linkedHandlerContext
     * @param param
     */
    protected void beforeProcess(LinkedHandlerContext linkedHandlerContext, ProcessParam param){
        if (handlerBiz == null){
            return;
        }
        if (linkedHandlerContext == null || CollectionUtils.isEmpty(linkedHandlerContext.getHandlerContexts())){
            return;
        }
        linkedHandlerContext.addParam(param.getParams());
        handlerBiz.processHandler(linkedHandlerContext);
    }

    /**
     * 处理后置handler
     * @param linkedHandlerContext
     * @param param
     */
    protected void afterProcess(LinkedHandlerContext linkedHandlerContext, ProcessParam param){
        if (handlerBiz == null){
            return;
        }
        if (linkedHandlerContext == null || CollectionUtils.isEmpty(linkedHandlerContext.getHandlerContexts())){
            return;
        }
        handlerBiz.processHandler(linkedHandlerContext);
    }

    public String process(ProcessContext processContext){
        //根据loader或者db 获取加载的配置信息
        prepareContext(processContext);

        //验证参数
        if (verificationBiz != null){
            verificationBiz.verifyThrowEx(processContext);
        }
//        isSupportThrowEx(processContext);

        //验证完成开始组装
        ProcessParam param = new ProcessParam();

        //处理url及必须参数
        boolean supportHttp = prepareUrl(processContext.getNeedfulParam(), param);

        //处理参数
        prepareParam(processContext.getBodyContext(), param);

        //处理header
        prepareHeader(processContext.getHeaderContext(), param);

        //处理授权认证
        prepareAuth(processContext.getAuthContext(), param);

        //签名的处理
        prepareSign(processContext.getSignContext(), param);

        //beetl template处理
        prepareTemplate(processContext.getBodyContext(), param);

        //前置处理
        beforeProcess(processContext.getBeforeHandlerContexts(), param);

        //钩子方法
        if (hookProcess != null) {
            hookProcess.hookProcess();
        }

        String result = StringUtils.EMPTY;
        //核心处理
        if (supportHttp && coreProcess != null) {
            result = coreProcess.exec(param);
        }

        //后置处理
        afterProcess(processContext.getAfterHandlerContexts(), param);

        //处理返回
        processResp();

        return result;

    }

    public void prepareContext(ProcessContext processContext){
        processContext.validateCode();
        String code = processContext.getCode();

        //从db或loader加载信息
        RestInfo restInfo = Optional.ofNullable(
                sourceCache.getSource(code)
        ).get();
//                .orElse(
//                restInfoService.getById(code)
//        );
        if (restInfo == null){
            throw new NoRuleException("not find rule : " + code);
        }
        verificationBiz.generalContext(restInfo, processContext);
    }

    public void prepareAuth(AuthContext authContext, ProcessParam param){
        if (Objects.isNull(authContext)){
            return;
        }
        customAuthProcess(authContext, param);
    }

    public void prepareTemplate(BodyContext bodyContext, ProcessParam param){
        if (Objects.isNull(bodyContext)){
            return;
        }
        String template = bodyContext.getTemplate();
        if (StringUtils.isNotEmpty(template)){
            //利用template解析
            Map<String, Object> params = param.getParams();
            try {
                //默认是一个json
                String templateRes = templateBiz.template(params, template);
                log.info("[rest] beetl template process res: " + templateRes);
                if (StringUtils.isNotEmpty(templateRes)){
                    Map map = JSON.parseObject(templateRes, new TypeReference<Map<String, Object>>() {});
                    if (!CollectionUtils.isEmpty(map)){
                        param.setParams(map);
                    }
                }
            } catch (IOException ex) {
                log.info("[rest] beetl template has a error: " + ex.getMessage());
                throw new RuntimeException(ex);
            }
        }
    }

    public void prepareParam(BodyContext bodyContext, ProcessParam param){
        if (Objects.isNull(bodyContext)){
            return;
        }
        Map<String, Object> map = new HashMap<>();
        //先处理模板,将模板转换成指定格式
        String inputParam = bodyContext.getParam();
        if (!StringUtils.isEmpty(inputParam)){
            // 将JSON字符串转换为Map
            map = JSON.parseObject(inputParam, HashMap.class);
        }
        //再处理设置的固定值
        List<ParamFix> paramFixes = bodyContext.getParamFixes();
        insertFix(map, paramFixes);
        param.setParams(map);

//        List<TemplateContext> templateContexts = bodyContext.getTemplateContexts();
//        //最后处理template
//        if (!CollectionUtils.isEmpty(templateContexts)){
//            Map<String, Object> finalMap = map;
//            templateContexts.stream().forEach(e -> {
//                e.isSupportThrowEx();
//                try {
//                    String template = templateBiz.template(finalMap, e.getTemplate());
//                    if (StringUtils.isNotEmpty(template)){
//                        param.putParams(e.getKey(), template);
//                    }
//                } catch (IOException ex) {
//                    log.info("[rest] beetl template has a error: " + ex.getMessage());
//                    throw new RuntimeException(ex);
//                }
//            });
//        }
        log.info("[prepareParam] process param: " + JSON.toJSONString(param.getParams()));
    }

    abstract void processResp();

    private Map<String, Object> insertFix(Map<String, Object> map, List<ParamFix> paramFixes){
        if (!CollectionUtils.isEmpty(paramFixes)){
            paramFixes.stream().forEach(e -> {
                switch (e.getFixed()) {
                    case FIX_FILL:
                    case SETTING_FIX:
                        map.put(e.getKey(), e.getValue());
                        break;
                    case MAPPING_KEY:
                        // 先获取映射的key
                        String mappingKey = e.getValue();
                        map.putIfAbsent(mappingKey, map.get(e.getKey()));
                        map.remove(e.getKey());
                        break;
                    default:
                        // 处理未知的PositionEnum值
                        log.info("[restProcess] unknown FixedValue!");
                        break;
                }
            });
        }
        return map;
    }

    /**
     * 定制化的customer auth
     */
    protected void customAuthProcess(AuthContext authContext, ProcessParam param){
        if (authContext == null || StringUtils.isEmpty(authContext.getCustom())){
            return;
        }
        if (CollectionUtils.isEmpty(customProcesses)){
            return;
        }
        AbstractCustomProcess abstractCustomProcess = customProcesses.stream().filter(e -> authContext.getCustom().equals(e.type()))
                .findFirst().get();

        if (abstractCustomProcess == null){
            return;
        }
        abstractCustomProcess.authProcess(authContext, param);
    }

    public static void main(String[] args) {
        String json = "{\n" +
                "    \"UNI_BSS_HEAD\": {\n" +
                "        \"APP_ID\": \"123\", \n" +
                "        \"TIMESTAMP\": \"2023-11-06 15:41:15 048\", \n" +
                "        \"TRANS_ID\": \"1699256475404266\", \n" +
                "        \"TOKEN\": \"3dbd2f21f2bc5d2908b325619f51e6ad\", \n" +
                "        \"RESERVED\": []\n" +
                "    }, \n" +
                "    \"UNI_BSS_BODY\": {\n" +
                "        \"SMSINDUSTRY_REQ\": {\n" +
                "            \"INDUSTRY_SMS_REQ\": {\n" +
                "                \"APP_ID\": \"123\", \n" +
                "                \"MOBILE_NUMBER\": \"[\"13\",\"12\"]\", \n" +
                "                \"APP_SECRET\": \"test\", \n" +
                "                \"CONTENT\": \"测试测试\", \n" +
                "                \"MSG_TYPE\": \"9\", \n" +
                "                \"CIP\": \"0\"\n" +
                "            }\n" +
                "        }\n" +
                "    }, \n" +
                "    \"UNI_BSS_ATTACHED\": {\n" +
                "        \"MEDIA_INFO\": \"\"\n" +
                "    }\n" +
                "}";
        Map map = JSON.parseObject(json, Map.class);
        System.out.println(map);
    }

}
