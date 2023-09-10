package com.tt.common.feign;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * ResultData
 *
 * @author Shuang Yu
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "Feign响应结果", description = "Feign响应结果")
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResultData<T> extends com.tt.common.ResultData<T> implements Serializable {

    /**
     * 请求ID
     */
    @ApiModelProperty(value = "请求ID")
    private String requestId;

    /**
     * 构造函数
     */
    public ResultData() {
        super();
    }
}
