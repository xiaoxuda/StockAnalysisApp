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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 抓取上市公司财务报表
 * @author kimi
 */
@Component
public class FinanceStatementCatcher extends BaseCatcher {

    private static final Logger LOGGER = LoggerFactory.getLogger (FinanceStatementCatcher.class);

    @Autowired
    private StockDataService stockDataService;

    private ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 2, 1,
            TimeUnit.MINUTES, new LinkedBlockingQueue<>(), r -> new FinanceStatementThread(r));

    public static class FinanceStatementThread extends Thread{
        private Runnable target;
        private String name;
        private static int threadInitNumber;

        private static synchronized int nextThreadNum() {
            return threadInitNumber++;
        }

        public FinanceStatementThread(Runnable r){
            this.target = r;
            this.name = "FinanceStatementThread-" + nextThreadNum();
        }

        @Override
        public void run(){
            if(this.target != null){
                try {
                    //抓去任务容易被限流，休眠1s中执行，降低qps
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    LOGGER.info("FinanceStatementThread sleep fail", e);
                }
                this.target.run();
            }
        }
    }

    @Override
    protected ThreadPoolExecutor getExecutor(){
        return this.executor;
    }

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
        JSONArray title = jsonObject.getJSONArray("title");
        createOrUpdateJqka(title, content, task);
        return false;
    }

    public boolean createOrUpdateJqka (JSONArray titleList, JSONArray contentList, CatchTask task) {
        List<String[]> valList = new ArrayList<> ();
        Map<String, Integer> idxMap = new HashMap<>();
        int num = contentList.size ();
        for (int i = 1;i< num;i++) {
            idxMap.put(titleList.getJSONArray(i).getString(0), i);
        }
        for (int i = 0;i< num;i++) {
            JSONArray jsonArray = contentList.getJSONArray (i);
            int len = jsonArray.size ();
            String[] valueArr = new String[len];
            for(int j=0;j<len;j++){
                valueArr[j] = (jsonArray.getString (j));
            }
            valList.add (valueArr);
        }
        int size = valList.get (0).length;
        for (int i = 0; i < size; i++) {
            try {
                FinancailStatement financailStatement = new FinancailStatement();
                financailStatement.setCode((String) task.getInfoValue("code"));
                financailStatement.setDate(valList.get(0)[i]);
                if(idxMap.get("净利润") != null) {
                    financailStatement.setMp(extractData(valList.get(idxMap.get("净利润"))[i]));
                }
                if(idxMap.get("净利润同比增长率") != null) {
                    financailStatement.setOpgr(extractData(valList.get(idxMap.get("净利润同比增长率"))[i]));
                }
                if(idxMap.get("扣非净利润") != null) {
                    financailStatement.setMpbpc(extractData(valList.get(idxMap.get("扣非净利润"))[i]));
                }
                if(idxMap.get("营业总收入") != null) {
                    financailStatement.setToi(extractData(valList.get(idxMap.get("营业总收入"))[i]));
                }

                if(idxMap.get("基本每股收益") != null) {
                    financailStatement.setPe(extractData(valList.get(idxMap.get("基本每股收益"))[i]));
                }
                if(idxMap.get("每股净资产") != null) {
                    financailStatement.setBvps(extractData(valList.get(idxMap.get("每股净资产"))[i]));
                }
                if(idxMap.get("每股经营现金流") != null) {
                    financailStatement.setCps(extractData(valList.get(idxMap.get("每股经营现金流"))[i]));
                }
                if(idxMap.get("销售净利率") != null) {
                    financailStatement.setSmpr(extractData(valList.get(idxMap.get("销售净利率"))[i]));
                }
                if(idxMap.get("销售毛利率") != null) {
                    financailStatement.setSgpr(extractData(valList.get(idxMap.get("销售毛利率"))[i]));
                }
                if(idxMap.get("净资产收益率") != null) {
                    financailStatement.setRoe(extractData(valList.get(idxMap.get("净资产收益率"))[i]));
                }
                if(idxMap.get("资产负债比率") != null) {
                    financailStatement.setDtar(extractData(valList.get(idxMap.get("资产负债比率"))[i]));
                }

                stockDataService.fsUpdateOrInsert(financailStatement);
            }catch (Exception e){
                LOGGER.warn("createOrUpdateJqka fail code={},date={}", task.getInfoValue("code"), valList.get(0)[i], e);
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
