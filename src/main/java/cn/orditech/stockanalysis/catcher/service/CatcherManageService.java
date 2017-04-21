/**
 *
 */
package cn.orditech.stockanalysis.catcher.service;

import cn.orditech.schedule.ScheduleTask;
import cn.orditech.schedule.ScheduleTaskService;
import cn.orditech.stockanalysis.catcher.BaseCatcher;
import cn.orditech.stockanalysis.catcher.enums.TaskTypeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 * 爬虫管理器
 *
 * @author kimi
 */
public class CatcherManageService implements ApplicationContextAware {

    public final Logger LOGGER = LoggerFactory.getLogger (CatcherManageService.class);

    @Autowired
    private TaskQueueService taskQueueService;

    //爬虫检测间隔1个定时任务周期
    private Integer interval = 1;

    private Map<TaskTypeEnum, BaseCatcher> catcherMap = new HashMap<TaskTypeEnum, BaseCatcher> ();

    private static CatcherCheckTask catcherCheckTask;

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

    @PostConstruct
    public void init () {
        //提交定时任务
        catcherCheckTask = new CatcherCheckTask ();
        ScheduleTaskService.commitTask (catcherCheckTask);
    }

    /**
     * 重启爬虫任务,暂时没有实现睡眠唤醒功能
     */
    public void catcherStateCheck () {
        for (TaskTypeEnum type : taskQueueService.getTypeSet ()) {
            startCatcher (type);
        }
    }

    /**
     * 启动已注册的选定key的爬虫
     *
     * @author kimi
     */
    public void startCatcher (TaskTypeEnum type) {
        BaseCatcher catcher = catcherMap.get (type);
        if (null != catcher && !catcher.isRunning ()) {
            LOGGER.info ("{}:爬虫重新启动", type);
            catcher.start ();
        }
    }

    //爬虫监控定时任务
    class CatcherCheckTask extends ScheduleTask {

        @Override
        public boolean isExecNow () {
            return false;
        }

        @Override
        public long cycleInterval () {
            return interval;
        }

        @Override
        public void run () {
            catcherStateCheck ();
        }
    }
}
