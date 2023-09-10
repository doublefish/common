package com.tt.common.util;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;
import lombok.var;
import org.apache.commons.lang3.StringUtils;
import springfox.documentation.oas.web.OpenApiTransformationContext;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * SpringFoxSwaggerUtils
 *
 * @author Shuang Yu
 */
public class SpringFoxSwaggerUtils {

    private SpringFoxSwaggerUtils() {
    }

    public static OpenAPI transform(OpenApiTransformationContext<HttpServletRequest> context) {
        var swagger = context.getSpecification();
        var scheme = "http";
        var referer = "";
        var requestOptional = context.request();
        if (requestOptional.isPresent()) {
            var request = requestOptional.get();
            referer = request.getHeader("Referer");
        }

        if (StringUtils.isNotEmpty(referer)) {
            //获取协议
            scheme = referer.split(":")[0];
        }

        List<Server> servers = new ArrayList<>();
        var finalScheme = scheme;
        //重新组装server信息
        swagger.getServers().forEach(item -> {

            //替换协议，去掉默认端口
            item.setUrl(clearDefaultPort(item.getUrl().replace("http", finalScheme)));
            servers.add(item);
        });
        swagger.setServers(servers);
        return swagger;
    }

    /**
     * 清除默认端口
     *
     * @param url url
     */
    private static String clearDefaultPort(String url) {
        var port = url.split(":")[2];
        var port80 = "80";
        var port443 = "443";
        if (port80.equals(port) || port443.equals(port)) {
            return url.replace(":80", "").replace(":443", "");
        }
        return url;
    }
}