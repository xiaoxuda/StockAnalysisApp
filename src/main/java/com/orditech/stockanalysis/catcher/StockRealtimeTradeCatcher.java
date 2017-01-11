/**
 *
 */
package com.orditech.stockanalysis.catcher;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.orditech.stockanalysis.catcher.enums.TaskTypeEnum;
import org.apache.commons.lang.StringUtils;
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
public class StockRealtimeTradeCatcher extends BaseCatcher {

    @Autowired
    private StockDataService stockDataService;

    @Override
    public TaskTypeEnum getTaskType () {
        return TaskTypeEnum.SINAJS_PRICE;
    }

    /**
     * 抓取股票当天交易信息
     *
     * @author kimi
     */
    @Override
    public boolean extract (String src, final CatchTask task) {
        if (StringUtils.isBlank (src)) {
            LOGGER.error ("{}:{},返回结果为空", task.getType (), task);
            return false;
        }

        final String arr[] = src.split (",");

        if (arr.length < 4) {
            LOGGER.error ("{}:{},抓取股票当天交易信息获取失败", task.getType (), task);
            return false;
        }

        transactionTemplate.execute (new TransactionCallbackWithoutResult () {
            @Override
            protected void doInTransactionWithoutResult (TransactionStatus arg0) {
                // 今日有交易则更新交易信息
                if (StringUtils.isNotBlank (arr[3]) && Float.valueOf (arr[3]) > 0) {
                    float price = Float.valueOf (arr[3]);

                    //更新股票当前价格信息
                    StockInfo stockInfo = new StockInfo ();
                    stockInfo.setCode (task.getInfoValue ("code").toString ());
                    stockInfo.setPrice (price);
                    stockDataService.siUpdateOrInsert (stockInfo, false);

                    //保存今日交易信息
                    DailyTradeDetail dtd = new DailyTradeDetail ();
                    dtd.setCode (task.getInfoValue ("code").toString ());
                    dtd.setDate (new SimpleDateFormat ("yyyy-MM-dd").format (new Date ()));
                    dtd.setStartPrice (Float.valueOf (arr[1]));
                    dtd.setLastEndPrice (Float.valueOf (arr[2]));
                    dtd.setEndPrice (price);
                    dtd.setMaxPrice (Float.valueOf (arr[4]));
                    dtd.setMinPrice (Float.valueOf (arr[5]));
                    dtd.setTradeVolume (Long.valueOf (arr[8]));
                    dtd.setTradeAmt (Double.valueOf (arr[9]));
                    stockDataService.dtdUpdateOrInsert (dtd);
                }
            }
        });
        return true;
    }

    @Override
    public CatchTask generateTask (StockInfo stockInfo) {
        CatchTask task = new CatchTask ();
        task.addInfo ("code", stockInfo.getCode ());
        task.addInfo ("type", stockInfo.getType ());
        task.setType (this.getTaskType ().getCode ());
        task.setUrl ("http://hq.sinajs.cn/list=" + stockInfo.getType ().substring (0, 2) + stockInfo.getCode ());
        return task;
    }
}
