package org.tiger.open.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.collect.Maps;
import com.google.common.hash.Hashing;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.tiger.open.core.defaults.*;
import org.tiger.open.core.entity.Context;
import org.tiger.open.core.entity.OpenResponse;
import org.tiger.open.core.exceptions.OpenClientException;
import org.tiger.open.core.exceptions.OpenServerException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

@Controller
public class RouterController {

    private final static Logger log = org.slf4j.LoggerFactory.getLogger(RouterController.class);

    @Autowired
    private OpenContainer openContainer;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private OpenSecurityService openSecurityService;

    /**
     * @param request
     * @param response
     */
    @RequestMapping(value = {"/router"})
    public void router(HttpServletRequest request, HttpServletResponse response) throws Exception {
        //当前时间毫秒
        long current = System.currentTimeMillis();
        try {
            //封装参数
            Map<String, Object> params = Maps.newTreeMap();
            Map parameterMap = request.getParameterMap();
            Iterator iterator = parameterMap.keySet().iterator();

            //打印所有请求参数
            if (log.isDebugEnabled()) {
                StringBuilder builder = new StringBuilder();
                while (iterator.hasNext()) {
                    String key = (String) iterator.next();
                    params.put(key, request.getParameter(key));
                    builder.append(String.format("request %s = %s \n", key, request.getParameter(key)));
                }
                log.debug(builder.toString());
            } else {
                while (iterator.hasNext()) {
                    String key = (String) iterator.next();
                    params.put(key, request.getParameter(key));
                }
            }

            //校验方法名称
            String method = params.get(OpenParams.METHOD).toString();
            if (StringUtils.isEmpty(method)) {
                log.error("method.miss");
                throw new OpenClientException("method.miss");
            }
            String group = request.getParameter(OpenParams.GROUP);
            String version = request.getParameter(OpenParams.VERSION);
            group = StringUtils.isEmpty(group) ? OpenParams.DEFAULT_GROUP : group;
            version = StringUtils.isEmpty(version) ? OpenParams.DEFAULT_VERSION : version;
            InvokeMethod invokeMethod = openContainer.getInvokeMethod(method + "-" + group + "-" + version);
            if (invokeMethod == null) {
                log.error("method ({}) not found", method);
                throw new OpenClientException("method.target.not.found");
            }

            //校验时间戳时间格式为毫秒数
            if (params.get(OpenParams.TIMESTAMP) == null || "".equalsIgnoreCase(params.get(OpenParams.TIMESTAMP).toString())) {
                throw new OpenClientException("timestamp.miss");
            }

            //过期
            Long requestTime = Long.valueOf(params.get(OpenParams.TIMESTAMP).toString());
            if ((requestTime + 30 * 60 * 1000) < current) {
                log.error("invoke.expired");
                throw new OpenClientException("invoke.expired");
            }

            //校验方法是否支持
            String httpMethod = request.getMethod();
            if (invokeMethod.getHttpMethods().length > 0) {
                boolean founded = false;
                RequestMethod[] requestMethod = invokeMethod.getHttpMethods();
                int length = requestMethod.length;

                for (int i = 0; i < length; ++i) {
                    RequestMethod springMethod = requestMethod[i];
                    if (Objects.equals(springMethod.name(), httpMethod)) {
                        founded = true;
                        break;
                    }
                }
                if (!founded) {
                    throw new OpenClientException(OpenCode.CODE_405, "method.not.allowed");
                }
            }

            //appKey查找
            String appKey = params.get(OpenParams.APP_KEY) == null ? null : params.get(OpenParams.APP_KEY).toString();
            if (StringUtils.isEmpty(appKey)) {
                throw new OpenClientException("appKey.miss");
            }

            Context context = null;

            //校验签名
            if (invokeMethod.isSign()) {
                String sign = params.get(OpenParams.SIGN) == null ? null : params.get(OpenParams.SIGN).toString();
                if (StringUtils.isEmpty(sign)) {
                    throw new OpenClientException("sign.mismatch");
                }
                //校验签名
                context = openSecurityService.findClientByAppKey(appKey);
                if (context == null) {
                    throw new OpenClientException("appKey.incorrect");
                }
                //除去sign
                params.remove(OpenParams.SIGN);


                //解析签名
                String toVerify = Joiner.on('&').withKeyValueSeparator("=").join(params);
                String expected = Hashing.md5().newHasher().putString(toVerify, Charsets.UTF_8).putString(context.getAppSecret(), Charsets.UTF_8).hash().toString();
                if (!Objects.equals(expected, sign)) {
                    throw new OpenClientException("sign.mismatch");
                }
            }


            if (context == null) {
                //校验签名
                context = openSecurityService.findClientByAppKey(appKey);
                if (context == null) {
                    throw new OpenClientException("appKey.incorrect");
                }
            }
            //获取当前clientId是否有调用此方法的权限
            if (!openSecurityService.isPermission(context.getClientId(), method)) {
                throw new OpenClientException("permission.deny");
            }

            //会话
            if (invokeMethod.isSession()) {
                String sessionId = params.get(OpenParams.SESSION_ID) == null ? null : params.get(OpenParams.SESSION_ID).toString();
                if (StringUtils.isEmpty(sessionId)) {
                    throw new OpenClientException("sessionId.miss");
                }
                Context sessionContext = openSecurityService.findClientByClientId(sessionId);
                if (sessionContext == null) {
                    throw new OpenClientException("session.expired");
                }
                if (!sessionContext.getClientId().equals(context.getClientId())) {
                    throw new OpenClientException("session.mismatch");
                }
            }
            //存储
            RequestContext.setContext(request, response, context);
            //正常输出
            Object result = invokeMethod.invoke(params);
            if (!(result instanceof OpenResponse)) {
                result = OpenResponse.ok(result);
            }
            Output.write(objectMapper, result, response);

        } catch (Exception ex) {
            log.error(ex.getLocalizedMessage());
            throw new OpenServerException(ex.getMessage());
        } finally {
            //清除
            RequestContext.clear();
        }

    }

}
