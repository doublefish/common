package com.tt.common.component;


import com.tt.common.RequestDataHelper;
import com.tt.common.ResultData;
import com.tt.common.util.ValidateUtils;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.Callable;
import java.util.function.*;

/**
 * EmptyController
 *
 * @author Shuang Yu
 */
@Slf4j
public class EmptyController {

    public EmptyController() {
    }

    /**
     * setEnterpriseNo
     *
     * @param enterpriseNo enterpriseNo
     */
    protected void setEnterpriseNo(String enterpriseNo) {
        if (StringUtils.isEmpty(enterpriseNo) || !ValidateUtils.isOnlyNumbersAndLetters(enterpriseNo)) {
            throw new RuntimeException("无效的企业号");
        }
        RequestDataHelper.setEnterpriseNo(enterpriseNo);
    }

    protected void beforeExecute() {
        // 子类根据需要重写此方法
    }

    protected void afterExecuted() {
        // 子类根据需要重写此方法
        RequestDataHelper.remove();
    }

    protected <R> ResultData<R> success(R result) {
        return ResultData.success(result);
    }

    protected void error(Exception e) {
    }

    protected <R> ResultData<R> run(Runnable runnable) {
        try {
            beforeExecute();
            runnable.run();
            return success(null);
        } catch (Exception e) {
            error(e);
            throw e;
        } finally {
            afterExecuted();
        }
    }

    protected <R> ResultData<R> call(Callable<R> callable) {
        try {
            beforeExecute();
            var res = callable.call();
            return success(res);
        } catch (Exception e) {
            error(e);
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            afterExecuted();
        }
    }

    protected <R> ResultData<R> get(Supplier<R> supplier) {
        try {
            beforeExecute();
            var res = supplier.get();
            return success(res);
        } catch (Exception e) {
            error(e);
            throw e;
        } finally {
            afterExecuted();
        }
    }

    protected <T, R> ResultData<R> accept(Consumer<T> consumer, T t) {
        try {
            beforeExecute();
            consumer.accept(t);
            return success(null);
        } catch (Exception e) {
            error(e);
            throw e;
        } finally {
            afterExecuted();
        }
    }

    protected <T, U, R> ResultData<R> accept(BiConsumer<T, U> consumer, T t, U u) {
        try {
            beforeExecute();
            consumer.accept(t, u);
            return success(null);
        } catch (Exception e) {
            error(e);
            throw e;
        } finally {
            afterExecuted();
        }
    }

    protected <T, R> ResultData<R> apply(Function<T, R> function, T t) {
        try {
            beforeExecute();
            var res = function.apply(t);
            return success(res);
        } catch (Exception e) {
            error(e);
            throw e;
        } finally {
            afterExecuted();
        }
    }

    protected <T, U, R> ResultData<R> apply(BiFunction<T, U, R> function, T t, U u) {
        try {
            beforeExecute();
            var res = function.apply(t, u);
            return success(res);
        } catch (Exception e) {
            error(e);
            throw e;
        } finally {
            afterExecuted();
        }
    }
}
