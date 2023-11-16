package com.asset.rest.factory.req;

import com.asset.rest.enums.RestType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author fisher
 * @date 2023-09-27: 13:40
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(description = "配置列表查询模型")
public class RestInfoQueryReq {

    @ApiModelProperty(value = "配置编码")
    private String code;

    @ApiModelProperty(value = "配置名称")
    private String name;

    @ApiModelProperty(value = "请求类型")
    private RestType restType;

}
