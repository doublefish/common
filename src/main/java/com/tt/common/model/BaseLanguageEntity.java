package com.tt.common.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.tt.common.annotation.TableColumn;
import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * 多语言基类
 *
 * @author admin
 */
@Data
@EqualsAndHashCode(callSuper = true)
public abstract class BaseLanguageEntity extends BaseEntity {

    /**
     * 语言代码
     */
    @TableField(value = "language")
    @TableColumn(value = "language")
    public String language;

    /**
     * 主表Id
     */
    @TableField(exist = false)
    @TableColumn(exist = false)
    public Long mainId;

    /**
     * setMainId
     *
     * @param mainId mainId
     */
    public abstract void setMainId(Long mainId);

    /**
     * getMainId
     *
     * @return mainId
     */
    public abstract Long getMainId();
}
