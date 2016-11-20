
package com.kimi.stockanalysis.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.kimi.stockanalysis.dao.g.BaseDao;
import com.kimi.stockanalysis.entity.DailyTradeDetail;
/**
 * @author kimi
 * @version 0.99
 * @see 继承基类，定义基本操作以外的数据库操作
 */
public class DailyTradeDetailDao extends BaseDao<DailyTradeDetail,DailyTradeDetail> {
	@Override
	public String getNameSpace() {
		// TODO Auto-generated method stub
		return "com.kimi.stockanalysis.dao.DailyTradeDetailDao";
	}
	
	/**
	 * 按年份大小倒叙查询指定数量的数据
	 * @param code
	 * @param dayCount要查询的交易日数量
	 * @return 数据列表按日期大小倒叙排列
	 */
	public List<DailyTradeDetail> selectListByDateDesc(String code, Integer dayCount){
		Map<String,Object> parMap = new HashMap<String,Object>();
		parMap.put("code", code);
		parMap.put("dayCount", dayCount);
		return this.getSqlSession().selectList(this.getNameSpace()+".selectListByDateDesc", parMap);
	}
}