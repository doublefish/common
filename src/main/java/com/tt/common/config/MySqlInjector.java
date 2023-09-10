package com.tt.common.config;

import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.injector.DefaultSqlInjector;
import com.baomidou.mybatisplus.core.injector.methods.*;
import com.baomidou.mybatisplus.core.metadata.TableInfo;

import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

/**
 * MySqlInjector
 *
 * @author Shuang Yu
 */
public class MySqlInjector extends DefaultSqlInjector {

    private final boolean enableLogicDelete;

    public MySqlInjector(boolean enableLogicDelete) {
        this.enableLogicDelete = enableLogicDelete;
    }

    @Override
    public List<AbstractMethod> getMethodList(Class<?> mapperClass, TableInfo tableInfo) {
        if (enableLogicDelete) {
            Stream.Builder<AbstractMethod> builder = Stream.<AbstractMethod>builder()
                    .add(new Insert())
                    .add(new LogicDelete())//Delete
                    .add(new LogicDeleteByMap())//DeleteByMap
                    .add(new Update())
                    .add(new SelectByMap())
                    .add(new SelectCount())
                    .add(new SelectMaps())
                    .add(new SelectMapsPage())
                    .add(new SelectObjs())
                    .add(new SelectList())
                    .add(new SelectPage());
            if (tableInfo.havePK()) {
                builder.add(new LogicDeleteById())//DeleteById
                        .add(new LogicDeleteBatchByIds())//DeleteBatchByIds
                        .add(new UpdateById())
                        .add(new SelectById())
                        .add(new SelectBatchByIds());
            } else {
                logger.warn(String.format("%s ,Not found @TableId annotation, Cannot use Mybatis-Plus 'xxById' Method.",
                        tableInfo.getEntityType()));
            }
            return builder.build().collect(toList());
        } else {
            return super.getMethodList(mapperClass, tableInfo);
        }
    }
}
