package com.asset.rest.biz;

import com.asset.rest.enums.SourceType;

public interface SourceLoader {

    /**
     * 加载文件的类型
     * @return
     */
    SourceType getType();

    /**
     * 获取source的加载路径
     * @return
     */
    String getSourcePath();

    /**
     * 读取
     */
    void loader();



}
