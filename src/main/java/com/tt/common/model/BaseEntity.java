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
 * BaseEntity
 *
 * @author Shuang Yu
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableAlias
public class BaseEntity extends Entity<Long> {

    /**
     * 主键ID
     */
    @Id
    @Field(type = FieldType.Long)
    @TableId(value = "id", type = IdType.AUTO)
    @TableColumn(value = "id", id = true)
    private Long id;

}
