package com.asset.rest.biz.context.param;

import cn.hutool.core.lang.Assert;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author fisher
 * @date 2023-10-10: 10:09
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TemplateContext {

    /**
     * 模板输出对应的key
     */
    private String key;

    /**
     * 对应的模板
     */
    private String template;

    @JsonIgnore
    public void isSupportThrowEx(){
        Assert.notEmpty(key, "[TemplateContext] key must not null!");
        Assert.notEmpty(template, "[TemplateContext] template must not null!");
    }

}
