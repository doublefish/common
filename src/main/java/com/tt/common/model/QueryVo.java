package com.tt.common.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.Instant;
import java.util.List;

/**
 * 查询条件
 *
 * @author Shuang Yu
 */
@Data
public class QueryVo<T> {

    @ApiModelProperty(value = "指定的Id")
    private List<T> ids;

    @ApiModelProperty(value = "排除的Id")
    private List<T> excludeIds;

    @ApiModelProperty(value = "创建用户")
    private String rowCreateUser;

    @ApiModelProperty(value = "创建用户名（模糊）")
    private String rowCreateUsername;

    @ApiModelProperty(value = "创建时间.开始时间，格式：2022-01-01T00:00:00.000Z")
    private Instant rowCreateDateFrom;

    @ApiModelProperty(value = "创建时间.结束时间，格式：2022-01-31T23:59:59.999Z")
    private Instant rowCreateDateTo;

    @ApiModelProperty(value = "修改时间.开始时间，格式：2022-01-01T00:00:00.000Z")
    private Instant rowUpdateDateFrom;

    @ApiModelProperty(value = "修改时间.结束时间，格式：2022-01-31T23:59:59.999Z")
    private Instant rowUpdateDateTo;

    @ApiModelProperty(value = "包括已删除的")
    private Boolean includeDeleted = false;

    @ApiModelProperty(value = "关联数据：所有非主表自带的属性的名称")
    private List<String> properties;

    @ApiModelProperty(value = "数据深度（关联数据）")
    private Integer dataDepth;

    @ApiModelProperty(value = "当前页")
    private Integer current = 1;

    @ApiModelProperty(value = "每页大小：最大10000")
    private Integer size;

    @ApiModelProperty(value = "排序字段")
    private String sortName = "id";

    @ApiModelProperty(value = "排序类型：asc/desc")
    private String sortType = "desc";

    @ApiModelProperty("滚动查询Id：正序传上次查询结果中最大的Id；倒序传上次查询结果中最小的Id。暂时只支持按照id排序")
    private T scrollId;

}

