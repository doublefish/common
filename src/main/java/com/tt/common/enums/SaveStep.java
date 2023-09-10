package com.tt.common.enums;

import lombok.var;

/**
 * SaveStep
 *
 * @author Shuang Yu
 */
public enum SaveStep {
    /**
     * 未执行
     */
    NOT(0),
    /**
     * 保存前/已执行保存前方法：beforeSave
     */
    BEFORE(1),
    /**
     * 已保存/已执行保存方法：mapper.insert/mapper.updateById
     */
    SAVED(2),
    /**
     * 保存后/已执行保存后方法：afterSave
     */
    AFTER(3);

    private final int value;

    SaveStep(int value) {
        this.value = value;
    }

    /**
     * valueOf
     *
     * @param value the value of the SaveStep
     * @return the corresponding SaveStep enum
     * @throws IllegalArgumentException if value is not a valid SaveStep
     */
    public static SaveStep valueOf(int value) {
        for (var v : values()) {
            if (v.value == value) {
                return v;
            }
        }
        throw new IllegalArgumentException("Illegal SaveStep value: " + value);
    }

    /**
     * Returns value of this SaveStep
     *
     * @return value of this SaveStep
     */
    public int value() {
        return this.value;
    }
}
