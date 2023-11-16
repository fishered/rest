package com.asset.rest.biz.tools;

import com.asset.rest.exception.NoSupportException;
import org.springframework.stereotype.Component;

import java.util.Base64;

/**
 * @author fisher
 * @date 2023-10-19: 18:17
 * base64 转换工具
 */
@Component
public class Base64Tool implements Tools{
    @Override
    public Object toolCore(Object param) {
        if (!isSupport(param)){
            throw new NoSupportException("[rest] tool base64 is not support! please check param!");
        }
        byte[] bytes = String.valueOf(param).getBytes();
        byte[] encodedBytes = Base64.getEncoder().encode(bytes);
        return new String(encodedBytes);
    }

    @Override
    public String toolName() {
        return "base64";
    }

    @Override
    public boolean isSupport(Object param) {
        if (param != null && param instanceof String){
            return true;
        }
        return false;
    }
}
