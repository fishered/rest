package com.asset.rest.biz.core;

import com.asset.rest.biz.context.ProcessParam;
import com.asset.rest.enums.ProcessType;

public interface CoreProcess {

    /**
     * 执行的方法
     * @return
     */
    String exec(ProcessParam processParam);

    /**
     * 指定核心处理的类型
     * @return
     */
    ProcessType getType();

}
