package com.tt.common.feign;

import com.tt.common.Constants;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.var;
import org.slf4j.MDC;

import java.util.UUID;

public class BaseInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {
        var requestId = UUID.randomUUID().toString();
        var requestTimestamp = Long.toString(System.currentTimeMillis());
        var url = template.feignTarget().url() + template.url();
        template.header(Constants.REQUEST_ID, requestId);
        template.header(Constants.REQUEST_TIMESTAMP, requestTimestamp);
        MDC.put(Constants.REQUEST_ID, requestId);
        MDC.put(Constants.REQUEST_TIMESTAMP, requestTimestamp);
    }
}