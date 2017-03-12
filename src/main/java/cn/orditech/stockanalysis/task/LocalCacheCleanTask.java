package cn.orditech.stockanalysis.task;


import cn.orditech.schedule.ScheduleTask;
import cn.orditech.stockanalysis.tool.CacheTool;

import java.util.Calendar;

/**
 * 本地缓存清理
 * Created by kimi on 2017/3/12.
 */
public class LocalCacheCleanTask extends ScheduleTask {
    @Override
    public boolean isExecNow () {
        return false;
    }

    @Override
    public long cycleInterval () {
        return 5L;
    }

    @Override
    public void run () {
        Calendar calendar = Calendar.getInstance ();
        if ((calendar.get (Calendar.HOUR_OF_DAY) == 23 && calendar.get (Calendar.MINUTE) >= 57)
                || (calendar.get (Calendar.HOUR_OF_DAY) == 0 && calendar.get (Calendar.MINUTE) <= 2)) {
            CacheTool.cleanLoginIpCache ();
        }
    }
}
