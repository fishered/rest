package com.asset.rest.biz;

import cn.hutool.core.lang.Assert;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.asset.rest.domain.RestInfo;
import com.asset.rest.enums.SourceType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * @author fisher
 * @date 2023-09-21: 11:04
 */
@Component
@Slf4j
public class JsonSourceLoader extends AbstractSourceLoader{

    private final ResourceLoader resourceLoader;

    @Autowired
    private SourceCache sourceCache;

    @Autowired
    private ProcessContextVerificationBiz verificationBiz;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    public JsonSourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public SourceType getType() {
        return SourceType.JSON;
    }

    @Override
    public String getSourcePath() {
        return "classpath*:/config/*.json";
    }

    @Override
    public void loader() {
        isSupportThrowEx();
        try {
            ResourcePatternResolver resourcePatternResolver = applicationContext;
            Resource[] resources = resourcePatternResolver.getResources(getSourcePath());
            for (Resource resource : resources) {
                log.info(String.format("[rest]获取到加载配置：{%s}", resource.getFilename()));
                // 处理每个配置文件
                InputStream inputStream = resource.getInputStream();
                String config = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
                Assert.notEmpty(config, "[resourceLoader] 读取的配置文件是空的！{}", resource.getFilename());
//                String content = StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
                // 在这里你可以解析/处理 content
//                verificationBiz.verifyConfigThrowEx(config);
//                sourceCache.putSourceThrowEx(JSONObject.parseObject(config, RestInfo.class));
                processConfig(config);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void processConfig(String config){
        //TODO 默认这个是一个json字符串 也有可能是一个jsonArray字符串
        Object jsonObjectOrArray = null;
        try {
            jsonObjectOrArray = JSON.parse(config);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (jsonObjectOrArray instanceof JSONObject) {
            //如果它是object 属于单个配置
            core(config);
        } else if (jsonObjectOrArray instanceof JSONArray) {
            JSONArray jsonArray = (JSONArray) jsonObjectOrArray;
            jsonArray.stream().forEach(e -> {
                core(e.toString());
            });
        } else {
            throw new IllegalArgumentException("解析的JSON字符串不是一个JSON对象或者JSON数组！");
        }
    }

    private void core(String config){
        verificationBiz.verifyConfigThrowEx(config);
        sourceCache.putSourceThrowEx(JSONObject.parseObject(config, RestInfo.class));
    }

    private void isSupportThrowEx(){
        Assert.notEmpty(getSourcePath(), "[resourceLoader] 加载信息不全！");
    }
}
