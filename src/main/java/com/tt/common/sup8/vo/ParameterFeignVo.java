package com.tt.common.sup8.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * ParameterFeignVo
 *
 * @author Shuang Yu
 */
@Data
@ApiModel(value = "参数", description = "参数")
@JsonIgnoreProperties(ignoreUnknown = true)
public class ParameterFeignVo {

    /**
     * 编码
     */
    @ApiModelProperty(value = "编码")
    @JsonProperty("code")
    private String code;

    /**
     * 值
     */
    @ApiModelProperty(value = "值")
    @JsonProperty("value")
    private String[] value;

}
