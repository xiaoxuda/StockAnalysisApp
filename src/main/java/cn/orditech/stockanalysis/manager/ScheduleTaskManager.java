package cn.orditech.stockanalysis.manager;

import cn.orditech.schedule.ScheduleTaskService;
import cn.orditech.stockanalysis.task.LocalCacheCleanTask;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * Created by kimi on 2017/3/12.
 */
@Service
public class ScheduleTaskManager {
    @PostConstruct
    public void init(){
        ScheduleTaskService.commitTask (new LocalCacheCleanTask ());
    }
}
