package com.asset.rest.factory.req;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSONObject;
import com.asset.rest.biz.context.auth.AuthContext;
import com.asset.rest.biz.context.auth.AuthData;
import com.asset.rest.biz.context.handler.AfterHandlerContext;
import com.asset.rest.biz.context.handler.BeforeHandlerContext;
import com.asset.rest.biz.context.handler.HandlerContext;
import com.asset.rest.biz.context.header.HeaderContext;
import com.asset.rest.biz.context.header.HeaderParam;
import com.asset.rest.biz.context.param.ParamFix;
import com.asset.rest.biz.context.param.TemplateContext;
import com.asset.rest.biz.context.sign.SignContext;
import com.asset.rest.domain.RestInfo;
import com.asset.rest.enums.RestType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;

/**
 * @author fisher
 * @date 2023-09-26: 17:15
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(description = "配置请求模型")
public class RestInfoReq {

    /**
     * code作为唯一标识，不可重复
     */
    @ApiModelProperty(value = "配置编码", required = true)
    @NotBlank(message = "配置编码不能为空！")
    private String code;

    @ApiModelProperty(value = "分组id", required = true)
    @NotNull(message = "分组id不能为空！")
    private String groupId;

    @ApiModelProperty(value = "名称", required = true)
    @NotBlank(message = "名称不能为空！")
    private String name;

//    @NotBlank(message = "ip不能为空！")
    @ApiModelProperty(value = "ip")
    private String ip;

//    @NotBlank(message = "端口不能为空！")
    @ApiModelProperty(value = "端口")
    private String port;

//    @NotBlank(message = "后缀路径不能为空！")
    @ApiModelProperty(value = "后缀路径")
    private String suffixUrl;

//    @NotNull(message = "rest类型！")
    @ApiModelProperty(value = "rest类型", allowableValues = "GET, POST, PUT, DELETE, PATCH, HEAD, OPTIONS, TRACE")
    private RestType restType;

    /**
     * content Type
     */
    @ApiModelProperty(value = "contentType")
    private String contentType;

    /**
     * 是否是https 1是 0否
     */
    @ApiModelProperty(value = "是否是https 1是 0否", allowableValues = "0, 1")
    private Integer isHttps;

    /**
     * json string param
     * 请求参数有几种处理 固定值添加、指定key的固定值、映射key
     */
    @ApiModelProperty(value = "参数处理")
    private List<ParamFix> paramsFix;

    /**
     * template string
     */
    @ApiModelProperty(value = "模板列表")
    private List<TemplateContext> paramTemplate;

    /**
     * json string header
     */
    @ApiModelProperty(value = "请求头处理")
    private List<HeaderParam> headerParams;

    /**
     * json string auth
     */
    @ApiModelProperty(value = "认证处理")
    private List<AuthData> authData;

    private String customAuth;

    /**
     * json string sign
     */
    @ApiModelProperty(value = "签名处理")
    private SignContext sign;

    /**
     * json string handler process params
     */
    @ApiModelProperty(value = "前置处理")
    private List<HandlerContext> beforeHandler;

    @ApiModelProperty(value = "后置处理")
    private List<HandlerContext> afterHandler;

    @ApiModelProperty(value = "描述")
    private String description;

    @JsonIgnore
    public RestInfo convertDomain(){
        RestInfo restInfo = new RestInfo();
        BeanUtil.copyProperties(this, restInfo);

        if (!CollectionUtils.isEmpty(paramsFix)){
            restInfo.setParamsFix(JSONObject.toJSONString(paramsFix));
        }
        if (!CollectionUtils.isEmpty(paramTemplate)){
            restInfo.setParamTemplate(JSONObject.toJSONString(paramTemplate));
        }
        if (!CollectionUtils.isEmpty(headerParams)){
            HeaderContext headerContext = new HeaderContext();
            headerContext.setHeaderParams(headerParams);
            restInfo.setHeader(JSONObject.toJSONString(headerContext));
        }
        if (!CollectionUtils.isEmpty(authData) || StringUtils.isNotBlank(customAuth)){
            AuthContext authContext = new AuthContext();
            authContext.setAuthDataList(authData);
            authContext.setCustom(customAuth);
            restInfo.setAuth(JSONObject.toJSONString(authContext));
        }
        if (Objects.nonNull(sign)){
            restInfo.setSign(JSONObject.toJSONString(sign));
        }
        if (!CollectionUtils.isEmpty(beforeHandler)){
            BeforeHandlerContext beforeHandlerContext = new BeforeHandlerContext();
            beforeHandlerContext.setHandlerContexts(beforeHandler);
            restInfo.setBeforeHandler(JSONObject.toJSONString(beforeHandlerContext));
        }
        if (!CollectionUtils.isEmpty(afterHandler)){
            AfterHandlerContext afterHandlerContext = new AfterHandlerContext();
            afterHandlerContext.setHandlerContexts(afterHandler);
            restInfo.setAfterHandler(JSONObject.toJSONString(afterHandlerContext));
        }
        return restInfo;
    }


}
