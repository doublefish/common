package com.tt.common.sup8.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.HashMap;

/**
 * LanguageFeignVo
 *
 * @author Shuang Yu
 */
@Data
@ApiModel(value = "多语言", description = "多语言")
@JsonIgnoreProperties(ignoreUnknown = true)
public class LanguageFeignVo {

    /**
     * 服务ID
     */
    @ApiModelProperty(value = "服务ID")
    @JsonProperty("serviceId")
    private String serviceId;

    /**
     * 数据：k=多语言标识，v=多语言内容
     */
    @ApiModelProperty("数据：k=多语言标识，v=多语言内容")
    @JsonProperty("data")
    private HashMap<String, String> data;
}
