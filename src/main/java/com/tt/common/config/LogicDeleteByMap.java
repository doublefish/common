package com.tt.common.config;

import com.baomidou.mybatisplus.core.injector.methods.DeleteByMap;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import lombok.var;

/**
 * DeleteByMap
 *
 * @author Shuang Yu
 */
public class LogicDeleteByMap extends DeleteByMap {

    public LogicDeleteByMap() {
        super();
    }

    public LogicDeleteByMap(String name) {
        super(name);
    }

    @Override
    protected String sqlLogicSet(TableInfo table) {
        var logicSet = super.sqlLogicSet(table);
        return logicSet + LogicDeleteMethod.sqlAuditSet(table);
    }
}
