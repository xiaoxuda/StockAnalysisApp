package cn.orditech.schedule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 定时任务服务，时间单位以秒计算，最小单位60秒
 * Created by kimi on 2017/1/13.
 */
public class ScheduleTaskService {
    private static final Logger logger = LoggerFactory.getLogger (ScheduleTaskService.class);
    /**
     * 最小时间单位,秒
     */
    public static final long MIN_TIME_UNIT = 60L;

    /**
     * 上次定时调度时间
     */
    private static long lastScheduleTime = System.currentTimeMillis ();

    /**
     * 任务池
     */
    private static final LinkedList<ScheduleTaskEntity> taskPool =
            new LinkedList<ScheduleTaskEntity> ();


    /**
     * 任务执行器
     */
    private static final ThreadPoolExecutor executor = (ThreadPoolExecutor)
            Executors.newCachedThreadPool ();


    /**
     * 周期调度器，负责时间周期的控制，定期发起任务生产
     */
    static class Sceduler implements Runnable {

        @Override
        public void run () {
            while (true) {
                try {
                    new Thread (new TaskProductor ()).start ();

                    TimeUnit.SECONDS.sleep (MIN_TIME_UNIT);
                } catch (InterruptedException e) {
                    e.printStackTrace ();
                }
            }
        }
    }

    /**
     * 可执行任务生产者
     */
    static class TaskProductor implements Runnable {

        @Override
        public void run () {
            if (taskPool.size () == 0) {
                return;
            }
            //任务提取与插入互斥
            synchronized (taskPool) {
                long currentTime = System.currentTimeMillis ();
                //用当前时间查找到对应的插入位置即为符合当前调度时间的任务数量
                int targetTaskSize = getInsertIndex (currentTime, 0, taskPool.size () - 1);
                int num = 0;
                for (ScheduleTaskEntity entity : taskPool) {
                    if (num >= targetTaskSize) {
                        return;
                    }
                    if (entity.getTask ().isShutDown ()) {
                        ++num;
                        continue;
                    }
                    if (entity.getTask ().isCanceled ()) {
                        --targetTaskSize;
                        taskPool.remove (num);
                        continue;
                    }
                    executor.submit (entity.getTask ());
                    entity.setNextExecTime (nextExecTime (entity.getTask (), currentTime));
                    ++num;
                }
                lastScheduleTime = currentTime;
            }
        }
    }

    /**
     * 启动任务调度器
     */
    static {
        new Thread (new Sceduler ()).start ();
    }

    /**
     * 提交任务到任务池
     *
     * @param task
     */
    public static void commitTask (ScheduleTask task) {
        ScheduleTaskEntity taskEntity = new ScheduleTaskEntity (task);
        taskEntity.setNextExecTime (nextExecTime (task, System.currentTimeMillis ()));
        if (task.isExecNow ()) {
            executor.execute (task);
        }
        putToTaskPool (taskEntity);
    }

    /**
     * 将任务放入任务池，按照下次执行时间正向排序
     *
     * @param taskEntity
     */
    private static void putToTaskPool (ScheduleTaskEntity taskEntity) {
        synchronized (taskPool) {
            int index = getInsertIndex (taskEntity.getNextExecTime (), 0, taskPool.size () - 1);
            taskPool.add (index, taskEntity);
        }
    }

    /**
     * 获取任务执行时间正向排序的插入位置
     *
     * @param nextExecTime
     * @param start        起始索引
     * @param end          结束索引
     * @return
     */
    private static int getInsertIndex (long nextExecTime, int start, int end) {
        if (taskPool.size () == 0) {
            return 0;
        }
        if (taskPool.size () - 1 < end) {
            end = taskPool.size () - 1;
        }
        if (start > end) {
            return start;
        }
        int mid = (start + end) / 2;
        long midNextTime = taskPool.get (mid).getNextExecTime ();
        if (nextExecTime < midNextTime) {
            return getInsertIndex (nextExecTime, start, mid - 1);
        } else {
            return getInsertIndex (nextExecTime, mid + 1, end);
        }
    }

    /**
     * 计算下一个执行时间
     *
     * @param task
     * @return
     */
    private static long nextExecTime (ScheduleTask task, long currentTime) {
        return currentTime + task.cycleInterval () * MIN_TIME_UNIT * 1000;
    }

}
