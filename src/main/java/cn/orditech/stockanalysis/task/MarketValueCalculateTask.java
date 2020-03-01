package cn.orditech.stockanalysis.task;

import cn.orditech.schedule.ScheduleTask;
import cn.orditech.schedule.ScheduleTaskService;
import cn.orditech.stockanalysis.entity.DailyTradeDetail;
import cn.orditech.stockanalysis.dao.DailyTradeDetailDao;
import cn.orditech.stockanalysis.dao.StockInfoDao;
import cn.orditech.stockanalysis.entity.StockInfo;
import cn.orditech.tools.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.List;

/**
 * 市值计算任务
 *
 * @author kimi
 * @date 2017/1/18
 */
@Component
public class MarketValueCalculateTask extends ScheduleTask {
    @Autowired
    private StockInfoDao stockInfoDao;
    @Autowired
    private DailyTradeDetailDao dailyTradeDetailDao;

    /**
     * 默认调度周期间隔
     */
    private static int DefaultCycleInterval = 10;

    /**
     * 调度周期
     */
    private int cycleInterval = DefaultCycleInterval;

    @Override
    public boolean isExecNow () {
        return false;
    }

    @Override
    public long cycleInterval () {
        return cycleInterval;
    }

    @PostConstruct
    public void register(){
        ScheduleTaskService.commitTask(this);
    }

    @Override
    public void run () {
        //周期递增，到240个周期时还原
        cycleInterval = cycleInterval > 4 * 60 ? DefaultCycleInterval : cycleInterval * 2;

        List<StockInfo> stockInfos = stockInfoDao.selectList (new StockInfo ());
        for(StockInfo stockInfo : stockInfos){
            DailyTradeDetail param = new DailyTradeDetail ();
            param.setCode (stockInfo.getCode ());
            param.setDate (DateUtils.getDayStr(new Date ()));
            DailyTradeDetail dailyTradeDetail = dailyTradeDetailDao.selectOne (param);
            if(dailyTradeDetail!=null){
                double marketValue = stockInfo.getSc ()!=null
                        && dailyTradeDetail.getEndPrice ()!=null ?
                        stockInfo.getSc () * dailyTradeDetail.getEndPrice () : 0;
                dailyTradeDetailDao.udpateMarketValue (dailyTradeDetail.getCode (),
                        dailyTradeDetail.getDate (),marketValue,stockInfo.getSc ());
            }
        }
    }
}
