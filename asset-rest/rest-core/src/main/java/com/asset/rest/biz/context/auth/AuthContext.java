package com.asset.rest.biz.context.auth;

import com.alibaba.fastjson.annotation.JSONField;
import com.asset.rest.biz.context.CustomContext;
import com.asset.rest.biz.util.CollectionUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * @author fisher
 * @date 2023-09-13: 14:29
 * 详细的认证策略参数
 */
@Data
public class AuthContext extends CustomContext {

    /**
     * 过期时间
     */
    private Long expireTime;

    /**
     * 认证的集合信息
     */
    private List<AuthData> authDataList;

    @JsonIgnore
    public void isSupportThrowEx(){
        if (!CollectionUtils.isEmpty(authDataList)){
            authDataList.stream().forEach(e -> e.isSupportThrowEx());
        }
    }

    /**
     * 是否支持处理
     */
    @JsonIgnore
    @JSONField(serialize = false, deserialize = false)
    public boolean isSupportProcess(){
        if (CollectionUtils.isEmpty(authDataList) || StringUtils.isNotBlank(getCustom())){
            return true;
        }
        return false;
    }





}
