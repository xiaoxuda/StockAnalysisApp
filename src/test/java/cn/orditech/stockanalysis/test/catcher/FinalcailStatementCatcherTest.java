package cn.orditech.stockanalysis.test.catcher;

import cn.orditech.stockanalysis.catcher.CatchTask;
import cn.orditech.stockanalysis.catcher.catcher.impl.FinanceStatementCatcher;
import cn.orditech.stockanalysis.entity.StockInfo;
import cn.orditech.stockanalysis.test.BaseTest;

/**
 * @author xiaoxuda
 * @createTime 2019-12-09 10:17
 * @description
 */
public class FinalcailStatementCatcherTest extends BaseTest {
    public static void main(String[] args) {
        FinanceStatementCatcher catcher = new FinanceStatementCatcher();
        StockInfo info = new StockInfo();
        info.setCode("000001");
        info.setType("sz");
        CatchTask task = catcher.generateTask(info);
        catcher.catchAction(task, 2);
    }
}
