package com.asset.rest.factory.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotNull;

/**
 * @author fisher
 * @date 2023-09-26: 14:37
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@ApiModel(description = "分组更新模型")
public class GroupUpdateReq extends GroupReq{

    @NotNull(message = "修改分组指定id不能为空！")
    @ApiModelProperty(value = "分组id", required = true)
    private String id;

}
