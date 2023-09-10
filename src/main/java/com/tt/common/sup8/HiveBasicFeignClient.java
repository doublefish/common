package com.tt.common.sup8;

import com.fasterxml.jackson.core.type.TypeReference;
import com.tt.common.ResultData;
import com.tt.common.feign.BaseDynamicClient;
import com.tt.common.sup8.vo.AreaDetailFeignVo;
import com.tt.common.sup8.vo.AreaFeignVo;
import lombok.var;
import org.springframework.context.ApplicationContext;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 * 服务名：hive-basic
 *
 * @author Shuang Yu
 */
public class HiveBasicFeignClient extends BaseDynamicClient {

    /**
     * 构造函数
     *
     * @param context context
     */
    public HiveBasicFeignClient(ApplicationContext context) {
        super(context, "hive-basic");
    }

    /**
     * 根据地区Id查询下级地区
     *
     * @param regionId regionId
     * @param level    level
     * @return List<AreaFeignVo>
     */
    public ResultData<List<AreaFeignVo>> getByParent(String regionId, Integer level) {
        var type = new TypeReference<ResultData<List<AreaFeignVo>>>() {
        };
        var params = new HashMap<String, Object>(0);
        params.put("regionId", regionId);
        params.put("level", level);
        return get(type, "area/select-list", params);
    }

    /**
     * 根据地区名称查询
     *
     * @param names names
     * @return List<AreaFeignVo>
     */
    public ResultData<List<AreaFeignVo>> getByNames(Collection<String> names) {
        var type = new TypeReference<ResultData<List<AreaFeignVo>>>() {
        };
        var params = new HashMap<String, Object>(0);
        params.put("areaNames", names);
        return get(type, "area/convert-names", params);
    }

    /**
     * 根据地区编码查询详细
     *
     * @param codes codes
     * @return List<AreaDetailFeignVo>
     */
    public ResultData<List<AreaDetailFeignVo>> getDetailByCodes(Collection<String> codes) {
        var type = new TypeReference<ResultData<List<AreaDetailFeignVo>>>() {
        };
        var params = new HashMap<String, Object>(0);
        params.put("areaCodes", codes);
        return get(type, "area/convert-codes", params);
    }

}
