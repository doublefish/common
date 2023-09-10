package com.tt.common.feign;

import com.tt.common.Constants;
import com.tt.common.ResultData;
import com.tt.common.ServiceException;
import feign.FeignException;
import lombok.var;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.slf4j.MDC;
import org.springframework.cloud.openfeign.FeignClient;

import java.lang.reflect.ParameterizedType;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.function.*;

/**
 * BaseClientService
 *
 * @author Shuang Yu
 */
public class BaseClientService<Client> {

    protected final Client client;
    protected final String clientName;
    protected final int errorCode;
    protected final String errorMessage;


    public BaseClientService(Client client) {
        this(client, null);
    }

    public BaseClientService(Client client, String clientName) {
        this(client, clientName, 0);
    }

    public BaseClientService(Client client, int errorCode) {
        this(client, "", errorCode);
    }

    public BaseClientService(Client client, String clientName, int errorCode) {
        this(client, clientName, errorCode, null);
    }

    public BaseClientService(Client client, String clientName, int errorCode, String errorMessage) {
        this.client = client;
        this.clientName = StringUtils.isEmpty(clientName) ? getClientName(client) : clientName;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    /**
     * run
     *
     * @param runnable runnable
     */
    public void run(Runnable runnable) {
        var mills = System.currentTimeMillis();
        try {
            beforeExecute(mills);
            runnable.run();
        } catch (Exception e) {
            error(e);
        } finally {
            afterExecuted(mills);
        }
    }


    /**
     * run
     *
     * @param runnable     runnable
     * @param errorCode    errorCode
     * @param errorMessage errorMessage
     */
    public void run(Runnable runnable, int errorCode, String errorMessage) {
        var mills = System.currentTimeMillis();
        try {
            beforeExecute(mills);
            runnable.run();
        } catch (Exception e) {
            error(e, errorCode, errorMessage);
        } finally {
            afterExecuted(mills);
        }
    }

    /**
     * call
     *
     * @param <R>      R
     * @param <D>      D
     * @param callable callable
     * @return D
     */
    public <R extends ResultData<D>, D> D call(Callable<R> callable) {
        var mills = System.currentTimeMillis();
        try {
            beforeExecute(mills);
            var res = callable.call();
            return ok(res);
        } catch (Exception e) {
            return error(e);
        } finally {
            afterExecuted(mills);
        }
    }

    /**
     * call
     *
     * @param <R>          R
     * @param <D>          D
     * @param callable     callable
     * @param errorCode    errorCode
     * @param errorMessage errorMessage
     * @return D
     */
    public <R extends ResultData<D>, D> D call(Callable<R> callable, int errorCode, String errorMessage) {
        var mills = System.currentTimeMillis();
        try {
            beforeExecute(mills);
            var res = callable.call();
            return ok(res, errorCode, errorMessage);
        } catch (Exception e) {
            return error(e, errorCode, errorMessage);
        } finally {
            afterExecuted(mills);
        }
    }

    /**
     * get
     *
     * @param <R>      R
     * @param <D>      D
     * @param supplier supplier
     * @return D
     */
    public <R extends ResultData<D>, D> D get(Supplier<R> supplier) {
        var mills = System.currentTimeMillis();
        try {
            beforeExecute(mills);
            var res = supplier.get();
            return ok(res);
        } catch (Exception e) {
            return error(e);
        } finally {
            afterExecuted(mills);
        }
    }

    /**
     * get
     *
     * @param <R>          R
     * @param <D>          D
     * @param supplier     supplier
     * @param errorCode    errorCode
     * @param errorMessage errorMessage
     * @return D
     */
    public <R extends ResultData<D>, D> D get(Supplier<R> supplier, int errorCode, String errorMessage) {
        var mills = System.currentTimeMillis();
        try {
            beforeExecute(mills);
            var res = supplier.get();
            return ok(res, errorCode, errorMessage);
        } catch (Exception e) {
            return error(e, errorCode, errorMessage);
        } finally {
            afterExecuted(mills);
        }
    }

    /**
     * accept
     *
     * @param <T>      T
     * @param consumer consumer
     * @param t        the input argument
     */
    public <T> void accept(Consumer<T> consumer, T t) {
        var mills = System.currentTimeMillis();
        try {
            beforeExecute(mills);
            consumer.accept(t);
        } catch (Exception e) {
            error(e);
        } finally {
            afterExecuted(mills);
        }
    }

    /**
     * accept
     *
     * @param <T>          T
     * @param consumer     consumer
     * @param t            the input argument
     * @param errorCode    errorCode
     * @param errorMessage errorMessage
     */
    public <T> void accept(Consumer<T> consumer, T t, int errorCode, String errorMessage) {
        var mills = System.currentTimeMillis();
        try {
            beforeExecute(mills);
            consumer.accept(t);
        } catch (Exception e) {
            error(e, errorCode, errorMessage);
        } finally {
            afterExecuted(mills);
        }
    }

    /**
     * accept
     *
     * @param <T>      T
     * @param consumer consumer
     * @param t        the first function argument
     * @param u        the second function argument
     */
    public <T, U> void accept(BiConsumer<T, U> consumer, T t, U u) {
        var mills = System.currentTimeMillis();
        try {
            beforeExecute(mills);
            consumer.accept(t, u);
        } catch (Exception e) {
            error(e);
        } finally {
            afterExecuted(mills);
        }
    }

    /**
     * accept
     *
     * @param <T>          T
     * @param consumer     consumer
     * @param t            the first function argument
     * @param u            the second function argument
     * @param errorCode    errorCode
     * @param errorMessage errorMessage
     */
    public <T, U> void accept(BiConsumer<T, U> consumer, T t, U u, int errorCode, String errorMessage) {
        var mills = System.currentTimeMillis();
        try {
            beforeExecute(mills);
            consumer.accept(t, u);
        } catch (Exception e) {
            error(e, errorCode, errorMessage);
        } finally {
            afterExecuted(mills);
        }
    }

    /**
     * apply
     *
     * @param <T>      T
     * @param <R>      R
     * @param <D>      D
     * @param function function
     * @param t        the first function argument
     * @return D
     */
    public <T, R extends ResultData<D>, D> D apply(Function<T, R> function, T t) {
        var mills = System.currentTimeMillis();
        try {
            beforeExecute(mills);
            var res = function.apply(t);
            return ok(res);
        } catch (Exception e) {
            return error(e);
        } finally {
            afterExecuted(mills);
        }
    }

    /**
     * apply
     *
     * @param <T>          T
     * @param <R>          R
     * @param <D>          D
     * @param function     function
     * @param t            the first function argument
     * @param errorCode    errorCode
     * @param errorMessage errorMessage
     * @return D
     */
    public <T, R extends ResultData<D>, D> D apply(Function<T, R> function, T t, int errorCode, String errorMessage) {
        var mills = System.currentTimeMillis();
        try {
            beforeExecute(mills);
            var res = function.apply(t);
            return ok(res, errorCode, errorMessage);
        } catch (Exception e) {
            return error(e, errorCode, errorMessage);
        } finally {
            afterExecuted(mills);
        }
    }

    /**
     * apply
     *
     * @param <T>      T
     * @param <U>      U
     * @param <R>      R
     * @param <D>      D
     * @param function function
     * @param t        the first function argument
     * @param u        the second function argument
     * @return D
     */
    public <T, U, R extends ResultData<D>, D> D apply(BiFunction<T, U, R> function, T t, U u) {
        var mills = System.currentTimeMillis();
        try {
            beforeExecute(mills);
            var res = function.apply(t, u);
            return ok(res);
        } catch (Exception e) {
            return error(e);
        } finally {
            afterExecuted(mills);
        }
    }

    /**
     * apply
     *
     * @param <T>          T
     * @param <U>          U
     * @param <R>          R
     * @param <D>          D
     * @param function     function
     * @param t            the first function argument
     * @param u            the second function argument
     * @param errorCode    errorCode
     * @param errorMessage errorMessage
     * @return D
     */
    public <T, U, R extends ResultData<D>, D> D apply(BiFunction<T, U, R> function, T t, U u, int errorCode, String errorMessage) {
        var mills = System.currentTimeMillis();
        try {
            beforeExecute(mills);
            var res = function.apply(t, u);
            return ok(res, errorCode, errorMessage);
        } catch (Exception e) {
            return error(e, errorCode, errorMessage);
        } finally {
            afterExecuted(mills);
        }
    }

    protected void beforeExecute(long mills) {
        var requestId = UUID.randomUUID().toString();
        var requestTimestamp = Long.toString(System.currentTimeMillis());
        MDC.put(Constants.REQUEST_ID, requestId);
        MDC.put(Constants.REQUEST_TIMESTAMP, Long.toString(mills));
    }

    protected void afterExecuted(long mills) {
        MDC.remove(Constants.REQUEST_ID);
        MDC.remove(Constants.REQUEST_TIMESTAMP);
    }

    /**
     * 在beforeExecute和afterExecuted之间有效
     *
     * @return requestId
     */
    protected String getRequestId() {
        return MDC.get(Constants.REQUEST_ID);
    }

    protected <D> D ok(ResultData<D> resultData) {
        return ok(resultData, errorCode, errorMessage);
    }

    protected <D> D ok(ResultData<D> resultData, int errorCode, String errorMessage) {
        if (resultData.getCode() != HttpStatus.SC_OK) {
            var code = errorCode > 0 ? errorCode : resultData.getCode();
            var message = StringUtils.isNotEmpty(errorMessage) ? errorMessage : String.format("调用[%s]发生异常：%s", clientName, resultData.getMessage());
            throw new ServiceException(code, message, null, getRequestId());
        }
        return resultData.getData();
    }

    protected <D> D error(Exception e) {
        return error(e, errorCode, errorMessage);
    }

    protected <D> D error(Exception e, int errorCode, String errorMessage) {
        if (e instanceof ServiceException) {
            throw (ServiceException) e;
        }
        var code = e instanceof FeignException.NotFound ? HttpStatus.SC_NOT_FOUND : errorCode > 0 ? errorCode : HttpStatus.SC_INTERNAL_SERVER_ERROR;
        var message = StringUtils.isNotEmpty(errorMessage) ? errorMessage : String.format("调用[%s]发生异常：%s", clientName, e.getMessage());
        throw new ServiceException(code, message, null, getRequestId(), e);
    }

    protected String getClientName(Client client) {
        if (client instanceof BaseDynamicClient) {
            return ((BaseDynamicClient) client).name;
        }
        try {
            var parameterizedType = (ParameterizedType) this.getClass().getGenericSuperclass();
            var typeArguments = parameterizedType.getActualTypeArguments();
            var clazz = Class.forName(typeArguments[0].getTypeName());
            var feignClient = clazz.getAnnotation(FeignClient.class);
            if (feignClient != null) {
                return feignClient.name();
            }
            return null;
        } catch (Exception e) {
            throw new RuntimeException("获取FeignClient注解发生异常：" + e.getMessage(), e);
        }
    }


}
