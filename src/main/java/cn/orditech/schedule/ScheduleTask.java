package cn.orditech.schedule;

/**
 * 定时任务
 * Created by kimi on 2017/1/13.
 */
public abstract class ScheduleTask implements Runnable{
    /**
     * 是否已取消
     */
    private boolean isCanceled = false;
    /**
     * 是否暂停
     */
    private boolean isShutDown = false;
    /**
     * 提交后马上执行
     * @return
     */
    public abstract boolean isExecNow();

    /**
     * 执行的时间单位间隔，时间单位指定时任务服务的时间单位
     * @return
     */
    public abstract long cycleInterval();

    /**
     * 是否暂停任务
     */
    public boolean isShutDown(){
        return isShutDown;
    }

    /**
     * 是否取消任务
     * @return
     */
    public boolean isCanceled(){
        return isCanceled;
    }

    @Override
    public abstract void run();

    public void setCanceled (boolean canceled) {
        isCanceled = canceled;
    }

    public void setShutDown (boolean shutDown) {
        isShutDown = shutDown;
    }
}

