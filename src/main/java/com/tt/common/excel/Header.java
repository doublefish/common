package com.tt.common.excel;

import lombok.Data;

/**
 * Header
 *
 * @author Shuang Yu
 */
@Data
public class Header {
    /**
     * 编码
     */
    private String code;
    /**
     * 名称
     */
    private String name;
    /**
     * 特别的
     */
    private Boolean special;
}
