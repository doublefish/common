package com.tt.common.test;

import lombok.Data;
import lombok.EqualsAndHashCode;
import com.tt.common.model.BaseEntity;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * TestModel
 *
 * @author Shuang.Yu
 */
@Data
@EqualsAndHashCode(callSuper = true)
class TestModel extends BaseEntity {

    @Field(type = FieldType.Keyword)
    private String code;

    private String name;

    private LocalDateTime  localDateTime = LocalDateTime.now();

    private Date date = Date.from(Instant.now());

    private Instant instant = Instant.now();


}
