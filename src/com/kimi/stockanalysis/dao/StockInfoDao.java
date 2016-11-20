
package com.kimi.stockanalysis.dao;

import java.util.List;

import com.kimi.stockanalysis.dao.g.BaseDao;
import com.kimi.stockanalysis.entity.StockInfo;
import com.kimi.stockanalysis.query.StockInfoQuery;
/**
 * @author kimi
 * @version 1.0
 * @see 继承基类，定义基本操作以外的数据库操作
 */
public class StockInfoDao extends BaseDao<StockInfo, java.lang.String> {
	@Override
	public String getNameSpace(){
		return "com.kimi.stockanalysis.dao.StockInfoDao";
	}
	
	/**
	 * 按名称和股票代码搜索
	 * @param keyword
	 * @return
	 */
	public List<StockInfo> fuzzySearchByCodeOrName(StockInfoQuery query){
		return this.getSqlSession().selectList(getNameSpace()+".fuzzySearchByCodeOrName",query);
	}
}