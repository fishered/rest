package com.asset.rest.biz.core;

import com.alibaba.fastjson.JSON;
import com.asset.rest.biz.ProcessContextVerificationBiz;
import com.asset.rest.biz.TemplateBiz;
import com.asset.rest.biz.context.ProcessContext;
import com.asset.rest.biz.context.ProcessParam;
import com.asset.rest.biz.context.auth.AuthContext;
import com.asset.rest.biz.context.auth.AuthData;
import com.asset.rest.biz.context.param.BodyContext;
import com.asset.rest.biz.context.param.ParamFix;
import com.asset.rest.domain.RestInfo;
import com.asset.rest.enums.ProcessType;
import com.asset.rest.exception.NoRuleException;
import com.asset.rest.exception.NoUrlException;
import com.asset.rest.service.RestInfoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * @author fisher
 * @date 2023-09-13: 16:02
 */
@Component
@Slf4j
public class RestProcess extends AbstractProcess {

    @Override
    public void prepareAuth(AuthContext authContext, ProcessParam param) {
        if (!authContext.isSupportProcess()){
            return;
        }
        List<AuthData> authDataList = authContext.getAuthDataList();
        if (!CollectionUtils.isEmpty(authDataList)){
            Map<String, Object> headerParam = new LinkedHashMap<>();
            authDataList.stream().forEach(e -> {
                switch (e.getPosition()) {
                    case URL_JOIN:
                        // 执行URL拼接的处理逻辑
                        headerParam.put(e.getKey(), e.getValue());
                        break;
                    case HEADER:
                        // 执行放入请求头的处理逻辑
                        param.putHeaders(e.getKey(), e.getValue());
                        break;
                    case PARAMS:
                        // 执行放入请求参数的处理逻辑
                        param.putParams(e.getKey(), e.getValue());
                        break;
                    default:
                        // 处理未知的PositionEnum值
                        log.info("[restProcess] unknown PositionEnum!");
                        break;
                }
            });
            urlJoinProcess(headerParam, param);
        }
        super.prepareAuth(authContext, param);
    }

    private void urlJoinProcess(Map<String, Object> headerParam, ProcessParam param) {
        if (CollectionUtils.isEmpty(headerParam)){
            return;
        }
        // URL拼接的处理逻辑
        String url = param.getUrl();
        if (StringUtils.isEmpty(url)){
            throw new NoUrlException("url is not found!");
        }
//        if (!StringUtils.endsWith(url, "/")) {
//            url = url.concat("/");
//        }
        param.setUrl(appendParamsToUrl(url, headerParam));
    }

    public static String appendParamsToUrl(String url, Map<String, Object> params) {
        StringBuilder sb = new StringBuilder(url);
        boolean isFirstParam = true;

        for (Map.Entry<String, Object> entry : params.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            if (isFirstParam) {
                sb.append("?");
                isFirstParam = false;
            } else {
                sb.append("&");
            }
            sb.append(key).append("=").append(encodeValue(value));
        }
        return sb.toString();
    }

    private static String encodeValue(Object value) {
        try {
            return URLEncoder.encode(value.toString(), StandardCharsets.UTF_8.toString());
        } catch (Exception e) {
            // 处理编码异常
            e.printStackTrace();
            return "";
        }
    }

    @Override
    void processResp() {

    }

    @Override
    public ProcessType getProcessEnum() {
        return ProcessType.REST;
    }
}
