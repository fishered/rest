package com.asset.rest.biz.tools;

import cn.hutool.core.lang.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Optional;

/**
 * @author fisher
 * @date 2023-10-20: 9:14
 */
@Component
public class ToolsHandler {

    @Autowired(required = false)
    private List<Tools> tools;

    /**
     * tools process
     * @param type
     */
    public Object tools(String type, Object param){
        Assert.notBlank(type, "[rest] type validate not null!");
        if (CollectionUtils.isEmpty(tools)){
            throw new IllegalArgumentException("[rest] tools not support!");
        }
        Tools tool = Optional.ofNullable(
                tools.stream()
                        .filter(e -> type.equals(e.toolName()))
                        .findFirst().get()
        ).orElseThrow(() -> new IllegalArgumentException("[rest] tools not support:" + type));
        return tool.toolCore(param);
    }

}
