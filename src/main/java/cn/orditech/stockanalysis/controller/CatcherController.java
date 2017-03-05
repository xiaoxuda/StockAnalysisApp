/**
 *
 */
package cn.orditech.stockanalysis.controller;

import javax.annotation.PostConstruct;

import cn.orditech.schedule.ScheduleTaskService;
import cn.orditech.stockanalysis.catcher.service.CatcherManageService;
import cn.orditech.stockanalysis.catcher.service.TaskGenerateService;
import cn.orditech.stockanalysis.dao.DailyTradeDetailDao;
import cn.orditech.stockanalysis.dao.StockInfoDao;
import cn.orditech.stockanalysis.task.MarketValueCalculateTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author kimi
 *         用于在系统启动后启动爬虫系统
 */
@Component
public class CatcherController {
    @Autowired
    private CatcherManageService catcherManageService;
    @Autowired
    private TaskGenerateService taskGenerateService;
    @Autowired
    private StockInfoDao stockInfoDao;
    @Autowired
    private DailyTradeDetailDao dailyTradeDetailDao;

    @PostConstruct
    public void starter () {
        //开启爬虫监控
        catcherManageService.startCatcherMonitor ();
        //启动任务生成器
        taskGenerateService.startGenerator ();

        //提交市值计算的定时任务
        MarketValueCalculateTask task = new MarketValueCalculateTask ();
        task.setStockInfoDao (stockInfoDao);
        task.setDailyTradeDetailDao (dailyTradeDetailDao);
        ScheduleTaskService.commitTask (task);
    }


}
