package com.orditech.stockanalysis.catcher;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import com.orditech.stockanalysis.catcher.enums.TaskTypeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.support.TransactionTemplate;

import com.orditech.stockanalysis.catcher.service.CatchTask;
import com.orditech.stockanalysis.catcher.service.TaskQueueService;
import com.orditech.stockanalysis.entity.StockInfo;

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
    private final ThreadPoolExecutor executor =
            (ThreadPoolExecutor) Executors.newFixedThreadPool (6);

    private boolean isRunning = false; // 标识任务正在运行
    private int defaultWaitTime = 1000; // 默认抓取间隔
    private int maxWaitTime = 2 * 60 * 1000; // 最大抓取间隔2分钟
    private int waitTime = 1000; // 当前抓取间隔
    private int waitMultiplier = 3;// 抓取时间乘数
    private int againTime = 5; // 异常重试次数

    /**
     * 子类可以通过重载此方法调用customExecutor来定制化工作线程池
     */
    public void customExecutor () {
    }

    /**
     * 定制线程池,不允许重载,不需要设置的参数可设为null
     *
     * @param corePoolSize
     * @param handler
     */
    protected final void customExecutor (Integer corePoolSize, RejectedExecutionHandler handler) {

        if (null != corePoolSize) {
            this.executor.setCorePoolSize (corePoolSize);
            this.executor.setMaximumPoolSize (corePoolSize);
        }

        if (null != handler) {
            this.executor.setRejectedExecutionHandler (handler);
        }
    }

    /**
     * 初始化属性
     */
    @PostConstruct
    public void init () {
        executor.setKeepAliveTime (5 * 60 * 1000L, TimeUnit.SECONDS);
        executor.allowCoreThreadTimeOut (true);
        //定制化扩展钩子
        this.customExecutor ();
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

    /**
     * 判断爬虫是否处于运行中
     *
     * @return
     */
    public boolean isRunning () {
        return this.isRunning;
    }

    /**
     * 启动爬虫,定时请求数据抓取任务
     */
    public void start () {
        if (this.isRunning) {
            return;
        }
        Thread thread_catcher = new Thread (getTaskType () + "_Catcher") {
            public void run () {
                while (true) {
                    CatchTask task = taskQueueService.getTask (getTaskType ());
                    try {
                        if (task == null) {// 当前没有任务，将抓取间隔调高waitMultiplier倍让出CPU资源
                            LOGGER.info ("{}:no task", getTaskType ());

                            if (waitTime == maxWaitTime) {
                                LOGGER.info ("{}等待任务时间超时，爬虫退出，等待重新唤起。", getTaskType ());
                                break;
                            }
                            waitTime *= waitMultiplier;
                            waitTime = waitTime > maxWaitTime ? maxWaitTime : waitTime;
                        } else {
                            // 当前任务列表有任务，恢复抓取间隔为默认值
                            waitTime = defaultWaitTime;
                            //提交任务
                            executor.submit (new CatcherRunable (task));
                            //当前任务数量没有达到最大核心数，继续添加任务
                            if (executor.getActiveCount () < executor.getCorePoolSize ()) {
                                continue;
                            }
                        }
                        Thread.sleep (waitTime);
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        LOGGER.error ("任务{}异常", task, e);
                    }
                }
                isRunning = false;
            }
        };
        LOGGER.info ("{}:start", getTaskType () + "_Catcher");
        thread_catcher.start ();
        this.isRunning = true;
    }

    /**
     * 根据任务抓取远程数据
     *
     * @param task      任务信息
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
     * @return the transactionTemplate
     */
    public TransactionTemplate getTransactionTemplate () {
        return transactionTemplate;
    }

    /**
     * @param transactionTemplate the transactionTemplate to set
     */
    public void setTransactionTemplate (TransactionTemplate transactionTemplate) {
        this.transactionTemplate = transactionTemplate;
    }


}
