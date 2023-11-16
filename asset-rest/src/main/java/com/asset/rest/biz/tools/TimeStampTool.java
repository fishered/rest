package com.asset.rest.biz.tools;

import com.asset.rest.exception.NoSupportException;
import org.springframework.stereotype.Component;

/**
 * @author fisher
 * @date 2023-10-19: 18:10
 * 生成时间戳
 */
@Component
public class TimeStampTool implements Tools{

    @Override
    public Object toolCore(Object param) {
        if (!isSupport(param)){
            throw new NoSupportException("[rest] tool timestamp is not support! please check param!");
        }
        return System.currentTimeMillis() / 1000;
    }

    @Override
    public String toolName() {
        return "timestamp";
    }

    @Override
    public boolean isSupport(Object param) {
        return true;
    }
}
