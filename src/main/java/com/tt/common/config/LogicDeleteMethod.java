package com.tt.common.config;

import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.tt.common.Constants;
import com.tt.common.util.MyBatisPlusUtils;
import lombok.var;

import java.util.stream.Collectors;

/**
 * MyDeleteMethod
 *
 * @author Shuang Yu
 */
public class LogicDeleteMethod {


    /**
     * 拼接审计字段赋值
     *
     * @param tableInfo tableInfo
     * @return String
     */
    public static String sqlAuditSet(TableInfo tableInfo) {
        var sqlSet = new StringBuilder();
        if (tableInfo != null) {
            var fieldInfos = tableInfo.getFieldList().stream().filter((f) -> !f.isLogicDelete()).collect(Collectors.toList());
            for (var tableFieldInfo : fieldInfos) {
                var field = tableFieldInfo.getField();
                var column = tableFieldInfo.getColumn();
                switch (field.getName()) {
                    case Constants.Field.ROW_DELETED_DATE:
                        sqlSet.append(", ").append(column).append("=").append(MyBatisPlusUtils.VALUE_NOW);
                        break;
                    case Constants.Field.ROW_DELETED_USER:
                        sqlSet.append(", ").append(column).append("=").append(MyBatisPlusUtils.VALUE_UID);
                        break;
                }
            }
        }
        return sqlSet.toString();
    }

}
