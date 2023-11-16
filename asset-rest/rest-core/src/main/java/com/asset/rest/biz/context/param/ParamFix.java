package com.asset.rest.biz.context.param;

import cn.hutool.core.lang.Assert;
import com.asset.rest.enums.FixedValue;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author fisher
 * @date 2023-09-14: 14:56
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ParamFix {

    private FixedValue fixed;

    private String key;

    private String value;

    @JsonIgnore
    public void isSupportThrowEx(){
        Assert.notEmpty(key, "[paramFix] key must not null!");
        Assert.notEmpty(value, "[paramFix] value must not null!");
        Assert.notNull(fixed, "[paramFix] fixed must not null!");
    }
}
