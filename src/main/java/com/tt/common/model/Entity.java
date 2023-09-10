package com.tt.common.model;

import com.baomidou.mybatisplus.annotation.*;
import com.tt.common.annotation.TableAlias;
import com.tt.common.annotation.TableColumn;
import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;
import java.time.Instant;

/**
 * 实体
 *
 * @author Shuang Yu
 */
@Data
@TableAlias
public class Entity<T> implements Serializable {

    /**
     * 版本号
     */
    @Field(type = FieldType.Integer)
    @Version
    @TableField(value = "row_version")
    @TableColumn(value = "row_version", version = true)
    private Integer rowVersion;

    /**
     * 创建时间
     */
    @Field(type = FieldType.Date)
    @TableField(value = "row_create_date", fill = FieldFill.INSERT)
    @TableColumn(value = "row_create_date")
    private Instant rowCreateDate;

    /**
     * 创建用户
     */
    @Field(type = FieldType.Keyword)
    @TableField(value = "row_create_user", fill = FieldFill.INSERT)
    @TableColumn(value = "row_create_user")
    private String rowCreateUser;

    /**
     * 创建用户名
     */
    @Field(type = FieldType.Keyword)
    @TableField(value = "row_create_username", fill = FieldFill.INSERT)
    @TableColumn(value = "row_create_username")
    private String rowCreateUsername;

    /**
     * 修改时间
     */
    @Field(type = FieldType.Date)
    @TableField(value = "row_update_date", fill = FieldFill.INSERT_UPDATE)
    @TableColumn(value = "row_update_date")
    private Instant rowUpdateDate;

    /**
     * 修改用户
     */
    @Field(type = FieldType.Keyword)
    @TableField(value = "row_update_user", fill = FieldFill.INSERT_UPDATE)
    @TableColumn(value = "row_update_user")
    private String rowUpdateUser;

    /**
     * 修改用户名
     */
    @Field(type = FieldType.Keyword)
    @TableField(value = "row_update_username", fill = FieldFill.INSERT_UPDATE)
    @TableColumn(value = "row_update_username")
    private String rowUpdateUsername;

    /**
     * 删除标识
     */
    @Field(type = FieldType.Boolean)
    @TableLogic
    @TableField(value = "row_deleted")
    @TableColumn(value = "row_deleted", logic = true)
    private Boolean rowDeleted;

    /**
     * 删除时间
     */
    @Field(type = FieldType.Date)
    @TableField(value = "row_deleted_date")
    @TableColumn(value = "row_deleted_date")
    private Instant rowDeletedDate;

    /**
     * 删除用户
     */
    @Field(type = FieldType.Keyword)
    @TableField(value = "row_deleted_user")
    @TableColumn(value = "row_deleted_user")
    private String rowDeletedUser;

}
