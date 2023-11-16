package com.asset.rest.biz.tools;

import com.asset.rest.exception.NoSupportException;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author fisher
 * @date 2023-10-19: 18:01
 */
@Component
public class MD5Tool implements Tools{
    @Override
    public Object toolCore(Object param) {
        if (!isSupport(param)){
            throw new NoSupportException("[rest] tool md5 is not support! please check param!");
        }
        return md5(String.valueOf(param));
    }

    @Override
    public String toolName() {
        return "md5";
    }

    @Override
    public boolean isSupport(Object param) {
        if (param != null && param instanceof String){
            return true;
        }
        return false;
    }

    public static String md5(String password) {
        //生成一个md5加密器
        try {
            //创建具有指定算法名称如MD5的摘要
            MessageDigest md = MessageDigest.getInstance("MD5");
            //使用指定的字节数组更新摘要
            md.update(password.getBytes());
            //BigInteger 将8位数的字符串转成16位的字符串，得到的字符串形式是哈希码值
            //BigInteger(参数1, 参数2) 参数1：1表示正数 0表示零 -1表示负数
            //md.digest() 进行哈希计算并返回一个字节数组
            return new BigInteger(1, md.digest()).toString(16);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException("[rest] md5 general fail:" + e.getMessage());
        }
    }

    public static void main(String[] args) {
        String s = md5("123456sdfg");
        System.out.println(s);
    }
}
