package com.tt.common.sup8.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * ParameterOptionFeignVo
 *
 * @author Shuang Yu
 */
@Data
@ApiModel(value = "参数选项", description = "参数选项")
@JsonIgnoreProperties(ignoreUnknown = true)
public class ParameterOptionFeignVo {

    /**
     * 参数Id
     */
    @ApiModelProperty(value = "参数Id")
    private Long assParameterId;
    /**
     * 选项Id
     */
    @ApiModelProperty(value = "选项Id")
    private Long id;
    /**
     * 选项名称
     */
    @ApiModelProperty(value = "选项名称")
    private String name;
    /**
     * 选项值
     */
    @ApiModelProperty(value = "选项值")
    private String value;
    /**
     * 选项描述
     */
    @ApiModelProperty(value = "选项描述")
    private String description;
    /**
     * 是否默认值
     */
    @ApiModelProperty(value = "是否默认值")
    private Boolean shouldDefault;
    /**
     * 是否可自定义
     */
    @ApiModelProperty(value = "是否可自定义")
    private Boolean customized;
    /**
     * 是否启用：Disable,Enable
     */
    @ApiModelProperty(value = "是否启用：Disable,Enable")
    private String status;
    /**
     * 排序值
     */
    @ApiModelProperty(value = "排序值")
    private Integer sort;
    /**
     * 多语言生效企业号标记
     */
    @ApiModelProperty(value = "多语言生效企业号标记")
    private String langEnterpriseNo;
    /**
     * 配置值=>非自定义情况：非空等于选项值，则选中。自定义情况，非空就是选项值
     */
    @ApiModelProperty(value = "配置值=>非自定义情况：非空等于选项值，则选中。自定义情况，非空就是选项值")
    private String configValue;

}
