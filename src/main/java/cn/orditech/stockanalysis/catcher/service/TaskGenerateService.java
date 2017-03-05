/**
 *
 */
package cn.orditech.stockanalysis.catcher.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import cn.orditech.stockanalysis.catcher.BaseCatcher;
import cn.orditech.stockanalysis.catcher.enums.TaskTypeEnum;
import cn.orditech.stockanalysis.dao.StockInfoDao;
import cn.orditech.stockanalysis.entity.StockInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * 任务生成器，按照不同任务的时间策略定时生成数据抓取任务
 *
 * @author kimi
 */
public class TaskGenerateService implements ApplicationContextAware {
    private final Logger LOGGER = LoggerFactory.getLogger (TaskGenerateService.class);

    @Autowired
    private StockInfoDao stockInfoDao;

    @Autowired
    private TaskQueueService taskQueueService;

    /**
     * 生成任务执行开关
     **/
    private boolean isContinue = true;

    /**
     * 任务生成器是否在运行
     */
    private boolean isRunning = false;

    /**
     * 调度周期 ，毫秒
     **/
    private long time_gap = 5 * 60 * 1000L;

    /**
     * 上次调度时间
     **/
    private Map<TaskTypeEnum, Date> scheduleMap = new HashMap<TaskTypeEnum, Date> ();

    /**
     * 注册爬虫
     **/
    private Map<TaskTypeEnum, BaseCatcher> catcherMap = new HashMap<TaskTypeEnum, BaseCatcher> ();

    @PostConstruct
    public void init () {
        // FIXME 考虑将调度时间落入数据库，启动时从数据库读取
    }

    /**
     * 自动注册已设置的爬虫
     */
    @Override
    public void setApplicationContext (ApplicationContext applicationContext) throws BeansException {
        // TODO Auto-generated method stub
        Map<String, BaseCatcher> beansMap = applicationContext.getBeansOfType (BaseCatcher.class);
        if (beansMap != null && beansMap.size () > 0) {
            for (BaseCatcher catcher : beansMap.values ()) {
                catcherMap.put (catcher.getTaskType (), catcher);
            }
        }
    }

    /**
     * 启动生成器
     **/
    public void startGenerator () {

        if (isRunning == true) {
            return;
        }

        Thread thread = new Thread ("taskGenerate") {
            @Override
            public void run () {
                while (true) {
                    if (isContinue) {
                        commitCatchTaskAll();
                    }

                    try {
                        Thread.sleep (time_gap);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        LOGGER.error ("taskGenerate,异常信息{}", e.getMessage ());
                    }

                }
            }
        };
        thread.start ();
        isRunning = true;
    }

    public void reStartGenerator () {
        this.isContinue = true;
    }

    public void closeGenerator () {
        this.isContinue = false;
    }

    /**
     * 提交爬虫任务
     *
     * @param typeEnum
     * @param stockInfo
     */
    public void commitCatchTask (TaskTypeEnum typeEnum, StockInfo stockInfo) {

        CatchTask task = catcherMap.get (typeEnum).generateTask (stockInfo);

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
        }

        // 若没有查询到股票信息则清除调度信息
        List<StockInfo> taskInfoList = stockInfoDao.selectList (new StockInfo ());
        if (taskInfoList == null || taskInfoList.size () == 0) {
            return;
        }
        for (Map.Entry<TaskTypeEnum, BaseCatcher> entry : catcherMap.entrySet ()) {
            // 跳过股票列表爬虫
            if (TaskTypeEnum.JUCAONET_COMPANY_LIST.equals (entry.getKey ())) {
                continue;
            }

            if (refreshCycle (entry.getKey ())) {
                for (StockInfo stockInfo : taskInfoList) {
                    commitCatchTask (entry.getKey (), stockInfo);
                }
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
        scheduleMap.put (typeEnum, new Date ());
        return true;
    }

    /**
     * @return the catcherMap
     */
    public Map<TaskTypeEnum, BaseCatcher> getCatcherMap () {
        return catcherMap;
    }

}
