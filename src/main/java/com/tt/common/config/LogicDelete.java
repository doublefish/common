package com.tt.common.config;

import com.baomidou.mybatisplus.core.injector.methods.Delete;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import lombok.var;

/**
 * Delete
 *
 * @author Shuang Yu
 */
public class LogicDelete extends Delete {

    public LogicDelete() {
        super();
    }

    public LogicDelete(String name) {
        super(name);
    }

    @Override
    protected String sqlLogicSet(TableInfo table) {
        var logicSet = super.sqlLogicSet(table);
        return logicSet + LogicDeleteMethod.sqlAuditSet(table);
    }

}
