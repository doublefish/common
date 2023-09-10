
package com.tt.common.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * 查询参数
 *
 * @author Shuang Yu
 */
@Data
@NoArgsConstructor
public class QueryDto<T> {
    /**
     * 指定的Id
     */
    private List<T> ids;
    /**
     * 排除的Id
     */
    private List<T> excludeIds;
    /**
     * 创建用户
     */
    private String rowCreateUser;
    /**
     * 创建用户名（模糊）
     */
    private String rowCreateUsername;
    /**
     * 创建时间
     */
    private Instant rowCreateDateFrom;
    /**
     * 创建时间
     */
    private Instant rowCreateDateTo;
    /**
     * 修改时间
     */
    private Instant rowUpdateDateFrom;
    /**
     * 修改时间
     */
    private Instant rowUpdateDateTo;
    /**
     * 是否获取已删除的
     */
    private Boolean includeDeleted = false;
    /**
     * 关联数据
     */
    private List<String> properties;
    /**
     * 数据深度（关联数据）
     */
    private Integer dataDepth;
    /**
     * 当前页
     */
    private Integer current = 1;
    /**
     * 每页大小
     */
    private Integer size = 10;
    /**
     * 排序字段
     */
    private String sortName = "id";
    /**
     * 排序类型：asc/desc
     */
    private String sortType = "desc";
    /**
     * 多个排序条件：{name:true,id:false}，默认倒序，用LinkedHashMap赋值
     */
    private Map<String, Boolean> sorts;
    /**
     * 滚动查询Id
     * 正序传上次查询结果中最大的Id
     * 倒序传上次查询结果中最小的Id
     */
    private T scrollId;

}
