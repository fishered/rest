package com.asset.rest.biz;

import cn.hutool.core.lang.Assert;
import com.alibaba.fastjson.JSONObject;
import com.asset.rest.biz.context.ProcessContext;
import com.asset.rest.biz.core.AbstractProcess;
import com.asset.rest.enums.ProcessType;
import com.asset.rest.exception.NoSupportException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;

/**
 * @author fisher
 * @date 2023-09-14: 17:23
 */
@Component
public class RestBiz {

    @Autowired(required = false)
    private List<AbstractProcess> process;

    private static ProcessType DEFAULT_TYPE = ProcessType.REST;

    /**
     * 通过code发起一个完整的rest流程
     * @param code
     * @return
     */
    public String rest(String code, String inputParam){
        Assert.notEmpty(code, "code must not null!");
        isSupportThrowEx();
        isSupportInputParamThrowEx(inputParam);
        AbstractProcess processImpl = process.stream()
                .filter(e -> Objects.equals(DEFAULT_TYPE, e.getProcessEnum()))
                .findFirst().get();

        if (Objects.isNull(processImpl)){
            throw new NoSupportException(
                    String.format("[rest] this type: {%s} not support!", DEFAULT_TYPE));
        }

        if (InitializingBiz.getCore(DEFAULT_TYPE) != null){
            processImpl.setCore(InitializingBiz.getCore(DEFAULT_TYPE));
        }

        if (InitializingBiz.getHook(DEFAULT_TYPE) != null){
            processImpl.setHook(InitializingBiz.getHook(DEFAULT_TYPE));
        }
        ProcessContext processContext = ProcessContext.builder().code(code).build();
        if (StringUtils.isNotBlank(inputParam)){
            processContext.setParams(inputParam);
        }
        return processImpl.process(processContext);
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
