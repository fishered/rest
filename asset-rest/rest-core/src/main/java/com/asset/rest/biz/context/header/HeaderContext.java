package com.asset.rest.biz.context.header;

import com.asset.rest.biz.util.CollectionUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;


import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author fisher
 * @date 2023-09-13: 16:10
 */
@Data
public class HeaderContext {

//    /**
//     * Content-Type
//     */
//    private String ContentType;

    private List<HeaderParam> headerParams;

    @JsonIgnore
    public Map headerJson2Map(){
        if (CollectionUtils.isEmpty(headerParams)){
            return Collections.EMPTY_MAP;
        }
        return headerParams.stream()
                .collect(Collectors
                        .toMap(HeaderParam::getKey, HeaderParam::getValue));
    }

    @JsonIgnore
    public void isSupportThrowEx(){
        if (!CollectionUtils.isEmpty(headerParams)){
            headerParams.stream().forEach(e -> e.isSupportThrowEx());
        }
    }

}
