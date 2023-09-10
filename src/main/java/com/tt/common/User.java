package com.tt.common;

import lombok.Data;


/**
 * User
 *
 * @author Shuang Yu
 */
@Data
public class User {

    /**
     * 用户Id
     */
    private String id;

    /**
     * 用户名
     */
    private String name;

    /**
     * 企业号
     */
    private String enterpriseNo;

    /**
     * 企业类型
     */
    private String enterpriseType;

}
