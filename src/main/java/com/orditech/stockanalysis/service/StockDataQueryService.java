/**
 * 
 */
package com.orditech.stockanalysis.service;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.orditech.stockanalysis.dao.StockInfoDao;
import com.orditech.stockanalysis.entity.StockInfo;
import com.orditech.stockanalysis.query.StockInfoQuery;

/**
 * @author kimi
 * @version 1.0
 */
public class StockDataQueryService {
	private final Logger LOGGER = LoggerFactory.getLogger(StockDataQueryService.class);

	@Autowired
	private StockInfoDao stockInfoDao;

	public List<StockInfo> fuzzySearchByCodeOrName(StockInfoQuery query){
		LOGGER.info("股票 模糊查询，参数keyword:{}", query.getKeyword());
		if(StringUtils.isBlank(query.getKeyword())){
			return null;
		}else{
			return stockInfoDao.fuzzySearchByCodeOrName(query);
		}
	}

}
