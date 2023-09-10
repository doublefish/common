package com.tt.common.config;

import com.baomidou.mybatisplus.core.injector.methods.DeleteBatchByIds;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import lombok.var;

/**
 * DeleteBatchByIds
 *
 * @author Shuang Yu
 */
public class LogicDeleteBatchByIds extends DeleteBatchByIds {

    public LogicDeleteBatchByIds() {
        super();
    }

    public LogicDeleteBatchByIds(String name) {
        super(name);
    }

    @Override
    protected String sqlLogicSet(TableInfo table) {
        var logicSet = super.sqlLogicSet(table);
        return logicSet + LogicDeleteMethod.sqlAuditSet(table);
    }
}
