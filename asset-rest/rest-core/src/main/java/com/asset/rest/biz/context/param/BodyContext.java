package com.asset.rest.biz.context.param;

import com.alibaba.fastjson.JSONObject;
import com.asset.rest.biz.util.CollectionUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;


import java.util.List;

/**
 * @author fisher
 * @date 2023-09-13: 14:56
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BodyContext {

    /**
     * 请求参数 传入的请求参数 默认接收json格式的参数
     */
    private String param;

    /**
     * 输出的格式化模板
     */
    private String template;

//    /**
//     * 批量的输出模板
//     */
//    private List<TemplateContext> templateContexts;

    private List<ParamFix> paramFixes;

    public void isSupportThrowEx(){
        if (!CollectionUtils.isEmpty(paramFixes)){
            paramFixes.stream().forEach(e -> e.isSupportThrowEx());
        }
//        if (!CollectionUtils.isEmpty(templateContexts)){
//            templateContexts.stream().forEach(e -> e.isSupportThrowEx());
//        }
        if (StringUtils.isNotEmpty(param)){
            try {
                JSONObject.parseObject(param);
            } catch (Exception e) {
                throw new RuntimeException("[BodyContext] param is error! " + param);
            }
        }
    }

}
