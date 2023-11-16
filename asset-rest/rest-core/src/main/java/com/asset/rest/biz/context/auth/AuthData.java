package com.asset.rest.biz.context.auth;

import cn.hutool.core.lang.Assert;
import com.asset.rest.enums.Position;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

/**
 * @author fisher
 * @date 2023-09-13: 14:24
 * 认证的上下文
 */
@Data
public class AuthData {

    /**
     * 认证key
     */
    private String key;

    /**
     * 认证token
     */
    private String value;

    /**
     * 认证位置
     */
    private Position position;

    @JsonIgnore
    public void isSupportThrowEx(){
        Assert.notEmpty(key, "[authData] key must not null!");
        Assert.notEmpty(value, "[authData] value must not null!");
        Assert.notNull(position, "[authData] position must not null!");
    }

}
