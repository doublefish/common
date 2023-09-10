package com.tt.common.sup8.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.var;
import org.apache.commons.lang3.StringUtils;

/**
 * AreaDetailFeignVo
 *
 * @author Shuang Yu
 */
@Data
@ApiModel(value = "地区详细信息", description = "地区详细信息")
@JsonIgnoreProperties(ignoreUnknown = true)
public class AreaDetailFeignVo {

    /**
     * 国家编码
     */
    @ApiModelProperty(value = "国家编码")
    @JsonProperty("countryCode")
    private String countryCode;
    /**
     * 国家
     */
    @ApiModelProperty(value = "国家")
    @JsonProperty("country")
    private String country;
    /**
     * 省编码
     */
    @ApiModelProperty(value = "省编码")
    @JsonProperty("provinceCode")
    private String provinceCode;
    /**
     * 省
     */
    @ApiModelProperty(value = "省")
    @JsonProperty("province")
    private String province;
    /**
     * 市编码
     */
    @ApiModelProperty(value = "市编码")
    @JsonProperty("cityCode")
    private String cityCode;
    /**
     * 市
     */
    @ApiModelProperty(value = "市")
    @JsonProperty("city")
    private String city;
    /**
     * 区编码
     */
    @ApiModelProperty(value = "区编码")
    @JsonProperty("areaCode")
    private String areaCode;
    /**
     * 区
     */
    @ApiModelProperty(value = "区")
    @JsonProperty("area")
    private String area;
    /**
     * 街道编码
     */
    @ApiModelProperty(value = "街道编码")
    @JsonProperty("streetCode")
    private String streetCode;
    /**
     * 街道
     */
    @ApiModelProperty(value = "街道")
    @JsonProperty("street")
    private String street;

    /**
     * 全名
     */
    @ApiModelProperty(value = "全名")
    private String fullName;
    /**
     * 最小级别编码
     */
    @ApiModelProperty(value = "最小级别编码", hidden = true)
    private String minCode;

    /**
     * 获取全名
     *
     * @return fullName
     */
    public String getFullName() {
        if (this.fullName == null) {
            var builder = new StringBuilder();
            if (!StringUtils.isEmpty(getProvince())) {
                builder.append("/").append(getProvince());
                if (!StringUtils.isEmpty(getCity())) {
                    builder.append("/").append(getCity());
                    if (!StringUtils.isEmpty(getArea())) {
                        builder.append("/").append(getArea());
                        if (!StringUtils.isEmpty(getStreet())) {
                            builder.append("/").append(getStreet());
                        }
                    }
                }
            }
            this.fullName = builder.length() > 0 ? builder.substring(1) : "";
        }
        return this.fullName;
    }

    /**
     * 获取最小级编码
     *
     * @return minName
     */
    public String getMinCode() {
        if (this.minCode == null) {
            if (!StringUtils.isEmpty(this.streetCode)) {
                this.minCode = this.streetCode;
            } else if (!StringUtils.isEmpty(this.areaCode)) {
                this.minCode = this.areaCode;
            } else if (!StringUtils.isEmpty(this.cityCode)) {
                this.minCode = this.cityCode;
            } else if (!StringUtils.isEmpty(this.provinceCode)) {
                this.minCode = this.provinceCode;
            } else {
                this.minCode = this.countryCode;
            }
        }
        return this.minCode;
    }

}
