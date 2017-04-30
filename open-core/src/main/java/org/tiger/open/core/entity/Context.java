package org.tiger.open.core.entity;

/**
 * Created by fish on 17/4/25.
 */
public class Context {


    /**
     * 客户端id
     */
    protected Long clientId;


    /**
     * 服务端appKey
     */
    protected String appKey;


    /**
     * 服务端秘钥
     */
    private String appSecret;


    /**
     * 会话id
     */
    private String sessionId;


    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getAppSecret() {
        return appSecret;
    }

    public void setAppSecret(String appSecret) {
        this.appSecret = appSecret;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
}
