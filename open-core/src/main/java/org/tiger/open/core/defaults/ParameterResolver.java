package org.tiger.open.core.defaults;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Defaults;
import com.google.common.base.Throwables;
import com.google.common.primitives.Primitives;
import org.slf4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.util.*;

/**
 * Created by fish on 17/3/5.
 */
public class ParameterResolver {

    private final static Logger log = org.slf4j.LoggerFactory.getLogger(ParameterResolver.class);

    private static final DefaultConversionService converter = new DefaultConversionService();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static Object resolve(Class<?> paramType, String paramName, Map<String, Object> context) throws Exception {
        if (paramType.isAssignableFrom(Map.class)) {
            return context;
        } else {
            Object realParam;
            if (!Primitives.isWrapperType(paramType) && !Primitives.allPrimitiveTypes().contains(paramType) && paramType != String.class) {
                Iterator formNames;
                MultipartHttpServletRequest mRequest;
                if (paramType == MultipartFile.class) {
                    mRequest = (MultipartHttpServletRequest) context.get("request");
                    formNames = mRequest.getFileNames();
                    if (formNames.hasNext()) {
                        return mRequest.getFile((String) formNames.next());
                    }
                }

                if (paramType.isAssignableFrom(context.get(paramName).getClass())) {
                    return context.get(paramName);
                } else {
                    if (context.containsKey(paramName) && context.get(paramName) instanceof String) {
                        try {
                            return objectMapper.readValue(String.valueOf(context.get(paramName)), paramType);
                        } catch (Exception ex) {
                            log.error("failed to parse {} to {} use jackson, cause:{}", new Object[]{context.get(paramName), paramType, Throwables.getStackTraceAsString(ex)});
                        }
                    }
                    realParam = BeanUtils.instantiate(paramType);
                    //request.getParameterMap()获取的Map转为JavaBean对象
                    org.apache.commons.beanutils.BeanUtils.populate(realParam, context);
                    return realParam;
                }
            } else {
                realParam = context.get(paramName);
                if (realParam == null) {
                    realParam = Defaults.defaultValue(paramType);
                }
                return converter.convert(realParam, paramType);
            }
        }
    }

}
