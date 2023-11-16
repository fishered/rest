package com.asset.rest.factory.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotBlank;

/**
 * @author fisher
 * @date 2023-09-26: 14:31
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@ApiModel(description = "分组请求模型")
public class GroupReq {

    @NotBlank(message = "设置分组名称不能为空！")
    @ApiModelProperty(value = "分组名称", required = true)
    private String name;

    @ApiModelProperty(value = "描述")
    private String description;

}
