package cn.orditech.stockanalysis.test.task;

import cn.orditech.stockanalysis.task.PeCalculateTask;
import cn.orditech.stockanalysis.test.BaseTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author xiaoxuda
 * @createTime 2020-03-01 18:41
 * @description
 */
public class PeCalculateTaskTest extends BaseTest {
    @Autowired
    private PeCalculateTask peCalculateTask;

    @Test
    public void runTest(){
        peCalculateTask.run();
    }
}
