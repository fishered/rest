package com.asset.rest.biz.context.header;

import cn.hutool.core.lang.Assert;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author fisher
 * @date 2023-09-19: 15:40
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HeaderParam {

    /**
     * header的key
     */
    private String key;

    /**
     * header的value
     */
    private String value;

    @JsonIgnore
    public void isSupportThrowEx(){
        Assert.notEmpty(key, "[HeaderParam] key must not null!");
        Assert.notEmpty(value, "[HeaderParam] value must not null!");
    }

}
