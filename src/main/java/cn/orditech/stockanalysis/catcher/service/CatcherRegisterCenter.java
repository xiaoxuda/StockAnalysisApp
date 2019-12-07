package cn.orditech.stockanalysis.catcher.service;

import cn.orditech.stockanalysis.catcher.catcher.Catcher;
import cn.orditech.stockanalysis.catcher.enums.TaskTypeEnum;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 爬虫注册中心
 * @author xiaoxuda
 * @createTime 2019-12-07 15:41
 * @description
 */
public class CatcherRegisterCenter {

    private static Map<TaskTypeEnum, Catcher> catcherMap = new ConcurrentHashMap<>();

    public static void register(TaskTypeEnum taskType, Catcher catcher){
        catcherMap.put(taskType, catcher);
    }

    public static Catcher getByType(TaskTypeEnum taskType){
        return catcherMap.get(taskType);
    }

    public static Set<TaskTypeEnum> getRegisterType(){
        return new HashSet<>(catcherMap.keySet());
    }

    public static List<Catcher> getRegisterCatcher(){
        return new ArrayList<>(catcherMap.values());
    }
}
