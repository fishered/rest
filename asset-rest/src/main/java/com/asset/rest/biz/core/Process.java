package com.asset.rest.biz.core;

import com.asset.rest.biz.context.ProcessContext;
import com.asset.rest.enums.ProcessType;

public interface Process {

    /**
     * 核心处理实现
     */
    String process(ProcessContext processContext);

    ProcessType getProcessEnum();

}
