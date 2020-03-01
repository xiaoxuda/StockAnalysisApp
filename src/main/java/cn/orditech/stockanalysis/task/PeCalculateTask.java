package cn.orditech.stockanalysis.task;

import cn.orditech.schedule.ScheduleTask;
import cn.orditech.schedule.ScheduleTaskService;
import cn.orditech.stockanalysis.dao.DailyTradeDetailDao;
import cn.orditech.stockanalysis.dao.FinancailStatementDao;
import cn.orditech.stockanalysis.entity.DailyTradeDetail;
import cn.orditech.stockanalysis.entity.FinancailStatement;
import cn.orditech.tools.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.List;

/**
 * 市盈利率计算任务
 * @author Created by kimi on 2017/1/18.
 */
@Component
public class PeCalculateTask extends ScheduleTask {
    @Autowired
    private DailyTradeDetailDao dailyTradeDetailDao;
    @Autowired
    private FinancailStatementDao financailStatementDao;

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

        Long minId = 1L;
        Date today = new Date();
        Integer pageSize = 100;
        List<DailyTradeDetail> tradeList;
        String todayStr = DateUtils.getDayStr(today);
        FinancailStatement finance = new FinancailStatement();
        do {
            tradeList = dailyTradeDetailDao.pageFindByDateOrderById(todayStr, minId, pageSize);
            if(tradeList.size() > 0){
                for(DailyTradeDetail detail : tradeList){
                    finance.setCode(detail.getCode());
                    finance.setDate(DateUtils.getQuarterFinanceReportDate(1));
                    FinancailStatement lastQuarterFinance = financailStatementDao.selectOne(finance);
                    if(lastQuarterFinance == null){
                        finance.setDate(DateUtils.getQuarterFinanceReportDate(2));
                        lastQuarterFinance = financailStatementDao.selectOne(finance);
                    }
                    finance.setDate(DateUtils.getYearFinanceReportDate(1));
                    FinancailStatement lastYearFinance = financailStatementDao.selectOne(finance);
                    if(lastYearFinance == null){
                        finance.setDate(DateUtils.getYearFinanceReportDate(2));
                        lastYearFinance = financailStatementDao.selectOne(finance);
                    }

                    DailyTradeDetail update = new DailyTradeDetail();
                    update.setCode(detail.getCode());
                    update.setDate(detail.getDate());
                    update.setPeStatic(calculateStaticPe(detail, lastYearFinance));
                    update.setPeDynamic(calculateDynamicPe(detail, lastQuarterFinance, lastYearFinance));
                    if(update.getPeDynamic() != null || update.getPeStatic() != null) {
                        dailyTradeDetailDao.updateSelective(update);
                    }
                }
                minId = tradeList.get(tradeList.size() - 1).getId() + 1;
            }
        } while (tradeList.size() == pageSize);
    }

    private Float calculateStaticPe(DailyTradeDetail detail, FinancailStatement lastYearFinance){
        if(detail.getMarketValue() != null && lastYearFinance != null && lastYearFinance.getMpbpc() != null){
            return (float)(detail.getMarketValue() / lastYearFinance.getMpbpc());
        }
        return null;
    }

    private Float calculateDynamicPe(DailyTradeDetail detail, FinancailStatement lastQuarterFinance,
                                     FinancailStatement lastYearFinance){
        if(detail.getMarketValue() != null && lastYearFinance != null && lastYearFinance.getMpbpc() != null
                && lastQuarterFinance != null && lastQuarterFinance.getOpgr() != null){
            return (float)(detail.getMarketValue() / (lastYearFinance.getMpbpc() * (1 + lastQuarterFinance.getOpgr()/100.0)));
        }
        return null;
    }
}
