package com.tt.common.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.Instant;

/**
 * ViewObject
 *
 * @author Shuang Yu
 */
@Data
public class Vo<T> {

    @ApiModelProperty(value = "Id", required = true)
    private T id;

    @ApiModelProperty(value = "版本号", required = true)
    private Integer rowVersion;

    @ApiModelProperty(value = "创建时间", required = true)
    private Instant rowCreateDate;

    @ApiModelProperty(value = "创建用户", required = true)
    private String rowCreateUser;

    @ApiModelProperty(value = "创建用户名", required = true)
    private String rowCreateUsername;

    @ApiModelProperty(value = "修改时间", required = true)
    private Instant rowUpdateDate;

    @ApiModelProperty(value = "修改用户", required = true)
    private String rowUpdateUser;

    @ApiModelProperty(value = "修改用户名", required = true)
    private String rowUpdateUsername;

    @ApiModelProperty(value = "删除标识", required = true, hidden = true)
    private Boolean rowDeleted;

    @ApiModelProperty(value = "删除时间", required = true, hidden = true)
    private Instant rowDeletedDate;

    @ApiModelProperty(value = "删除用户", required = true, hidden = true)
    private String rowDeletedUser;
}