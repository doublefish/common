package com.tt.common.sup8.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Map;

/**
 * HiveObjectFeignVo
 *
 * @author Shuang Yu
 */
@Data
@ApiModel(value = "文件", description = "文件")
@JsonIgnoreProperties(ignoreUnknown = true)
public class HiveObjectFeignVo {

    /**
     * 二进制字节
     */
    @ApiModelProperty(value = "二进制字节", required = true)
    @JsonProperty("binary")
    private byte[] binary;
    /**
     * 文件名、类型
     */
    @ApiModelProperty(value = "文件名、类型", required = true)
    @JsonProperty("meta")
    private Map<String, String> meta;
}
