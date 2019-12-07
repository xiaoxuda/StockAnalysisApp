package cn.orditech.stockanalysis.catcher.catcher.impl;

import cn.orditech.stockanalysis.catcher.CatchTask;
import cn.orditech.stockanalysis.catcher.catcher.BaseCatcher;
import cn.orditech.stockanalysis.catcher.enums.TaskTypeEnum;
import cn.orditech.stockanalysis.entity.FinancailStatement;
import cn.orditech.stockanalysis.entity.StockInfo;
import cn.orditech.stockanalysis.service.StockDataService;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 抓取上市公司财务报表
 * @author kimi
 */
@Component
public class FinancailStatementCatcher extends BaseCatcher {

    @Autowired
    private StockDataService stockDataService;

    @Override
    public TaskTypeEnum getTaskType () {
        return TaskTypeEnum.EASTMONEYNET_STATEMENT;
    }

    @Override
    public boolean extractAndPersistence (String src, CatchTask task) {
        return extract10jqka (src, task);
    }

    public double extractData (String s) {
        double result = 0;
        if (s == null || s.isEmpty () || s.contains ("--") || s.contains ("false")) {
            return result;
        }
        try {
            int index;
            if ((index = s.indexOf ("万亿")) > -1) {
                s = s.substring (0, index);
                result = Double.valueOf (s.isEmpty () ? "0" : s) * 1000000000000L;
            } else if ((index = s.indexOf ("亿")) > -1) {
                s = s.substring (0, index);
                result = Double.valueOf (s.isEmpty () ? "0" : s) * 100000000;
            } else if ((index = s.indexOf ("万")) > -1) {
                s = s.substring (0, index);
                result = Double.valueOf (s.isEmpty () ? "0" : s) * 10000;
            } else if ((index = s.indexOf("%")) > -1){
                s = s.substring (0, index);
                result = Double.valueOf (s.isEmpty () ? "0" : s);
            } else {
                result = Double.valueOf (s.isEmpty () ? "0" : s);
            }
        } catch (Exception e) {
            e.printStackTrace ();
            LOGGER.error ("【{}】解析异常", s, e);
        }
        return result;
    }

    private boolean extract10jqka(String src, CatchTask task){
        if(StringUtils.isBlank (src)){
            LOGGER.error ("TaskType:{} param:{},抓取财务报表失败！", task.getType (), task);
            return false;
        }
        int startIndex = src.indexOf ("<p id=\"main\">");
        if(startIndex == -1){
            LOGGER.error ("TaskType:{} param:{},抓取财务报表失败！", task.getType (), task);
            return false;
        }
        src = src.substring (startIndex);
        int endIndex = src.indexOf("</p>");
        if(startIndex == -1 || endIndex == -1){
            LOGGER.error ("TaskType:{} param:{},抓取财务报表失败！", task.getType (), task);
            return false;
        }
        String json = src.substring ("<p id=\"main\">".length (), endIndex);
        if(StringUtils.isBlank (json)){
            LOGGER.error ("TaskType:{} param:{},抓取财务报表失败！", task.getType (), task);
            return false;
        }
        JSONObject jsonObject = JSONObject.parseObject (json);
        JSONArray content = jsonObject.getJSONArray ("report");
        createOrUpdateJqka(content, task);
        return false;
    }

    public boolean createOrUpdateJqka (JSONArray list, CatchTask task) {
        List<String[]> aList = new ArrayList<String[]> ();
        int num = list.size ();
        for (int i = 0;i< num;i++) {
            JSONArray jsonArray = list.getJSONArray (i);
            int len = jsonArray.size ();
            String[] valueArr = new String[len];
            for(int j=0;j<len;j++){
                valueArr[j] = (jsonArray.getString (j));
            }
            aList.add (valueArr);
        }
        int size = aList.get (0).length;
        for (int i = 0; i < size; i++) {
            try {
                FinancailStatement financailStatement = new FinancailStatement();
                financailStatement.setCode((String) task.getInfoValue("code"));
                financailStatement.setDate(aList.get(0)[i]);
                financailStatement.setPe(extractData(aList.get(1)[i]));
                financailStatement.setMp(extractData(aList.get(2)[i]));
                financailStatement.setOpgr(extractData(aList.get(3)[i]));
                financailStatement.setMpbpc(extractData(aList.get(4)[i]));
                financailStatement.setToi(extractData(aList.get(6)[i]));
                financailStatement.setBvps(extractData(aList.get(8)[i]));
                financailStatement.setRoe(extractData(aList.get(9)[i]));
                financailStatement.setDtar(extractData(aList.get(11)[i]));
                financailStatement.setCps(extractData(aList.get(14)[i]));
                financailStatement.setSgpr(extractData(aList.get(15)[i]));

                stockDataService.fsUpdateOrInsert(financailStatement);
            }catch (Exception e){
                LOGGER.warn("createOrUpdateJqka fail code={},date={}", task.getInfoValue("code"), aList.get(0)[i], e);
                continue;
            }
        }
        return true;
    }

    @Override
    public CatchTask generateTask (StockInfo stockInfo) {
        CatchTask task = new CatchTask ();
        task.setType (this.getTaskType ().getCode ());
        task.setUrl("http://basic.10jqka.com.cn/" + stockInfo.getCode () + "/finance.html");
        task.addInfo ("code", stockInfo.getCode ());
        task.addInfo ("type", stockInfo.getType ());
        return task;
    }

}
