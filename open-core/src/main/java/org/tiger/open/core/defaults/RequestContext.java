package org.tiger.open.core.defaults;


import org.tiger.open.core.entity.Context;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 用于解决当前请求访问问题
 * <p>
 * Created by fish on 17/3/29.
 */
public final class RequestContext {

    private static ThreadLocal<Request> store = new ThreadLocal();

    private RequestContext() {
    }

    /**
     * 当前线程上操作,所以在外层不枷锁。
     */
    public static void setContext(HttpServletRequest request,HttpServletResponse response,Context context) {
        Request r = store.get();
        if (r == null) {
            r = new RequestContext.Request(request,response,context);
        }
        r.setContext(context);
        store.set(r);
    }

    public static Context getContext() {
        return store.get().getContext();
    }
    public static HttpServletRequest getRequest() {
        return store.get().getRequest();
    }

    public static HttpServletResponse getResponse() {
        return store.get().getResponse();
    }

    public static void clear() {
        store.remove();
    }


    /**
     * 内部类
     */
    static class Request {

        HttpServletRequest request;

        HttpServletResponse response;

        Context context;

        public Request(HttpServletRequest request,HttpServletResponse response,Context context){
            this.request = request;
            this.response = response;
            this.context = context;
        }

        public HttpServletRequest getRequest() {
            return request;
        }

        public void setRequest(HttpServletRequest request) {
            this.request = request;
        }

        public HttpServletResponse getResponse() {
            return response;
        }

        public void setResponse(HttpServletResponse response) {
            this.response = response;
        }

        public Context getContext() {
            return context;
        }

        public void setContext(Context context) {
            this.context = context;
        }
    }


}
