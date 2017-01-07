package com.orditech.stockanalysis.catcher;

import org.springframework.beans.factory.annotation.Autowired;

import com.orditech.stockanalysis.catcher.enums.TaskTypeEnum;
import com.orditech.stockanalysis.catcher.service.CatchTask;
import com.orditech.stockanalysis.entity.StockInfo;
import com.orditech.stockanalysis.service.StockDataService;

/*
 * @author kimi
 * @see 抓取上市公司股票数量
 */
public class StockInfoDetailCatcher extends BaseCatcher {

    @Autowired
    private StockDataService stockDataService;

    @Override
    public TaskTypeEnum getTaskType () {
        return TaskTypeEnum.JUCAONET_COMPANY_SHARECAPITAL;
    }

    @Override
    public boolean extract (String src, CatchTask task) {
        if (src == null || src == "" || src.contains ("没有查询到数据！")) {
            LOGGER.error ("公司详细信息抓取失败,TaskType:{} param:{}", task.getType (), task);
            return false;
        }
        // 提取数据
        int start = src.indexOf ("zx_data2");
        if (start == -1) {
            LOGGER.error ("公司详细信息抓取失败,TaskType:{} param:{}", task.getType (), task);
            return false;
        }
        src = src.substring (start);
        start = src.indexOf (">");
        src = src.substring (start + 1);
        int end = src.indexOf ("<");
        src = src.substring (0, end);
        src = src.replaceAll ("[^0-9]", "");
        Long l = Long.valueOf (src.isEmpty () ? "0" : src);
        if (l.equals (0)) {
            LOGGER.error ("公司详细信息抓取失败,TaskType:{} param:{}", task.getType (), task);
            return false;
        }

        StockInfo stockInfo = new StockInfo ();
        stockInfo.setCode (task.getInfo ().get ("code").toString ());
        stockInfo.setSc (l);
        int cnt = stockDataService.siUpdateOrInsert (stockInfo, false);
        return cnt == 1;
    }

    @Override
    public CatchTask generateTask (StockInfo stockInfo) {
        CatchTask task = new CatchTask ();
        task.addInfo ("code", stockInfo.getCode ());
        task.addInfo ("type", stockInfo.getType ());
        task.setType (this.getTaskType ().getCode ());
        task.setUrl (
                "http://www.cninfo.com.cn/information/lastest/" + stockInfo.getType () + stockInfo.getCode () + ".html");

        return task;
    }

}
