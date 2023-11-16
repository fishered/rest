package com.asset.rest.biz.tools;

import com.asset.rest.exception.NoSupportException;
import org.springframework.stereotype.Component;

/**
 * @author fisher
 * @date 2023-10-19: 17:16
 * 生成指定位数随机数工具
 */
@Component
public class RandomTool implements Tools{

    public static int generateRandomNumber(int numDigits) {
        int min = (int) Math.pow(10, numDigits - 1);
        int max = (int) Math.pow(10, numDigits) - 1;
        return (int) (Math.random() * (max - min + 1)) + min;
    }


    @Override
    public Object toolCore(Object param) {
        if (!isSupport(param)){
            throw new NoSupportException("[rest] tool random is not support! please check param!");
        }
        int numDigits = (Integer) param;
        int min = (int) Math.pow(10, numDigits - 1);
        int max = (int) Math.pow(10, numDigits) - 1;
        return (int) (Math.random() * (max - min + 1)) + min;
    }

    @Override
    public String toolName() {
        return "random";
    }

    @Override
    public boolean isSupport(Object param) {
        if (param != null && param instanceof Integer){
            return true;
        }
        return false;
    }

}
