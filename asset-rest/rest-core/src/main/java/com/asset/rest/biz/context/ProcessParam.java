package com.asset.rest.biz.context;

import com.asset.rest.biz.util.CollectionUtils;
import com.asset.rest.enums.RestType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;


import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author fisher
 * @date 2023-09-14: 10:03
 * 处理过后的模型
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ProcessParam {

    private String url;

    private RestType restType;

    private Map<String, Object> headers;

    private Map<String, Object> params;

    /**
     * is https
     */
    private Boolean isHttps;

    @JsonIgnore
    public synchronized void putHeaders(String key, Object value){
        if (CollectionUtils.isEmpty(headers)){
            headers = new LinkedHashMap<>();
        }
        if (StringUtils.isNotBlank(key) && value != null){
            headers.put(key, value);
            return;
        }
        throw new IllegalArgumentException("put header data: key or value is null!");
    }

    @JsonIgnore
    public synchronized void putParams(String key, Object value){
        if (CollectionUtils.isEmpty(params)){
            params = new LinkedHashMap<>();
        }
        if (StringUtils.isNotBlank(key) && value != null){
            params.put(key, value);
            return;
        }
        throw new IllegalArgumentException("put params data: key or value is null!");
    }

    @JsonIgnore
    public synchronized void removeHeaders(String key){
        if (CollectionUtils.isEmpty(headers)){
            return;
        }
        if (StringUtils.isNotBlank(key)){
            headers.remove(key);
            return;
        }



    }

    @JsonIgnore
    public boolean isHttps(){
        if (isHttps == null){
            return false;
        }
        return isHttps;
    }

}
