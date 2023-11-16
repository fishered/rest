package com.asset.rest.biz;

import cn.hutool.core.lang.Assert;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.asset.rest.domain.RestInfo;
import com.asset.rest.exception.NoRuleException;
import com.asset.rest.exception.SourceExistException;
import com.asset.rest.service.RestInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author fisher
 * @date 2023-09-21: 11:22
 */
@Component
public class SourceCache {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RestInfoService restInfoService;

    @Autowired
    private ProcessContextVerificationBiz verificationBiz;

    /**
     * source condition
     */
    private static ConcurrentHashMap<String, RestInfo> source = new ConcurrentHashMap<>();

    /**
     * group condition
     */
    private static ConcurrentHashMap<String, List<String>> group = new ConcurrentHashMap<>();

    /**
     * 初始化只应该在启动时执行一次
     */
    public void putSourceThrowEx(RestInfo restInfo){
        Assert.notNull(restInfo, "[resourceLoader] 放入的restInfo是空的！");
        String code = restInfo.getCode();
        Assert.notEmpty(code, "[resourceLoader] 放入的restInfo code非法！");

        if (source.get(code) == null){
            source.put(code, restInfo);
            redisTemplate.opsForHash().put(getHashKey(), code, JSONObject.toJSONString(restInfo));
            return;
        }
        throw new SourceExistException(String.format("{%s}存在重复的配置，请检查后删除无用的配置信息！", code));
    }

    public RestInfo getSource(String code){
        Assert.notEmpty(code, "[resourceLoader] 获取restInfo code非法！");
        Object res = redisTemplate.opsForHash().get(getHashKey(), code);
        if (res != null){
            return JSON.parseObject(String.valueOf(res), RestInfo.class);
        }
        return Optional.ofNullable(
                restInfoService.getById(code)
        ).orElseThrow(
                () -> new NoRuleException("[resourceLoader] 没有找到对应配置 : " + code)
        );
    }

    private String getHashKey(){
        return "rest:info";
    }



}
