package org.tiger.open.core.defaults;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;
import org.tiger.open.core.annotations.OpenMethod;
import org.tiger.open.core.annotations.OpenService;

import javax.annotation.PostConstruct;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by fish on 17/3/5.
 */

@Component
public class OpenContainer {

    private final static Logger log = org.slf4j.LoggerFactory.getLogger(OpenContainer.class);

    private final ApplicationContext applicationContext;

    private final Map<String, InvokeMethod> methods;


    /**
     * 获取spring上下文
     */
    @Autowired
    public OpenContainer(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        this.methods = new ConcurrentHashMap();
    }

    /**
     * 初始化注解
     */
    @PostConstruct
    public void init() {
        Map<String, Object> openServices = this.applicationContext.getBeansWithAnnotation(OpenService.class);
        Iterator iterator = openServices.values().iterator();
        while (iterator.hasNext()) {
            Object openService = iterator.next();
            this.handleOpenService(openService);
        }

    }


    /**
     * 对每一个OpenService注解进行解析
     *
     * @param openService
     */
    private void handleOpenService(Object openService) {

        for (Class aClass = openService.getClass(); aClass != null; aClass = aClass.getSuperclass()) {

            List allMethods = Arrays.asList(aClass.getDeclaredMethods());

            Iterator iterator = allMethods.iterator();

            while (iterator.hasNext()) {

                Method method = (Method) iterator.next();

                if (method.isAnnotationPresent(OpenMethod.class)) {
                    OpenMethod openMethod = (OpenMethod) method.getAnnotation(OpenMethod.class);
                    String methodName = openMethod.name();
                    String group = openMethod.group();
                    String version = openMethod.version();
                    RequestMethod[] httpMethods = openMethod.httpMethods();
                    Class[] paramTypes = method.getParameterTypes();
                    String[] paramNames = openMethod.paramNames();
                    boolean isSession = openMethod.isSession();
                    boolean isSign = openMethod.isSign();
                    InvokeMethod invokeMethod = new InvokeMethod(openService,
                            method,
                            httpMethods,
                            paramTypes,
                            paramNames,
                            isSession,
                            isSign);
                    if (this.methods.put(methodName + "-" + group + "-" + version, invokeMethod) != null) {
                        log.error("duplicate open method name:({}) group({}) version({})", methodName, group, version);
                        throw new RuntimeException("duplicated open method name: " + methodName + "-" + group + "-" + version);
                    }
                }

            }
        }

    }

    public InvokeMethod getInvokeMethod(String name) {
        return methods.get(name);
    }


}
