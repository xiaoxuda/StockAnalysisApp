package cn.orditech.schedule;

/**
 * Created by kimi on 2017/1/14.
 */
class ScheduleTaskEntity {
    /**
     * 任务
     */
    private ScheduleTask task;

    /**
     * 下个执行时间点,由调度服务设置
     */
    private long nextExecTime;

    /**
     * 屏蔽默认构造函数
     */
    private ScheduleTaskEntity(){}

    ScheduleTaskEntity (ScheduleTask task) {
        this.task = task;
    }

    ScheduleTask getTask () {
        return task;
    }

    void setTask (ScheduleTask task) {
        this.task = task;
    }

    long getNextExecTime () {
        return nextExecTime;
    }

    void setNextExecTime (long nextExecTime) {
        this.nextExecTime = nextExecTime;
    }
}
