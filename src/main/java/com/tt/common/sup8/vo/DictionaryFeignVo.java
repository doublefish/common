package com.tt.common.sup8.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * DictionaryFeignVo
 *
 * @author Shuang Yu
 */
@Data
@ApiModel(value = "字典", description = "字典")
@JsonIgnoreProperties(ignoreUnknown = true)
public class DictionaryFeignVo {

    /**
     * Id
     */
    @ApiModelProperty(value = "Id")
    @JsonProperty("id")
    private Long id;
    /**
     * 选项值
     */
    @ApiModelProperty(value = "选项值")
    @JsonProperty("itemValue")
    private String itemValue;
    /**
     * 选项名称
     */
    @ApiModelProperty(value = "选项名称")
    @JsonProperty("itemName")
    private String itemName;
    /**
     * 选项描述
     */
    @ApiModelProperty(value = "选项描述")
    @JsonProperty("itemDescription")
    private String itemDescription;
    /**
     * 是否默认值
     */
    @ApiModelProperty(value = "编码")
    private Boolean isDefault = false;

}
