package com.tt.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 导出参数
 *
 * @author Shuang Yu
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExportParameter<T> {
    /**
     * 查询条件
     */
    private T query;
    /**
     * 列头
     */
    private Map<String, String> headers;
    /**
     * 文件名
     */
    private String fileName;

    public <E extends ExportParameter<T>> ExportParameter(E parameter) {
        this.query = parameter.getQuery();
        this.headers = parameter.getHeaders();
        this.fileName = parameter.getFileName();
    }
}
