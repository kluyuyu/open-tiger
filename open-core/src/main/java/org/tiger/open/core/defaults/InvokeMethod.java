package org.tiger.open.core.defaults;

import com.google.common.base.MoreObjects;
import com.google.common.base.Throwables;
import org.slf4j.Logger;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.RequestMethod;
import org.tiger.open.core.exceptions.OpenServerException;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * Created by fish on 17/3/5.
 */
public class InvokeMethod {

    private final static Logger log =  org.slf4j.LoggerFactory.getLogger(InvokeMethod.class);

    /**
     * 主类
     */
    private final Object owner;

    /**
     * 方法
     */
    private final Method method;

    /**
     * http方法
     */
    private final RequestMethod[] httpMethods;

    /**
     * 参数类型
     */
    private final Class<?>[] paramTypes;

    /**
     * 参数名称
     */
    private final String[] paramNames;

    /**
     * 是否要会话
     */
    private boolean isSession;

    /**
     * 是否要签名
     */
    private boolean isSign;


    public InvokeMethod(Object owner,
                        Method method,
                        RequestMethod[] httpMethods,
                        Class<?>[] paramTypes,
                        String[] paramNames,
                        boolean isSession,
                        boolean isSign) {
        this.owner = owner;
        this.method = method;
        this.httpMethods = httpMethods;
        this.paramTypes = paramTypes;
        this.paramNames = paramNames;
        this.isSession = isSession;
        this.isSign = isSign;
    }

    public Object invoke(Map<String, Object> params) {
        Object[] concernedParams = new Object[this.paramTypes.length];
        for(int i = 0; i < this.paramTypes.length; ++i) {
            Class type = this.paramTypes[i];
            String paramName = this.paramNames[i];
            try {
                concernedParams[i] = ParameterResolver.resolve(type, paramName, params);
            } catch (Exception ex) {
                log.error("{} has no default constructor, cause:{}", type, Throwables.getStackTraceAsString(ex));
                throw new OpenServerException("server.internal.error");
            }
        }
        InvokeValidator.instance().validateParams(this.owner, this.method, concernedParams);
        return ReflectionUtils.invokeMethod(this.method, this.owner, concernedParams);
    }

    public String toString() {
        return MoreObjects.toStringHelper(this).add("owner", this.owner).add("method", this.method).add("paramTypes", this.paramTypes).add("paramNames", this.paramNames).omitNullValues().toString();
    }


    public static Logger getLog() {
        return log;
    }

    public Object getOwner() {
        return owner;
    }

    public Method getMethod() {
        return method;
    }

    public RequestMethod[] getHttpMethods() {
        return httpMethods;
    }

    public Class<?>[] getParamTypes() {
        return paramTypes;
    }

    public String[] getParamNames() {
        return paramNames;
    }

    public boolean isSession() {
        return isSession;
    }

    public void setSession(boolean session) {
        isSession = session;
    }

    public boolean isSign() {
        return isSign;
    }

    public void setSign(boolean sign) {
        isSign = sign;
    }
}
