package com.tt.common.sup8;

import com.fasterxml.jackson.core.type.TypeReference;
import com.tt.common.ResultData;
import com.tt.common.feign.BaseDynamicClient;
import com.tt.common.sup8.vo.LanguageFeignVo;
import com.tt.common.sup8.vo.LanguageQueryVo;
import lombok.var;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.util.Assert;

import java.util.*;

/**
 * 服务名：language
 *
 * @author Shuang Yu
 */
public class LanguageFeignClient extends BaseDynamicClient {

    /**
     * 构造函数
     *
     * @param context context
     */
    public LanguageFeignClient(ApplicationContext context) {
        super(context, "language");
    }

    /**
     * 获取KeyData
     *
     * @param queryVos queryVos
     */
    public ResultData<List<LanguageFeignVo>> getKeyData(List<LanguageQueryVo> queryVos) {
        var type = new TypeReference<ResultData<List<LanguageFeignVo>>>() {
        };
        return post(type, "v1/static/key/data", queryVos);
    }

    /**
     * 获取KeyData
     *
     * @param serviceId serviceId
     * @param keys      keys
     * @return Map<String, String>
     */
    public ResultData<LanguageFeignVo> getKeyData(String serviceId, Collection<String> keys) {
        Assert.hasLength(serviceId, "serviceId must not be empty");
        Assert.notEmpty(keys, "keys must not be empty");
        var queryVo = new LanguageQueryVo(serviceId, keys);
        var res = getKeyData(Collections.singletonList(queryVo));
        if (res == null) {
            return null;
        }
        var data = new ResultData<LanguageFeignVo>();
        data.setCode(res.getCode());
        data.setMessage(res.getMessage());
        if (res.getData() != null && CollectionUtils.isNotEmpty(res.getData())) {
            data.setData(res.getData().get(0));
        } else {
            data.setData(null);
        }
        data.setTimestamp(res.getTimestamp());
        data.setTraceId(res.getTraceId());
        return data;
    }

}
