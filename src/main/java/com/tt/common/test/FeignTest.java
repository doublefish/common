package com.tt.common.test;

import com.tt.common.sup8.DictionaryFeignClient;
import com.tt.common.sup8.HiveBasicFeignClient;
import com.tt.common.sup8.LanguageFeignClient;
import lombok.var;
import org.springframework.context.ApplicationContext;

import java.util.Arrays;
import java.util.Collections;

/**
 * FeignTest
 *
 * @author Shuang Yu
 */
public class FeignTest {

    private final HiveBasicFeignClient hiveBasicFeignClient;
    private final DictionaryFeignClient dictionaryFeignClient;
    private final LanguageFeignClient languageFeignClient;

    public FeignTest(ApplicationContext context) {
        hiveBasicFeignClient = new HiveBasicFeignClient(context);
        dictionaryFeignClient = new DictionaryFeignClient(context);
        languageFeignClient = new LanguageFeignClient(context);
    }

    public void start() {
        var eno = "000000000000";
        var areas = hiveBasicFeignClient.getByParent("340000000000", 2);

        var area = hiveBasicFeignClient.getByNames(Collections.singleton("长淮街道"));
        var areaDetail = hiveBasicFeignClient.getDetailByCodes(Collections.singleton("340102011000"));

        var items = dictionaryFeignClient.getDictItems("org_activation_status", eno);
        var config = dictionaryFeignClient.getConfigValues("org_activation_process", eno);

        var languages = languageFeignClient.getKeyData("organization", Arrays.asList("20500000", "20500001", "20500002"));

    }

}
