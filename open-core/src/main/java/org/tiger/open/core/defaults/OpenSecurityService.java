package org.tiger.open.core.defaults;


import org.tiger.open.core.entity.Context;

/**
 * Created by fish on 17/3/6.
 */
public interface OpenSecurityService {


    /**
     * 根据appKey 查询
     * @param appKey
     */
    Context findClientByAppKey(String appKey);


    /**
     * 根据 clientId 和调用的方法名openMethod称查询是否有权限
     *
     * @param clientId
     * @param openMethod
     */
    boolean isPermission(Long clientId, String openMethod);


    /**
     * 根据 clientId 查询 OpenClient
     * @param clientId
     */
    Context findClientByClientId(Long clientId);


    /**
     * 根据 sessionId 查询 OpenClient
     * @param sessionId
     */
    Context findClientByClientId(String sessionId);
}
