package com.asset.rest.biz.context;

import cn.hutool.core.lang.Assert;
import com.asset.rest.enums.RestType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author fisher
 * @date 2023-09-14: 11:00
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NeedfulParam {

    private String ip;

    private String port;

    private String suffixUrl;

    private Boolean isHttps;

    private RestType restType;

    private String contentType;

    @JsonIgnore
    public void checkThrowEx(){
//        Assert.notNull(needfulParam, "Missing needful parameters!");
        Assert.notEmpty(ip, "Missing needful parameters : ip!");
        Assert.notEmpty(port, "Missing needful parameters : port!");
        Assert.notEmpty(suffixUrl, "Missing needful parameters : suffixUrl!");
        Assert.notEmpty(contentType, "Missing needful parameters : contentType!");
        Assert.notNull(restType, "Missing needful parameters : restType!");
        Assert.notNull(isHttps, "Missing needful parameters : isHttps!");
    }

}
