package com.asset.rest.biz.core;

import com.asset.rest.enums.ProcessType;

/**
 * @author fisher
 * @date 2023-09-13: 15:51
 */
public interface HookProcess {

    /**
     * 预留的钩子方法
     */
    void hookProcess();

    ProcessType getType();

}
