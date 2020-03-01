
package cn.orditech.stockanalysis.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.orditech.stockanalysis.entity.DailyTradeDetail;
import org.springframework.stereotype.Repository;

/**
 * @author kimi
 * @version 0.99
 * @see BaseDao，定义基本操作以外的数据库操作
 */
@Repository
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
    public int udpateMarketValue(String code,String date,double marketValue,Long sc){
        Map<String, Object> parMap = new HashMap<String, Object> ();
        parMap.put ("code", code);
        parMap.put("date",date);
        parMap.put ("marketValue", marketValue);
        parMap.put("sc",sc);
        return this.getSqlSession ().update (this.getNameSpace () + ".udpateMarketValue", parMap);
    }

    /**
     * 根据日期分页查询交易信息
     * @param date 格式yyyy-MM-dd
     * @param minId 查询的起始数据
     * @param pageSize 查询数量
     * @return
     */
    public List<DailyTradeDetail> pageFindByDateOrderById(String date, Long minId, Integer pageSize){
        Map<String, Object> map = new HashMap<>();
        map.put("date", date);
        map.put("minId", minId);
        map.put("pageSize", pageSize);
        return this.getSqlSession().selectList(this.getNameSpace() + ".pageFindByDateOrderById", map);
    }
}