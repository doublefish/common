package com.tt.common.model;

import com.tt.common.User;
import lombok.Data;

/**
 * 通用变量
 *
 * @author Shuang Yu
 */
@Data
public class UniversalVariable {
    /**
     * 语言代码
     */
    private String language;
    /**
     * 操作对象
     */
    private String enterpriseNo;
    /**
     * 登录用户
     */
    private User user;
}
