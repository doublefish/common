package com.tt.common.sup8.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Collection;

/**
 * LanguageQueryVo
 *
 * @author Shuang Yu
 */
@Data
@ApiModel(value = "多语言查询条件", description = "多语言查询条件")
@JsonIgnoreProperties(ignoreUnknown = true)
public class LanguageQueryVo {

    /**
     * 服务ID
     */
    @ApiModelProperty(value = "服务ID", required = true)
    @JsonProperty("serviceId")
    private String serviceId;

    /**
     * 多语言标识
     */
    @ApiModelProperty(value = "多语言标识", required = true)
    @JsonProperty("keys")
    private Collection<String> keys;

    /**
     * 构造函数
     *
     * @param serviceId 服务ID
     * @param keys      多语言标识
     */
    public LanguageQueryVo(String serviceId, Collection<String> keys) {
        this.serviceId = serviceId;
        this.keys = keys;
    }
}
