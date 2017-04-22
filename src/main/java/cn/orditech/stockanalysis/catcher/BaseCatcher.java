package cn.orditech.stockanalysis.catcher;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.*;

import javax.annotation.PostConstruct;

import cn.orditech.schedule.ScheduleTask;
import cn.orditech.schedule.ScheduleTaskService;
import cn.orditech.stockanalysis.catcher.enums.TaskTypeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.support.TransactionTemplate;

import cn.orditech.stockanalysis.catcher.service.CatchTask;
import cn.orditech.stockanalysis.catcher.service.TaskQueueService;
import cn.orditech.stockanalysis.entity.StockInfo;

/*
 * @author kimi
 */
public abstract class BaseCatcher {
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
     * 线程池，子类只能通过覆盖入口方法修改部分属性，核心默认6个线程
     **/
    private static final ThreadPoolExecutor executor = new ThreadPoolExecutor (2, 20, 5,
            TimeUnit.MINUTES, new LinkedBlockingQueue<Runnable> ());

    private CatcherSchedulerTask catcherSchedulerTask;

    private int interval = 1;//爬虫定时任务间隔
    private int againTime = 5; // 异常重试次数

    @PostConstruct
    public void init () {
        catcherSchedulerTask = new CatcherSchedulerTask ();
        ScheduleTaskService.commitTask (catcherSchedulerTask);
    }

    /**
     * 生成爬虫任务
     *
     * @param stockInfo
     * @return
     */
    public abstract CatchTask generateTask (StockInfo stockInfo);

    /**
     * 数据提取及保存逻辑，需要爬虫具体实现
     *
     * @param src  数据文本
     * @param task 任务信息
     * @return
     */
    public abstract boolean extract (String src, CatchTask task);

    /**
     * 返回任务类型，不能为空，需要爬虫具体实现
     *
     * @return 返回值不能为空
     * @author kimi
     */
    public abstract TaskTypeEnum getTaskType ();


    public void startCatcher () {
        if(catcherSchedulerTask.isShutDown () || catcherSchedulerTask.isCanceled ()){
            catcherSchedulerTask.setShutDown (false);
            catcherSchedulerTask.setCanceled (false);
            ScheduleTaskService.commitTask (catcherSchedulerTask);
            LOGGER.info ("{}:爬虫定时任务重新唤起", getTaskType ());
        }
    }

    /**
     * 根据任务抓取远程数据
     *
     * @param task         任务信息
     * @param nowAgainTime 失败重试次数
     * @return
     */
    public String catchAction (CatchTask task, int nowAgainTime) {
        try {
            URL url = new URL (task.getUrl ());
            HttpURLConnection con = (HttpURLConnection) url.openConnection ();
            InputStream inputStream = con.getInputStream ();
            InputStreamReader inputStreamReader = new InputStreamReader (inputStream);
            BufferedReader reader = new BufferedReader (inputStreamReader);
            StringBuilder builder = new StringBuilder ();
            // 将html文档格式化为单行文本
            String line = null;
            while ((line = reader.readLine ()) != null) {
                builder.append (line);
            }
            extract (builder.toString (), task);
        } catch (Exception e) {
            // TODO Auto-generated catch block
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
     * 爬虫执行单元
     *
     * @author kimi
     */
    class CatcherRunable implements Runnable {
        private CatchTask task;

        public CatcherRunable (CatchTask task) {
            this.task = task;
        }

        public void run () {
            catchAction (this.task, againTime);
        }
    }

    /**
     * 爬虫定时任务
     */
    class CatcherSchedulerTask extends ScheduleTask {

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
                    LOGGER.info ("{}:没有爬取任务,爬虫定时任务退出", getTaskType ());
                    catcherSchedulerTask.setCanceled (true);
                    break;
                } else {
                    //当前任务数量大于处理核心的2倍停止添加任务，将当前任务放回队列的顶部
                    if (executor.getQueue ().size () >= executor.getMaximumPoolSize ()*2) {
                        LOGGER.info ("爬虫任务队列已满,taskSize={},maxPoolSize={},activePoolSize={}",
                                executor.getQueue ().size (),
                                executor.getMaximumPoolSize (),
                                executor.getActiveCount ());
                        taskQueueService.paybackTask (task);
                        break;
                    }
                    //提交任务
                    executor.submit (new CatcherRunable (task));
                }
            }
        }
    }
}
