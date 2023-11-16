package com.asset.rest.biz;

import cn.hutool.core.lang.Assert;
import com.alibaba.fastjson.JSONObject;
import com.asset.rest.biz.core.AbstractProcess;
import com.asset.rest.exception.NoSupportException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;

/**
 * @author fisher
 * @date 2023-10-09: 10:58
 */
@Component
public class ProcessBiz {

    @Autowired(required = false)
    private List<AbstractProcess> process;

    /**
     * 核心的处理方式
     */
    public void process(String code, String inputParam){
        Assert.notEmpty(code, "code must not null!");
        isSupportThrowEx();
        isSupportInputParamThrowEx(inputParam);
        /**
         * 先从cache或db中获取配置
         */


    }

    private void isSupportThrowEx(){
        if (CollectionUtils.isEmpty(process)){
            throw new NoSupportException("[rest] this type not support!");
        }
    }

    private void isSupportInputParamThrowEx(String inputParam){
        if (StringUtils.isNotBlank(inputParam)){
            JSONObject.parseObject(inputParam);
        }
    }

}
