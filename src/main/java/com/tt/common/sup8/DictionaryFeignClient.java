package com.tt.common.sup8;

import com.fasterxml.jackson.core.type.TypeReference;
import com.tt.common.ResultData;
import com.tt.common.feign.BaseDynamicClient;
import com.tt.common.sup8.vo.DictionaryFeignVo;
import com.tt.common.sup8.vo.ParameterDetailFeignVo;
import com.tt.common.sup8.vo.ParameterFeignVo;
import lombok.var;
import org.springframework.context.ApplicationContext;
import org.springframework.lang.Nullable;

import java.util.HashMap;
import java.util.List;

/**
 * 服务名：dictionary
 *
 * @author Shuang Yu
 */
public class DictionaryFeignClient extends BaseDynamicClient {

    /**
     * 构造函数
     *
     * @param context context
     */
    public DictionaryFeignClient(ApplicationContext context) {
        super(context, "dictionary");
    }

    /**
     * 获取字典选项
     *
     * @param code         code
     * @param enterpriseNo enterpriseNo
     * @return List<DictionaryFeignVo>
     */
    public ResultData<List<DictionaryFeignVo>> getDictItems(String code, @Nullable String enterpriseNo) {
        var type = new TypeReference<ResultData<List<DictionaryFeignVo>>>() {
        };
        var params = new HashMap<String, Object>(0);
        params.put("code", code);
        params.put("enterpriseNo", enterpriseNo);
        return get(type, "v1/info/items", params);
    }

    /**
     * 获取字典配置
     *
     * @param code         code
     * @param enterpriseNo enterpriseNo
     * @return ParameterFeignVo
     */
    public ResultData<ParameterFeignVo> getConfigValues(String code, @Nullable String enterpriseNo) {
        var type = new TypeReference<ResultData<ParameterFeignVo>>() {
        };
        var params = new HashMap<String, Object>(0);
        params.put("code", code);
        params.put("enterpriseNo", enterpriseNo);
        return get(type, "v1/config/values/code", params);
    }

    /**
     * 获取字典配置
     *
     * @param code         code
     * @param enterpriseNo enterpriseNo
     * @return ParameterDetailFeignVo
     */
    public ResultData<ParameterDetailFeignVo> getConfigDetail(String code, @Nullable String enterpriseNo) {
        var type = new TypeReference<ResultData<ParameterDetailFeignVo>>() {
        };
        var params = new HashMap<String, Object>(0);
        params.put("code", code);
        params.put("enterpriseNo", enterpriseNo);
        return get(type, "v1/config/details/code", params);
    }

}
