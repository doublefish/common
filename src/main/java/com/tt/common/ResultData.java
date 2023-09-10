package com.tt.common;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tt.common.util.ErrorCodeUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.var;
import org.apache.http.HttpStatus;
import org.apache.skywalking.apm.toolkit.trace.TraceContext;

import java.io.Serializable;

/**
 * ResultData
 *
 * @author Shuang Yu
 */
@Data
@ApiModel(value = "响应结果", description = "响应结果")
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResultData<T> implements Serializable {

    /**
     * 序列化唯一ID
     */
    private static final long serialVersionUID = 2764567879240941987L;

    /**
     * code，200 ：正常，其他：非正常响应。
     */
    @ApiModelProperty(value = "错误码", example = "200", required = true)
    private int code;

    /**
     * 消息内容
     */
    @ApiModelProperty(value = "消息内容", example = "success")
    private String message;

    /**
     * 响应消息
     */
    @ApiModelProperty(value = "数据")
    private T data;

    /**
     * 额外的数据
     */
    @ApiModelProperty(value = "额外的数据")
    private Object extraData;

    /**
     * 时间戳
     */
    @ApiModelProperty(value = "时间戳")
    private long timestamp;

    /**
     * 链路追踪ID
     */
    @ApiModelProperty(value = "链路追踪ID")
    private String traceId;

    /**
     * 构造函数
     */
    public ResultData() {
        this.timestamp = System.currentTimeMillis();
        this.traceId = TraceContext.traceId();
    }

    /**
     * 是否成功
     *
     * @return boolean
     */
    public boolean successful() {
        return getCode() == HttpStatus.SC_OK;
    }

    /**
     * 默认成功，无响应body
     *
     * @param <T> 返回值类型
     * @return ResultVo<T>
     */
    public static <T> ResultData<T> success() {
        return success(null);
    }

    /**
     * 默认成功响应
     *
     * @param <T>  T
     * @param data data
     * @return ResultVo<T>
     */
    public static <T> ResultData<T> success(T data) {
        var res = new ResultData<T>();
        res.setCode(HttpStatus.SC_OK);
        res.setMessage("success");
        res.setData(data);
        return res;
    }

    /**
     * 失败响应，只含错误码
     * 如果错误码文件可以翻译，则返回翻译的信息
     *
     * @param <T>  T
     * @param code code
     * @return ResultVo<T>
     */
    public static <T> ResultData<T> error(int code) {
        return error(code, null, null);
    }

    /**
     * 默认失败响应
     *
     * @param <T>     T
     * @param code    code
     * @param message message
     * @return ResultVo<T>
     */
    public static <T> ResultData<T> error(int code, String message) {
        return error(code, message, null);
    }

    /**
     * 默认失败响应，携带body
     *
     * @param <T>     T
     * @param code    code
     * @param message message
     * @param data    data
     * @return ResultVo<T>
     */
    public static <T> ResultData<T> error(int code, String message, T data) {
        return error(code, message, data, null);
    }

    /**
     * 默认失败响应，携带body
     *
     * @param <T>       T
     * @param code      code
     * @param message   message
     * @param data      data
     * @param extraData extraData
     * @return ResultVo<T>
     */
    public static <T> ResultData<T> error(int code, String message, T data, Object extraData) {
        var res = new ResultData<T>();
        res.setCode(code);
        res.setMessage(message != null ? message : ErrorCodeUtils.getMessage(code));
        res.setData(data);
        res.setExtraData(extraData);
        return res;
    }

    @Override
    public String toString() {
        return "ResultData{code=" + code + ", message=\"" + message + "\", data=" + data + "\", extraData=" + extraData + ", timestamp=" + timestamp + ", traceId=\"" + traceId + "\"}";
    }
}
