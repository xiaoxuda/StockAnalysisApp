/**
 *
 */
package cn.orditech.stockanalysis.catcher.service;

import cn.orditech.schedule.ScheduleTask;
import cn.orditech.schedule.ScheduleTaskService;
import cn.orditech.stockanalysis.catcher.CatchTask;
import cn.orditech.stockanalysis.catcher.catcher.Catcher;
import cn.orditech.stockanalysis.catcher.enums.TaskTypeEnum;
import cn.orditech.stockanalysis.dao.StockInfoDao;
import cn.orditech.stockanalysis.entity.StockInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 任务生成器，按照不同任务的时间策略定时生成数据抓取任务
 *
 * @author kimi
 */
@Service
public class TaskGenerator {
    private final Logger LOGGER = LoggerFactory.getLogger (TaskGenerator.class);

    @Autowired
    private StockInfoDao stockInfoDao;
    @Autowired
    private TaskQueueService taskQueueService;

    /**
     * 默认调度周期间隔
     */
    private static int DefaultCycleInterval = 5;

    /**
     * 上次调度时间
     **/
    private Map<TaskTypeEnum, Long> scheduleMap = new ConcurrentHashMap<>();

    private ScheduleGenerateTask scheduleGenerateTask;

    @PostConstruct
    public void init () {
        //启动时将自己注册到定时调度器
        scheduleGenerateTask = new ScheduleGenerateTask ();
        ScheduleTaskService.commitTask (scheduleGenerateTask);
    }

    /**
     * 提交爬虫任务
     *
     * @param typeEnum
     * @param stockInfo
     */
    private void commitCatchTask (TaskTypeEnum typeEnum, StockInfo stockInfo) {

        CatchTask task = CatcherRegisterCenter.getByType (typeEnum).generateTask (stockInfo);

        taskQueueService.commitTask (typeEnum, task);
    }

    /**
     * 提交选定类型的任务，可以打破当前任务周期计划
     *
     * @param typeEnum 任务类型
     * @param followCycle 是否遵循调度规则
     */
    public void commitCatchTask (TaskTypeEnum typeEnum, boolean followCycle) {
        // 周期判断
        if (followCycle && !typeEnum.isInNextCycle (scheduleMap.get (typeEnum))) {
            return;
        }

        // 生成股票列表任务
        if (TaskTypeEnum.JUCAONET_COMPANY_LIST.equals (typeEnum)) {
            commitCatchTask (typeEnum, null);
            refreshCycle (typeEnum);
            return;
        }

        // 若没有查询到股票信息则清除调度信息
        List<StockInfo> taskInfoList = stockInfoDao.selectList (new StockInfo ());
        if (taskInfoList == null || taskInfoList.size () == 0) {
            return;
        }
        for (StockInfo stockInfo : taskInfoList) {
            commitCatchTask (typeEnum, stockInfo);
        }
        //刷新周期
        refreshCycle (typeEnum);
    }

    /**
     * 生成爬虫任务,上市公司列表的任务除外
     */
    public void commitCatchTaskAll () {
        // 生成股票列表抓取任务
        if (refreshCycle (TaskTypeEnum.JUCAONET_COMPANY_LIST)) {
            commitCatchTask (TaskTypeEnum.JUCAONET_COMPANY_LIST, null);
            LOGGER.info("提交{}任务",TaskTypeEnum.JUCAONET_COMPANY_LIST.getDesc ());
        }

        // 若没有查询到股票信息则清除调度信息
        List<StockInfo> taskInfoList = stockInfoDao.selectList (new StockInfo ());
        if (taskInfoList == null || taskInfoList.size () == 0) {
            return;
        }
        for (Catcher catcher : CatcherRegisterCenter.getRegisterCatcher()) {
            TaskTypeEnum type = catcher.getTaskType();
            // 跳过股票列表爬虫
            if (TaskTypeEnum.JUCAONET_COMPANY_LIST.equals (type)) {
                continue;
            }

            if (refreshCycle (type)) {
                for (StockInfo stockInfo : taskInfoList) {
                    commitCatchTask (type, stockInfo);
                }
                LOGGER.info("提交{}任务",type.getDesc ());
            }
        }
    }

    /**
     * 若在下一个调度周期内则刷新调度时间，若不在当前调度周期内则返回false
     *
     * @return
     */
    private boolean refreshCycle (TaskTypeEnum typeEnum) {
        // 判断是否在调度周期内
        if (!typeEnum.isInNextCycle (scheduleMap.get (typeEnum))) {
            return false;
        }
        // 更新调度时间
        scheduleMap.put (typeEnum, System.currentTimeMillis ());
        return true;
    }

    /**
     * 定时生成任务
     */
    class ScheduleGenerateTask extends ScheduleTask {

        @Override
        public boolean isExecNow () {
            return false;
        }

        @Override
        public long cycleInterval () {
            return DefaultCycleInterval;
        }

        @Override
        public void run () {
            commitCatchTaskAll ();
        }
    }
}
