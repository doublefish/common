package com.tt.common.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.tt.common.RequestDataHelper;
import lombok.var;
import org.apache.ibatis.reflection.MetaObject;

import java.time.Instant;

/**
 * 自动填充
 *
 * @author Shuang Yu
 */
public class AutofillHandler implements MetaObjectHandler {

    private final String appName;

    public AutofillHandler(String appName) {
        this.appName = appName;
    }

    @Override
    public void insertFill(MetaObject metaObject) {
        fill(metaObject, 1);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        fill(metaObject, 2);
    }

    protected void fill(MetaObject metaObject, int type) {
        var now = Instant.now();
        var uid = "";
        var uname = "";
        var user = RequestDataHelper.getUser();
        if (user != null) {
            uid = user.getId();
            uname = user.getName();
        } else {
            uid = this.appName;
            uname = this.appName;
        }

        /*注释
        if (type == 1) {
            this.strictInsertFill(metaObject, "rowCreateDate", Instant.class, now);
            this.strictInsertFill(metaObject, "rowCreateUser", String.class, uid);
            this.strictInsertFill(metaObject, "rowCreateUsername", String.class, uname);
            this.strictInsertFill(metaObject, "rowUpdateDate", Instant.class, now);
            this.strictInsertFill(metaObject, "rowUpdateUser", String.class, uid);
            this.strictInsertFill(metaObject, "rowUpdateUsername", String.class, uname);
        } else {
            this.strictUpdateFill(metaObject, "rowUpdateDate", Instant.class, now);
            this.strictUpdateFill(metaObject, "rowUpdateUser", String.class, uid);
            this.strictUpdateFill(metaObject, "rowUpdateUsername", String.class, uname);
        }*/
        if (type == 1) {
            metaObject.setValue("rowCreateDate", now);
            metaObject.setValue("rowCreateUser", uid);
            metaObject.setValue("rowCreateUsername", uname);
        }
        metaObject.setValue("rowUpdateDate", now);
        metaObject.setValue("rowUpdateUser", uid);
        metaObject.setValue("rowUpdateUsername", uname);
    }
}
