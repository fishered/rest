package com.asset.rest.domain;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.asset.rest.enums.RestType;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Optional;

/**
 * @author fisher
 * @date 2023-09-13: 16:26
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("rest_info")
public class RestInfo {

    /**
     * code作为唯一标识，不可重复
     */
    @TableId(type = IdType.INPUT)
    private String code;

    private String groupId;

    private String name;

    private String ip;

    private String port;

    private String suffixUrl;

    private RestType restType;

    /**
     * content Type
     */
    private String contentType;

    /**
     * 是否是https 1是 0否
     */
    private Integer isHttps;

    /**
     * json string param
     * 请求参数有几种处理 固定值添加、指定key的固定值、映射key
     */
    private String paramsFix;

    /**
     * template string
     */
    private String paramTemplate;

    /**
     * json string header
     */
    private String header;

    /**
     * json string auth
     */
    private String auth;

    /**
     * json string sign
     */
    private String sign;

    /**
     * json string handler process params
     */
    private String beforeHandler;

    private String afterHandler;

    private String description;

    @JsonIgnore
    public boolean isHttps(){
        isHttps = Optional.ofNullable(isHttps).orElse(0);
        return isHttps == 1;
    }

    /**
     * 将domain 生成json配置
     * @return
     */
    @JsonIgnore
    public String generalJsonConfig(){
        return JSON.toJSONString(this);
    }



}
