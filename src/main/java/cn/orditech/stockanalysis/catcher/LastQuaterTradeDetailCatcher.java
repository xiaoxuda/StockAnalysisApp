/**
 *
 */
package cn.orditech.stockanalysis.catcher;

import cn.orditech.stockanalysis.catcher.enums.TaskTypeEnum;
import cn.orditech.stockanalysis.catcher.service.CatchTask;
import cn.orditech.stockanalysis.entity.DailyTradeDetail;
import cn.orditech.stockanalysis.entity.StockInfo;
import cn.orditech.stockanalysis.service.StockDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;

import java.util.*;

/**
 * @author kimi
 */
public class LastQuaterTradeDetailCatcher extends HistoryTradeDetailCatcher {

    /* (non-Javadoc)
     * @see BaseCatcher#getTaskType()
     */
    @Override
    public TaskTypeEnum getTaskType () {
        // TODO Auto-generated method stub
        return TaskTypeEnum.SINAJS_LAST_QUATER_TRADE_DETAIL;
    }

    @Override
    public CatchTask generateTask (StockInfo stockInfo) {
        //获取当前年份和季度
        Calendar calendar = Calendar.getInstance ();
        calendar.setTime (new Date ());
        int year = calendar.get (Calendar.YEAR);
        int quarter = (int) Math.ceil ((calendar.get (Calendar.MONTH) + 1) / 3.0);

        //获取上一个季度
        --quarter;
        //上年第四季度
        if (quarter == 0) {
            year -= 1;
            quarter = 4;
        }
        CatchTask task = new CatchTask ();
        task.setType (this.getTaskType ().getCode ());
        task.setUrl (
                String.format ("http://money.finance.sina.com.cn/corp/go.php/vMS_MarketHistory/stockid/%s.phtml?year=%s&jidu=%s"
                        , stockInfo.getCode (), year, quarter));
        task.addInfo ("code", stockInfo.getCode ());
        task.addInfo ("type", stockInfo.getType ());
        return task;
    }
}
