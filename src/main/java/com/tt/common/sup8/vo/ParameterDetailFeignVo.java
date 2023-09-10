package com.tt.common.sup8.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * ParameterDetailFeignVo
 *
 * @author Shuang Yu
 */
@Data
@ApiModel(value = "参数", description = "参数")
@JsonIgnoreProperties(ignoreUnknown = true)
public class ParameterDetailFeignVo {

    /**
     * Id
     */
    @ApiModelProperty(value = "Id")
    @JsonProperty("id")
    private Long id;
    /**
     * 编码
     */
    @ApiModelProperty(value = "编码")
    @JsonProperty("code")
    private String code;
    /**
     * 名称
     */
    @ApiModelProperty(value = "名称")
    @JsonProperty("name")
    private String name;
    /**
     * 配置值：用于计算选项值是否发生变动
     */
    @ApiModelProperty(value = "配置值：用于计算选项值是否发生变动")
    @JsonProperty("configValue")
    private String configValue;
    /**
     * 描述
     */
    @ApiModelProperty(value = "描述")
    @JsonProperty("description")
    private String description;
    /**
     * 参数选项类型：Checkbox,Radio
     */
    @ApiModelProperty(value = "参数选项类型：Checkbox,Radio")
    @JsonProperty("optionType")
    private String optionType;
    /**
     * 选项
     */
    @ApiModelProperty(value = "选项")
    @JsonProperty("options")
    private ParameterOptionFeignVo[] options;

}
