package com.tt.common.feign;

import com.tt.common.Constants;
import com.tt.common.util.FeignUtils;
import feign.FeignException;
import feign.Response;
import feign.codec.Decoder;
import lombok.var;
import org.slf4j.MDC;
import org.springframework.cloud.openfeign.support.ResponseEntityDecoder;

import java.io.IOException;
import java.lang.reflect.Type;

public class BaseResponseEntityDecoder extends ResponseEntityDecoder implements Decoder {

    public BaseResponseEntityDecoder(Decoder decoder) {
        super(decoder);
    }

    @Override
    public Object decode(Response response, Type type) throws IOException, FeignException {
        var headers = response.request().headers();
        var body = response.body();
        var requestId = FeignUtils.getFirstHeader(headers, Constants.REQUEST_ID);
        var requestTimestamp = FeignUtils.getFirstHeader(headers, Constants.REQUEST_TIMESTAMP);
        MDC.put(Constants.REQUEST_ID, requestId);
        MDC.put(Constants.REQUEST_TIMESTAMP, requestTimestamp);
        return super.decode(response, type);
    }
}
