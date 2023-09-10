package com.tt.common.test;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tt.common.annotation.TableColumn;
import com.tt.common.model.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.Instant;
import java.util.List;

/**
 * 组织
 *
 * @author Shuang Yu
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@EqualsAndHashCode(callSuper = true)
@Document(indexName = "sup8-org", createIndex = false)
@TableName(value = "tb_org_151666005646")
public class OrgEntity extends BaseEntity {

    /**
     * 编码
     */
    @Field(type = FieldType.Keyword)
    @TableField(value = "code")
    @TableColumn(value = "code")
    private String code;
    /**
     * 名称
     */
    @Field(type = FieldType.Keyword)
    @TableField(value = "name")
    @TableColumn(value = "name")
    private String name;
    /**
     * 类型Id
     */
    @Field(type = FieldType.Long)
    @TableField(value = "type_id")
    @TableColumn(value = "type_id")
    private Long typeId;
    /**
     * 父级Id
     */
    @Field(type = FieldType.Long)
    @TableField(exist = false)
    @TableColumn(exist = false)
    private List<Long> parentIds;
    /**
     * 标签Id：分类Id/经销商级别Id/门店分组Id
     */
    @Field(type = FieldType.Long)
    @TableField(value = "tag_id")
    @TableColumn(value = "tag_id")
    private Long tagId;
    /**
     * 区域代码
     */
    @Field(type = FieldType.Keyword)
    @TableField(value = "district_code")
    @TableColumn(value = "district_code")
    private String districtCode;
    /**
     * 地址
     */
    @Field(type = FieldType.Text, index = false)
    @TableField(value = "address")
    @TableColumn(value = "address")
    private String address;
    /**
     * 负责人Id
     */
    @Field(type = FieldType.Long)
    @TableField(value = "manager_id")
    @TableColumn(value = "manager_id")
    private Long managerId;
    /**
     * 业务员Id
     */
    @Field(type = FieldType.Long)
    @TableField(value = "salesman_id")
    @TableColumn(value = "salesman_id")
    private Long salesmanId;
    /**
     * 员工上限
     */
    @Field(type = FieldType.Integer)
    @TableField(value = "max_employees")
    @TableColumn(value = "max_employees")
    private Integer maxEmployees;
    /**
     * 证照路径
     */
    @Field(type = FieldType.Text, index = false)
    @TableField(value = "license_path")
    @TableColumn(value = "license_path")
    private String licensePath;
    /**
     * 备注
     */
    @Field(type = FieldType.Text, index = false)
    @TableField(value = "remark")
    @TableColumn(value = "remark")
    private String remark;
    /**
     * 激活状态：1-已激活，2-未激活
     */
    @Field(type = FieldType.Integer)
    @TableField(value = "activation_status")
    @TableColumn(value = "activation_status")
    private Integer activationStatus;
    /**
     * 激活状态名称
     */
    @TableField(exist = false)
    @TableColumn(exist = false)
    private String activationStatusName;
    /**
     * 激活时间
     */
    @Field(type = FieldType.Date)
    @TableField(value = "activation_date")
    @TableColumn(value = "activation_date")
    private Instant activationDate;
    /**
     * 状态：1-启用，2-禁用
     */
    @Field(type = FieldType.Integer)
    @TableField(value = "status")
    @TableColumn(value = "status")
    private Integer status;
    /**
     * e0：仓库类型/大区级别/经销商级别/门店类型/供应商类型
     */
    @Field(type = FieldType.Keyword)
    @TableField(value = "e0")
    @TableColumn(value = "e0")
    private String e0;
    /**
     * e1：大区覆盖区域代码/经销商销售区域代码/门店销售区域代码
     */
    @Field(type = FieldType.Keyword)
    @TableField(value = "e1")
    @TableColumn(value = "e1")
    private String e1;
    /**
     * e2：门店图片/供应商生产许可证路径
     */
    @Field(type = FieldType.Keyword)
    @TableField(value = "e2")
    @TableColumn(value = "e2")
    private String e2;
    /**
     * e3：经销商审核备注/门店审核备注
     */
    @Field(type = FieldType.Keyword)
    @TableField(value = "e3")
    @TableColumn(value = "e3")
    private String e3;
    /**
     * e4：产线序号，工厂和车间全部00
     */
    @Field(type = FieldType.Keyword)
    @TableField(value = "e4")
    @TableColumn(value = "e4")
    private String e4;
    /**
     * e5
     */
    @Field(type = FieldType.Keyword)
    @TableField(value = "e5")
    @TableColumn(value = "e5")
    private String e5;
    /**
     * e6
     */
    @Field(type = FieldType.Keyword)
    @TableField(value = "e6")
    @TableColumn(value = "e6")
    private String e6;
    /**
     * e7
     */
    @Field(type = FieldType.Keyword)
    @TableField(value = "e7")
    @TableColumn(value = "e7")
    private String e7;
    /**
     * e8
     */
    @Field(type = FieldType.Keyword)
    @TableField(value = "e8")
    @TableColumn(value = "e8")
    private String e8;
    /**
     * e9：是否顶级：与所有父级的类型的分类都不同
     */
    @Field(type = FieldType.Keyword)
    @TableField(value = "e9")
    @TableColumn(value = "e9")
    private String e9;

    /**
     * f0
     */
    @Field(type = FieldType.Keyword)
    @TableField(value = "f0")
    @TableColumn(value = "f0")
    private String f0;
    /**
     * f1
     */
    @Field(type = FieldType.Keyword)
    @TableField(value = "f1")
    @TableColumn(value = "f1")
    private String f1;
    /**
     * f2
     */
    @Field(type = FieldType.Keyword)
    @TableField(value = "f2")
    @TableColumn(value = "f2")
    private String f2;
    /**
     * f3
     */
    @Field(type = FieldType.Keyword)
    @TableField(value = "f3")
    @TableColumn(value = "f3")
    private String f3;
    /**
     * f4
     */
    @Field(type = FieldType.Keyword)
    @TableField(value = "f4")
    @TableColumn(value = "f4")
    private String f4;
    /**
     * f5
     */
    @Field(type = FieldType.Keyword)
    @TableField(value = "f5")
    @TableColumn(value = "f5")
    private String f5;
    /**
     * f6
     */
    @Field(type = FieldType.Keyword)
    @TableField(value = "f6")
    @TableColumn(value = "f6")
    private String f6;
    /**
     * f7
     */
    @Field(type = FieldType.Keyword)
    @TableField(value = "f7")
    @TableColumn(value = "f7")
    private String f7;
    /**
     * f8
     */
    @Field(type = FieldType.Keyword)
    @TableField(value = "f8")
    @TableColumn(value = "f8")
    private String f8;
    /**
     * f9
     */
    @Field(type = FieldType.Keyword)
    @TableField(value = "f9")
    @TableColumn(value = "f9")
    private String f9;
}
