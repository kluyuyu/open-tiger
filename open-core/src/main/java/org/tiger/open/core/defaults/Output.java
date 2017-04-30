package org.tiger.open.core.defaults;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;


/**
 * Created by fish on 17/3/5.
 */
public class Output {

    /**
     * 写json
     *
     * @param result
     * @param response
     */
    public static void write(ObjectMapper mapper, Object result, HttpServletResponse response) {
        PrintWriter out = null;
        try {
            if (response.isCommitted()) {
                return;
            }
            response.addHeader("Access-Control-Allow-Origin", "*");
            response.addHeader("Access-Control-Allow-Methods", "*");
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json");
            out = response.getWriter();
            String json = result instanceof String ? (String) result : mapper.writeValueAsString(result);
            out.print(json);
            out.flush();
            out.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (out != null) {
                out.close();
            }
        }

    }


    /**
     * 所有调用参数(包括pampasCall, 但不包括sign本身,包括请求体中的JSON内容),
     *
     * 按照字母序升序排列, 然后调用者再附加上给分配给自己的appSecret, 再做md5签名
     *
     * appKey=foobar&data={“a”: “1”}&pampasCall=say.hi&timestamp=20160320133000my.secret
     *
     */
    public static String md5Sign(Map<String,String> params, String secret){
        List<String> paramNames = new ArrayList<String>(params.size());
        paramNames.addAll(params.keySet());
        //升序
        Collections.sort(paramNames);
        StringBuffer buffer = new StringBuffer();
        for(int i = 0 ;i < params.size() ; i ++){
            String paramName = paramNames.get(i);
            if(i == params.size() -1){
                buffer.append(paramName).append("=").append(params.get(paramName));
            }else {
                buffer.append(paramName).append("=").append(params.get(paramName)).append("&");
            }
        }
        //secret添加到尾部
        buffer.append(secret);
        try {
            MessageDigest messageDigest  = MessageDigest.getInstance("MD5");
            byte [] bytes = messageDigest.digest(buffer.toString().getBytes("UTF-8"));
            StringBuilder sign = new StringBuilder();
            for (int i = 0; i < bytes.length; i++) {
                String hex = Integer.toHexString(bytes[i] & 0xFF);
                if (hex.length() == 1) {
                    sign.append("0");
                }
                sign.append(hex);
            }
            return sign.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}
