package cn.orditech.stockanalysis.catcher.catcher.impl;

import cn.orditech.stockanalysis.catcher.CatchTask;
import cn.orditech.stockanalysis.catcher.catcher.BaseCatcher;
import cn.orditech.stockanalysis.catcher.enums.TaskTypeEnum;
import cn.orditech.stockanalysis.entity.StockInfo;
import cn.orditech.stockanalysis.service.StockDataService;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author kimi
 * 抓取股票代码与名称、股票类型
 */
@Component
public class StockInfoCatcher extends BaseCatcher {

    @Autowired
    private StockDataService stockDataService;

    @Override
    public TaskTypeEnum getTaskType () {
        return TaskTypeEnum.JUCAONET_COMPANY_LIST;
    }

    @Override
    public boolean extractAndPersistence (String src, CatchTask task) {
        if (StringUtils.isBlank(src) || !src.contains("SecurityShortName")) {
            return false;
        }
        JSONObject json = JSONObject.parseObject(src);
        String code = json.getString("SecurityCode");
        String codeName = json.getString("SecurityShortName");
        String market = json.getString("Market");

        if (StringUtils.isBlank(code) || StringUtils.isBlank(codeName)) {
            return false;
        }

        StockInfo si = new StockInfo();
        si.setCode(code);
        si.setName(codeName);
        si.setType(market);
        stockDataService.siUpdateOrInsert(si);
        return true;
    }

    @Override
    public CatchTask generateTask (StockInfo stockInfo) {
        CatchTask task = new CatchTask ();
        task.setUrl ("http://f10.eastmoney.com/PC_HSF10/CompanySurvey/CompanySurveyAjax?code=" + stockInfo.getType() + stockInfo.getCode());
        task.setType (this.getTaskType ().getCode ());
        return task;
    }

}
