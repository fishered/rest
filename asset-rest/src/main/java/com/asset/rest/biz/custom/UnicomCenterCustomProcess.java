package com.asset.rest.biz.custom;

import cn.hutool.core.lang.Assert;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.asset.rest.biz.context.ProcessParam;
import com.asset.rest.biz.context.auth.AuthContext;
import com.asset.rest.biz.tools.MD5Tool;
import com.asset.rest.biz.tools.RandomTool;
import com.asset.rest.biz.tools.TimeStampTool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

/**
 * @author fisher
 * @date 2023-10-19: 17:11
 * 联通中心 定制处理
 */
@Component
@Slf4j
public class UnicomCenterCustomProcess extends AbstractCustomProcess{

    @Autowired
    private TimeStampTool timeStampTool;
    @Autowired
    private RandomTool randomTool;

    private static final String APP_ID = "APP_ID";
    private static final String APP_SECRET = "APP_SECRET";
    private static final String TIMESTAMP = "TIMESTAMP";
    private static final String TRANS_ID = "TRANS_ID";
    private static final String TOKEN = "TOKEN";

    @Override
    public void authProcess(AuthContext authContext, ProcessParam param) {
        Map<String, Object> headers = param.getHeaders();
        if (CollectionUtils.isEmpty(headers)){
            throw new IllegalArgumentException("[rest] custom unicom found error!header not exist!");
        }
        String appId = (String) headers.get(APP_ID);
        String appSecret = (String) headers.get(APP_SECRET);
        Assert.notEmpty(appId, "[rest] custom unicom not found appId!");
        Assert.notEmpty(appId, "[rest] custom unicom not found appSecret!");

        log.info("[rest] - Unicom 获取到appId：" + appId);
        log.info("[rest] - Unicom 获取到appSecret：" + appSecret);

        long timeMillis = System.currentTimeMillis();
        //先放入时间
        String transId = (timeMillis / 1000) + String.valueOf(randomTool.toolCore(6));
//        headers.put(TRANS_ID, (timeMillis / 1000) + String.valueOf(randomTool.toolCore(6)));
        String timestamp = convertTimestamp(timeMillis);
//        headers.put(TIMESTAMP, convertTimestamp(timeMillis));

        //生成token
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(APP_ID);
        stringBuilder.append(appId);
        stringBuilder.append(TIMESTAMP);
        stringBuilder.append(timestamp);
        stringBuilder.append(TRANS_ID);
        stringBuilder.append(transId);

        if (!CollectionUtils.isEmpty(param.getParams())){
            //有参数列表 就要添加上参数列表
            stringBuilder.append(JSON.toJSONString(param.getParams()));
        }

        stringBuilder.append(appSecret);

        log.info("[rest] - Unicom 生成token的参数拼接：" + stringBuilder.toString());
        //MD5处理
        String token = MD5Tool.md5(stringBuilder.toString());

        log.info("[rest] - Unicom 生成token：" + token);

        JSONObject json = new JSONObject();
        json.put(APP_ID, appId);
        json.put(TIMESTAMP, timestamp);
        json.put(TRANS_ID, transId);
        json.put(TOKEN, token);

        log.info("[rest] - Unicom 生成base64 Authentication 的json字符串 ：" + json.toJSONString());
        byte[] serializedBytes = json.toJSONString().getBytes();
        String base64EncodedString = Base64.getEncoder().encodeToString(serializedBytes);

        headers.remove(APP_ID);
        headers.remove(APP_SECRET);
        headers.put("Authentication", base64EncodedString);

        log.info("[rest] - Unicom 生成Authentication：" + base64EncodedString);

        super.authProcess(authContext, param);
    }

    @Override
    public String type() {
        return "unicom";
    }

    public static String convertTimestamp(long timestampMillis) {
        Date date = new Date(timestampMillis);
        SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss SSS");
        return sdf.format(date);
    }
}
