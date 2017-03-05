package cn.orditech.stockanalysis;

import cn.orditech.schedule.ScheduleTask;
import cn.orditech.schedule.ScheduleTaskService;

import java.util.concurrent.TimeUnit;

/**
 * Created by kimi on 2017/1/18.
 */
public class ScheduleTest extends BaseTest{
    public static void main(String args[]){
        ScheduleTask task = new ScheduleTask () {
            private int num;

            @Override
            public boolean isExecNow () {
                return true;
            }

            @Override
            public long cycleInterval () {
                return 1;
            }


            @Override
            public void run () {
                System.out.println(++num);
            }
        };

        ScheduleTaskService.commitTask (task);

        try {
            TimeUnit.SECONDS.sleep (20L);
            task.setShutDown (true);
            TimeUnit.SECONDS.sleep (20L);
            task.setShutDown (false);
            TimeUnit.SECONDS.sleep (10);
            System.exit (0);
        } catch (InterruptedException e) {
            e.printStackTrace ();
        }
    }
}
