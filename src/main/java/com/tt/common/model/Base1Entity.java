package com.tt.common.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.tt.common.annotation.TableAlias;
import com.tt.common.annotation.TableColumn;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * Base2Entity
 *
 * @author Shuang Yu
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableAlias
public class Base1Entity extends Entity<String> {

    /**
     * 主键ID
     */
    @Id
    @Field(type = FieldType.Keyword)
    @TableId(value = "id", type = IdType.AUTO)
    @TableColumn(value = "id", id = true)
    private String id;

}
