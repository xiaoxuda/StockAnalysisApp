
package com.kimi.stockanalysis.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.kimi.stockanalysis.dao.g.BaseDao;
import com.kimi.stockanalysis.entity.FinancailStatement;
/**
 * @author kimi
 * @version 0.99
 * @see 继承基类，定义基本操作以外的数据库操作
 */
public class FinancailStatementDao extends BaseDao<FinancailStatement,FinancailStatement> {
	@Override
	public String getNameSpace(){
		return "com.kimi.stockanalysis.dao.FinancailStatementDao";
	}
	
	/**
	 * 按年份大小倒叙查询指定数量的数据
	 * @param code
	 * @param quarterCount要查询的季度数量
	 * @return 数据列表按年份大小倒叙排列
	 */
	public List<FinancailStatement> selectListByYearDesc(String code, Integer quarterCount){
		Map<String,Object> parMap = new HashMap<String,Object>();
		parMap.put("code", code);
		parMap.put("quarterCount", quarterCount);
		return this.getSqlSession().selectList(this.getNameSpace()+".selectListByQuarterDesc", parMap);
	}
}