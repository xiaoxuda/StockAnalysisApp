package cn.orditech.stockanalysis.catcher.catcher;

import cn.orditech.schedule.ScheduleTask;
import cn.orditech.schedule.ScheduleTaskService;
import cn.orditech.stockanalysis.catcher.CatchTask;
import cn.orditech.stockanalysis.catcher.enums.TaskTypeEnum;
import cn.orditech.stockanalysis.catcher.service.CatcherRegisterCenter;
import cn.orditech.stockanalysis.catcher.service.TaskQueueService;
import cn.orditech.stockanalysis.entity.StockInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author kimi
 */
public abstract class BaseCatcher implements Catcher {
    /**
     * 为每个子类提供一个区别化的日志类
     **/
    protected final Logger LOGGER = LoggerFactory.getLogger (this.getClass ());

    /**
     * 事务
     **/
    @Autowired
    protected TransactionTemplate transactionTemplate;

    @Autowired
    private TaskQueueService taskQueueService;

    /**
     * 线程池，子类只能通过覆盖入口方法修改部分属性，核心默认4个守护线程
     **/
    private static final int TASK_CAPACITY = 1000;
    private static final int CPU_NUM = Runtime.getRuntime().availableProcessors();
    private static final ThreadPoolExecutor executor = new ThreadPoolExecutor (CPU_NUM, 40, 1,
            TimeUnit.MINUTES, new LinkedBlockingQueue<> (TASK_CAPACITY));

    /**异常重试次数*/
    private int againTime = 5;

    @PostConstruct
    public void init () {
        //将当前爬虫任务执行注册到定时调度器
        ScheduleTaskService.commitTask (new CatcherSchedulerTask ());
        //爬虫注册
        CatcherRegisterCenter.register(getTaskType(), this);
    }

    /**
     * 生成爬虫任务
     *
     * @param stockInfo
     * @return
     */
    @Override
    public abstract CatchTask generateTask (StockInfo stockInfo);

    /**
     * 返回任务类型，不能为空，需要爬虫具体实现
     *
     * @return 返回值不能为空
     * @author kimi
     */
    @Override
    public abstract TaskTypeEnum getTaskType ();

    /**
     * 爬虫任务执行,子类若有需要可以自行覆盖
     *
     * @param task         任务信息
     * @param nowAgainTime 失败重试次数
     * @return
     */
    @Override
    public String catchAction (CatchTask task, int nowAgainTime) {
        try {
            URL url = new URL (task.getUrl ());
            HttpURLConnection con = (HttpURLConnection) url.openConnection ();
            InputStream inputStream = con.getInputStream ();
            InputStreamReader inputStreamReader = new InputStreamReader (inputStream);
            BufferedReader reader = new BufferedReader (inputStreamReader);
            StringBuilder builder = new StringBuilder ();
            // 将html文档格式化为单行文本
            String line;
            while ((line = reader.readLine ()) != null) {
                builder.append (line);
            }
            this.extractAndPersistence (builder.toString (), task);
        } catch (Exception e) {
            LOGGER.error ("任务{}异常", task, e);
            // 异常重试
            if (nowAgainTime <= againTime) {
                catchAction (task, ++nowAgainTime);
            } else {
                LOGGER.info ("任务重试超过{}次，重新放回任务队列等待调度，参数{}", againTime, task);
            }
        }
        return null;
    }

    /**
     * 数据提取及持久化逻辑，需要爬虫具体实现
     *
     * @param src  数据文本
     * @param task 任务信息
     * @return
     */
    public abstract boolean extractAndPersistence (String src, CatchTask task);


    /**
     * 爬虫执行单元
     *
     * @author kimi
     */
    class CatcherRunnable implements Runnable {
        private CatchTask task;

        public CatcherRunnable (CatchTask task) {
            this.task = task;
        }

        @Override
        public void run () {
            catchAction (this.task, againTime);
        }


        public CatchTask getTask () {
            return task;
        }
    }

    /**
     * 爬虫定时任务
     */
    class CatcherSchedulerTask extends ScheduleTask {
        /**
         * 爬虫定时任务间隔
         */
        private final int defaultInterval = 1;
        private final int maxInterval = 20;
        private int interval = defaultInterval;

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
            while (true) {
                CatchTask task = taskQueueService.getTask (getTaskType ());
                if (task == null) {
                    interval = interval < maxInterval ? (interval + defaultInterval) : maxInterval;
                    LOGGER.info ("{}:没有爬取任务,爬虫定时任务延长执行时间, interval:{}", getTaskType (), interval);
                    break;
                } else {
                    //恢复时间间隔
                    if(interval > defaultInterval) {
                        interval = defaultInterval;
                    }
                    //当前处理器核心达到最大任务数量且由任务等待执行则停止添加任务，将当前任务放回队列的顶部
                    if (executor.getQueue ().size () >= TASK_CAPACITY && executor.getPoolSize () >= executor.getMaximumPoolSize ()) {
                        LOGGER.info ("爬虫任务队列已满，提交任务被拒绝,taskSize={},maxPoolSize={},poolSize={},queueSize={},completedTaskCount={}",
                                executor.getQueue ().size (),
                                executor.getMaximumPoolSize (),
                                executor.getPoolSize (),
                                executor.getQueue().size(),
                                executor.getCompletedTaskCount ());
                        taskQueueService.paybackTask (task);
                        break;
                    }
                    //提交任务
                    try {
                        executor.submit(new CatcherRunnable(task));
                    } catch (RejectedExecutionException ex){
                        LOGGER.info ("爬虫任务队列已满，提交任务被拒绝,taskSize={},maxPoolSize={},poolSize={},queueSize={},completedTaskCount={}",
                                executor.getQueue ().size (),
                                executor.getMaximumPoolSize (),
                                executor.getPoolSize (),
                                executor.getQueue().size(),
                                executor.getCompletedTaskCount ());
                        taskQueueService.paybackTask (task);
                    }
                }
            }
        }
    }
}
