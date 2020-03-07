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
 * 抓取上市公司股票数量
 */
@Component
public class StockInfoDetailCatcher extends BaseCatcher {

    @Autowired
    private StockDataService stockDataService;

    @Override
    public TaskTypeEnum getTaskType () {
        return TaskTypeEnum.JUCAONET_COMPANY_SHARECAPITAL;
    }

    @Override
    public boolean extractAndPersistence (String src, CatchTask task) {
        if (StringUtils.isBlank(src) || !src.contains ("CapitalStockStructureDetail")) {
            LOGGER.error ("公司详细信息抓取失败,TaskType:{} param:{}", task.getType (), task);
            return false;
        }
        // 提取数据
        try {
            JSONObject json = JSONObject.parseObject(src);
            JSONObject detailJson = json.getJSONObject("CapitalStockStructureDetail");
            if(detailJson == null || detailJson.size() == 0 || detailJson.getDouble("zgb") == null){
                LOGGER.warn("股票股本信息不完整,taskUrl={}", task.getUrl());
                return false;
            }
            StockInfo stockInfo = new StockInfo();
            stockInfo.setCode(task.getInfo().get("code").toString());
            stockInfo.setSc((long)(detailJson.getDouble("zgb") * 10000));
            int cnt = stockDataService.siUpdateOrInsert(stockInfo, false);
            return cnt == 1;
        } catch (Exception e){
            LOGGER.warn("提取股票股本信息失败,taskUrl={}", task.getUrl());
        }
        return false;
    }

    @Override
    public CatchTask generateTask (StockInfo stockInfo) {
        CatchTask task = new CatchTask ();
        task.addInfo ("code", stockInfo.getCode ());
        task.addInfo ("type", stockInfo.getType ());
        task.setType (this.getTaskType ().getCode ());
        String type = null;
        if(stockInfo.getCode().startsWith("0") || stockInfo.getCode().startsWith("3")){
            type = "SZ";
        } else if(stockInfo.getCode().startsWith("6") || stockInfo.getCode().startsWith("9")){
            type = "SH";
        } else {
            LOGGER.warn("无法识别的股票类型，stockInfo={}", JSONObject.toJSONString(stockInfo));
        }
        task.setUrl ("http://f10.eastmoney.com/CapitalStockStructure/CapitalStockStructureAjax?code=" + type + stockInfo.getCode ());

        return task;
    }


}
