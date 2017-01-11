/**
 *
 */
package com.orditech.stockanalysis.catcher;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.orditech.stockanalysis.catcher.enums.TaskTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;

import com.orditech.stockanalysis.catcher.service.CatchTask;
import com.orditech.stockanalysis.entity.DailyTradeDetail;
import com.orditech.stockanalysis.entity.StockInfo;
import com.orditech.stockanalysis.service.StockDataService;

/**
 * @author kimi
 */
public class HistoryTradeDetailCatcher extends BaseCatcher {

    @Autowired
    private StockDataService stockDataService;

    @Override
    public boolean extract (String src, CatchTask task) {
        if (src == null || src == "" || src.contains ("该品种暂无此项记录!")) {
            LOGGER.error ("TaskType:{} param:{},抓取每日交易历史记录失败！", task.getType (), task);
            return false;
        }
        int start = src.indexOf ("<table id=\"FundHoldSharesTable\">");
        int end = src.indexOf ("</table>", start);
        if (start == -1 || end == -1) {
            LOGGER.error ("TaskType:{} param:{},抓取每日交易历史记录失败！", task.getType (), task);
            return false;
        }
        String table = src.substring (start, end);
        start = table.indexOf ("<tr class=\"tr_2\">");
        table = table.substring (start);
        table = table.replaceAll ("<a[^>]*>|</a>|<div[^>]*>|</div>| |	", "");
        table = table.replaceAll ("</td><td[^>]*>", ",");
        table = table.replaceAll ("</td>|<td[^>]*>", "");
        table = table.replaceAll ("</tr><tr[^>]*>", ";");
        table = table.replaceAll ("</tr>|<tr[^>]*>", "");
        table = table.replaceAll ("--", "0");
        List<String> trs = new ArrayList<String> (Arrays.asList (table.split (";")));
        for (int i = 0; i < trs.size (); ) {
            String temp = trs.get (i);
            if (temp.contains ("strong")) {
                trs.remove (i);
                continue;
            }
            ++i;
        }
        return createOrUpdate (trs, task);
    }

    public boolean createOrUpdate (final List<String> list, final CatchTask task) {
        if (list == null || list.isEmpty ()) {
            return false;
        }

        transactionTemplate.execute (new TransactionCallbackWithoutResult () {
            @Override
            protected void doInTransactionWithoutResult (TransactionStatus arg0) {
                for (String src : list) {
                    String ds[] = src.split (",");
                    if (ds.length < 7) {
                        continue;
                    }
                    DailyTradeDetail dtd = new DailyTradeDetail ();
                    dtd.setCode (task.getInfoValue ("code").toString ());
                    dtd.setDate (ds[0]);
                    dtd.setStartPrice (Float.valueOf (ds[1]));
                    dtd.setMaxPrice (Float.valueOf (ds[2]));
                    dtd.setEndPrice (Float.valueOf (ds[3]));
                    dtd.setMinPrice (Float.valueOf (ds[4]));
                    dtd.setTradeVolume (Long.valueOf (ds[5]));
                    dtd.setTradeAmt (Double.valueOf (ds[6]));
                    stockDataService.dtdUpdateOrInsert (dtd);
                }
            }
        });
        return true;
    }

    /* (non-Javadoc)
     * @see com.orditech.stockanalysis.catcher.BaseCatcher#getTaskType()
     */
    @Override
    public TaskTypeEnum getTaskType () {
        // TODO Auto-generated method stub
        return TaskTypeEnum.SINAJS_HISTORY_TRADE_DETAIL;
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
