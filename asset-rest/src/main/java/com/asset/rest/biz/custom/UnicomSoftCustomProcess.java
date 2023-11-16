package com.asset.rest.biz.custom;

import cn.hutool.core.lang.Assert;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.asset.rest.biz.context.ProcessParam;
import com.asset.rest.biz.context.auth.AuthContext;
import com.asset.rest.biz.tools.MD5Tool;
import com.asset.rest.biz.tools.RandomTool;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * @author fisher
 * @date 2023-11-02: 16:56
 * 软研院
 */
@Component
@Slf4j
public class UnicomSoftCustomProcess extends AbstractCustomProcess{

    private static final String APP_ID = "APP_ID";
    private static final String APP_SECRET = "APP_SECRET";
    private static final String TIMESTAMP = "TIMESTAMP";
    private static final String TRANS_ID = "TRANS_ID";
    private static final String TOKEN = "TOKEN";

    /**
     * 因为在联通软营院接口中,header中的appid和secret与body中不同。
     * 之前的body中的用于body里的参数，而header中的appid和生成token需要使用header中定义的参数
     */
    private static final String HEADER_APPID = "pQgqFWvODY";
//    private static final String HEADER_SECRET = "Z70XO8Pi135EaPeA8upDFgKrPMF7w7xz";
    private static final String HEADER_SECRET = "ES5s0fGn28bXUA7V1hCLCc5DPq7nJg57";

    @Autowired
    private RandomTool randomTool;

    @Override
    public void authProcess(AuthContext authContext, ProcessParam param) {
        Map<String, Object> params = param.getParams();
        if (CollectionUtils.isEmpty(params)){
            throw new IllegalArgumentException("[rest] custom unicom_soft found error!params not exist!");
        }

        String appId = (String) params.get(APP_ID);
        String appSecret = (String) params.get(APP_SECRET);
        Assert.notEmpty(appId, "[rest] custom unicom_soft not found appId!");
        Assert.notEmpty(appId, "[rest] custom unicom_soft not found appSecret!");

        log.info("[rest] - unicom_soft 获取到appId：" + appId);
        log.info("[rest] - unicom_soft 获取到appSecret：" + appSecret);

        //生成TIMESTAMP 和TRANS_ID
        long timeMillis = System.currentTimeMillis();
        //先放入时间
        String transId = (timeMillis / 1000) + String.valueOf(randomTool.toolCore(6));
        String timestamp = UnicomCenterCustomProcess.convertTimestamp(timeMillis);

        param.putParams(APP_ID, appId);
        param.putParams(TIMESTAMP, timestamp);
        param.putParams(TRANS_ID, transId);

        //得到系统参数的列表
        List<String> sysParams = Arrays.asList(APP_ID, TIMESTAMP, TRANS_ID);

        //生成token
        StringBuilder tokenParamString = new StringBuilder();
        new TreeMap<>(param.getParams()).entrySet().forEach(entry -> {
            if (sysParams.contains(entry.getKey())){
                tokenParamString.append(entry.getKey());
                if (APP_ID.equals(entry.getKey())){
                    //生成token用的id是header的id
                    tokenParamString.append(HEADER_APPID);
                    return;
                }
                tokenParamString.append(entry.getValue());
            }
        });
        //拼装appSecret
        log.info("[rest] - unicom-soft 拼接指定secret: " + HEADER_SECRET);
        tokenParamString.append(HEADER_SECRET);

        log.info("[rest] - unicom_soft 生成token的参数拼接：" + tokenParamString.toString());
        //MD5处理
        String token = MD5Tool.md5(tokenParamString.toString());
        log.info("[rest] - unicom_soft 生成token：" + token);

        param.putParams(TOKEN, token);

        Object phoneList = param.getParams().get("mobileList");
        //处理phoneList
        if (phoneList != null && phoneList instanceof String){
//            phoneList = Arrays.asList(String.valueOf(phoneList).split(","));
        }
        if (phoneList != null && phoneList instanceof JSONArray){
            phoneList = ((JSONArray) phoneList).toJavaList(String.class).stream().collect(Collectors.joining(","));
        }
        param.putParams("mobileList", JSON.toJSONString(phoneList));

        super.authProcess(authContext, param);
    }

    @Override
    public String type() {
        return "unicom_soft";
    }
}
