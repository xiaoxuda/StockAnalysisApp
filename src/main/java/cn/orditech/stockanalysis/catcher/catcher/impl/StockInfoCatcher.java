package cn.orditech.stockanalysis.catcher.catcher.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.orditech.stockanalysis.catcher.CatchTask;
import cn.orditech.stockanalysis.catcher.catcher.BaseCatcher;
import cn.orditech.stockanalysis.catcher.enums.TaskTypeEnum;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import cn.orditech.stockanalysis.entity.StockInfo;
import cn.orditech.stockanalysis.service.StockDataService;
import org.springframework.stereotype.Component;

/**
 * @author kimi
 * @see 抓取股票代码与名称、股票类型
 */
@Component
public class StockInfoCatcher extends BaseCatcher {

    @Autowired
    private StockDataService stockDataService;

    // 股票类型
    public class TypeClass {
        public final static String SZMB = "szmb";
        public final static String SZSME = "szsme";
        public final static String SZCN = "szcn";
        public final static String SHMB = "shmb";
        public final static String HKMB = "hk_mb";
        public final static String HKGEN = "hk_gem";
    }

    // 匹配www.eastmoney.com网站对股票类型的定义
    public static Map<String, String> typeMap = new HashMap<String, String> ();

    static {
        typeMap.put (TypeClass.SZMB, "02");
        typeMap.put (TypeClass.SZSME, "02");
        typeMap.put (TypeClass.SZCN, "02");
        typeMap.put (TypeClass.SHMB, "01");
    }

    @Override
    public TaskTypeEnum getTaskType () {
        return TaskTypeEnum.JUCAONET_COMPANY_LIST;
    }

    @Override
    public boolean extractAndPersistence (String src, CatchTask task) {
        if (src == null || src == "") {
            LOGGER.error ("抓取公司列表失败,TaskType:{},param:{}", task.getType (), task);
            return false;
        }
        int start = src.indexOf ("<div class=\"list-ct\">");
        int end = StringUtils.indexOf (src, "</div></div><div class=\"clear\">", start);
        if (start == -1 || end == -1) {
            LOGGER.error ("抓取公司列表失败,TaskType:{},param:{}", task.getType (), task);
            return false;
        }
        // 需要使用webclient
        String list = src.substring (start + 21, end);
        list = list.replaceAll ("[\t]|[ ]", "");
        String table[] = list.split ("</div><divid=\"con-a-[0-9]{1}\"[^>]*>");
        Map<String, List<String>> result = new HashMap<String, List<String>> ();
        // 不取香港市场的股票，顺序需要根据网站同步调整
        String types[] = {TypeClass.SZMB, TypeClass.SZSME, TypeClass.SZCN, TypeClass.SHMB};
        for (int i = 0; i < 4; i++) {
            String item = table[i];
            item = item.replaceAll ("<li[^>]*>|<a[^>]*>|</li>|<ul[^>]*>|</ul>|<div[^>]*>|</div>", "");
            item = item.replaceAll ("[\t]|[ ]", "");
            List<String> trs = new ArrayList<String> (Arrays.asList (item.split ("</a>")));
            result.put (types[i], trs);
        }
        createOrUpdate (result, task);
        return true;
    }

    /**
     * 批量插入或者更新股票基本信息
     *
     * @param map
     * @param task
     * @return
     */
    private boolean createOrUpdate (Map<String, List<String>> map, CatchTask task) {
        for (String type : map.keySet ()) {
            List<String> list = map.get (type);
            for (String s : list) {
                String code = s.substring (0, 6);

                StockInfo si = new StockInfo ();
                si.setCode (code);
                si.setName (s.substring (6).replace (" ", ""));
                si.setType (type);

                stockDataService.siUpdateOrInsert (si);

            }
        }
        return true;
    }

    @Override
    public CatchTask generateTask (StockInfo stockInfo) {
        CatchTask task = new CatchTask ();
        task.setUrl ("http://www.cninfo.com.cn/cninfo-new/information/companylist");
        task.setType (this.getTaskType ().getCode ());
        return task;
    }

}
