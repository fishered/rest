package com.asset.rest.biz.tools;

public interface Tools {

    /**
     * 工具处理实现(接收各种类型的参数)
     * @return
     */
    Object toolCore(Object param);

    /**
     * get tools name
     * @return
     */
    String toolName();

    /**
     * 是否支持执行
     * @return
     */
    boolean isSupport(Object param);

}
