
package cn.orditech.stockanalysis.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.orditech.stockanalysis.entity.DailyTradeDetail;

/**
 * @author kimi
 * @version 0.99
 * @see BaseDao，定义基本操作以外的数据库操作
 */
public class DailyTradeDetailDao extends BaseDao<DailyTradeDetail, DailyTradeDetail> {

    /**
     * 按年份大小倒叙查询指定数量的数据
     *
     * @param code
     * @param dayCount 要查询的交易日数量
     * @return 数据列表按日期大小倒叙排列
     */
    public List<DailyTradeDetail> selectListByDateDesc (String code, Integer dayCount) {
        Map<String, Object> parMap = new HashMap<String, Object> ();
        parMap.put ("code", code);
        parMap.put ("dayCount", dayCount);
        return this.getSqlSession ().selectList (this.getNameSpace () + ".selectListByDateDesc", parMap);
    }

    /**
     * 更新市值
     * @param code
     * @param date
     * @param marketValue
     * @return
     */
    public int udpateMarketValue(String code,String date,double marketValue){
        Map<String, Object> parMap = new HashMap<String, Object> ();
        parMap.put ("code", code);
        parMap.put("date",date);
        parMap.put ("marketValue", marketValue);
        return this.getSqlSession ().update (this.getNameSpace () + ".udpateMarketValue", parMap);
    }
}