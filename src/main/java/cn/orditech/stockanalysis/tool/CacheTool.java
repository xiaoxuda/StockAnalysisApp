package cn.orditech.stockanalysis.tool;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by kimi on 2017/3/12.
 */
public class CacheTool {
    /**
     * 记录当天登录的用户IP以及对应的访问次数,每日清零
     */
    private static Set<String> loginIpCache = new HashSet<String> ();

    /**
     * 当日访问量
     * @return
     */
    public static long getLoginCount(){
        return loginIpCache.size ();
    }

    /**
     * 缓存当前IP
     * @param ip
     */
    public static void putLoginIp(String ip){
        loginIpCache.add(ip);
    }

    /**
     * 清理IP缓存，由定时任务专门处理，每日23：58分到00：02不统计IP
     */
    public static void cleanLoginIpCache(){
        loginIpCache.clear ();
    }
}
