
package com.orditech.stockanalysis.dao;

import java.util.List;

import com.orditech.stockanalysis.dao.g.BaseDao;
import com.orditech.stockanalysis.entity.StockInfo;
import com.orditech.stockanalysis.query.StockInfoQuery;
/**
 * @author kimi
 * @version 1.0
 * @see 继承基类，定义基本操作以外的数据库操作
 */
public class StockInfoDao extends BaseDao<StockInfo, java.lang.String> {
	@Override
	public String getNameSpace(){
		return "com.orditech.stockanalysis.dao.StockInfoDao";
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