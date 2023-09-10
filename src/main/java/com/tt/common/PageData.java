package com.tt.common;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.util.List;


/**
 * PageData 分页查询出参
 *
 * @param <T> T
 * @author Shuang Yu
 */
@Data
@NoArgsConstructor
@ApiModel(value = "分页结果", description = "分页结果")
public class PageData<T> {
    /**
     * 查询数据列表
     */
    protected List<T> list;
    /**
     * 总数
     */
    private long total = 0;
    /**
     * 每页显示条数，默认 10
     */
    private long size = 10;
    /**
     * 当前页
     */
    protected long current = 1;

    /**
     * of
     *
     * @param list    list
     * @param total   total
     * @param size    size
     * @param current current
     */
    public PageData(List<T> list, long total, long size, long current) {
        this.list = list;
        this.total = total;
        this.size = size;
        this.current = current;
    }

    /**
     * of
     *
     * @param <T>   T
     * @param list  list
     * @param total total
     * @return PageData<T>
     */
    public static <T> PageData<T> of(List<T> list, long total) {
        return new PageData<>(list, total, 0, 0);
    }

    /**
     * of
     *
     * @param <T>     T
     * @param list    list
     * @param total   total
     * @param size    size
     * @param current current
     * @return PageData<T>
     */
    public static <T> PageData<T> of(List<T> list, long total, long size, long current) {
        return new PageData<>(list, total, size, current);
    }

}