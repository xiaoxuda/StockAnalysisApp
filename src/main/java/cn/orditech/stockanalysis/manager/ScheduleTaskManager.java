package cn.orditech.stockanalysis.manager;

import cn.orditech.schedule.ScheduleTaskService;
import cn.orditech.stockanalysis.dao.DailyTradeDetailDao;
import cn.orditech.stockanalysis.dao.StockInfoDao;
import cn.orditech.stockanalysis.task.LocalCacheCleanTask;
import cn.orditech.stockanalysis.task.MarketValueCalculateTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * Created by kimi on 2017/3/12.
 */
@Service
public class ScheduleTaskManager {
    @Autowired
    private StockInfoDao stockInfoDao;
    @Autowired
    private DailyTradeDetailDao dailyTradeDetailDao;

    @PostConstruct
    public void init () {
        //提交缓存清理的定时任务
        ScheduleTaskService.commitTask (new LocalCacheCleanTask ());

        //提交市值计算的定时任务
        MarketValueCalculateTask task = new MarketValueCalculateTask ();
        task.setStockInfoDao (stockInfoDao);
        task.setDailyTradeDetailDao (dailyTradeDetailDao);
        ScheduleTaskService.commitTask (task);

    }
}
