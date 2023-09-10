package com.tt.common.sup8.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * AreaFeignVo
 *
 * @author Shuang Yu
 */
@Data
@ApiModel(value = "地区", description = "地区")
@JsonIgnoreProperties(ignoreUnknown = true)
public class AreaFeignVo {

    /**
     * 编码
     */
    @ApiModelProperty(value = "编码", required = true)
    @JsonProperty("areaId")
    private String areaId;
    /**
     * 名称
     */
    @ApiModelProperty(value = "名称", required = true)
    @JsonProperty("areaName")
    private String areaName;
    /**
     * 简称
     */
    @ApiModelProperty(value = "简称", required = true)
    @JsonProperty("shortName")
    private String shortName;
    /**
     * 父级Id
     */
    @ApiModelProperty(value = "父级Id", required = true)
    @JsonProperty("regionId")
    private String regionId;
    /**
     * 级别：0-国家，1-省，2-市，3-区县，4-街道
     */
    @ApiModelProperty(value = "级别", required = true)
    @JsonProperty("level")
    private Integer level;
    /**
     * 子集：无值，用于扩展
     */
    @ApiModelProperty(value = "子集")
    private List<AreaFeignVo> children;

}
