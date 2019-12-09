package cn.orditech.stockanalysis.catcher.catcher;

import cn.orditech.stockanalysis.catcher.CatchTask;
import cn.orditech.stockanalysis.catcher.enums.TaskTypeEnum;
import cn.orditech.stockanalysis.entity.StockInfo;

/**
 * 爬虫功能接口
 * @author xiaoxuda
 * @createTime 2019-12-07 11:09
 * @description
 */
public interface Catcher {
    /**
     * 获取给定任务的相应数据
     * @param task
     * @param nowAgainTime
     * @return
     */
    String catchAction (CatchTask task, int nowAgainTime);

    /**
     * 生成数据抓去任务
     * @param stockInfo
     * @return
     */
    CatchTask generateTask (StockInfo stockInfo);

    /**
     * 爬虫处理的任务类型
     * @return
     */
    TaskTypeEnum getTaskType ();
}
