package com.asset.rest.biz.context.sign;

import cn.hutool.core.codec.Base64;
import cn.hutool.crypto.SecureUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

/**
 * @author fisher
 * @date 2023-09-18: 16:24
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignContext {

    private boolean status;

    private String app_id;

    private String app_secret;

    private String timestamp;

    private String version;

    @JsonIgnore
    public String generalSign(){
        if (status){
            if (StringUtils.isNoneEmpty(app_id, app_secret, timestamp, version)){
                StringBuilder sb = new StringBuilder();
                sb.append(app_id).append(app_secret).append(timestamp).append(version);
                String sign = SecureUtil.md5(sb.toString()).toLowerCase();
                sign = Base64.encode(sign);
                return sign;
            }
            throw new IllegalArgumentException("无法生成签名,参数异常！");
        }
        throw new RuntimeException("当前签名策略已关闭！");

    }

}
