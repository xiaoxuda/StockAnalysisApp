package cn.orditech.tools;

import javax.servlet.ServletRequest;

/**
 * Created by kimi on 2017/3/12.
 */
public class RequestTool {

    /**
     * 获取IP地址
     * @param request
     * @return
     */
    public static String getIp(ServletRequest request){
        return request.getRemoteAddr ();
    }
}
