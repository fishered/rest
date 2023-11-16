package com.asset.rest.factory.req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

/**
 * @author fisher
 * @date 2023-10-16: 10:28
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RestParam {

    @NotBlank(message = "配置编码")
    private String code;

    /**
     * input json
     */
    private String input;

}
