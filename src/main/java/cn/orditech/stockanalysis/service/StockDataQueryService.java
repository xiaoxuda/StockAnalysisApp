/**
 *
 */
package cn.orditech.stockanalysis.service;

import cn.orditech.stockanalysis.dao.StockInfoDao;
import cn.orditech.stockanalysis.entity.StockInfo;
import cn.orditech.stockanalysis.query.StockInfoQuery;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author kimi
 * @version 1.0
 */
@Service
public class StockDataQueryService {
    private final Logger LOGGER = LoggerFactory.getLogger (StockDataQueryService.class);

    @Autowired
    private StockInfoDao stockInfoDao;

    public List<StockInfo> fuzzySearchByCodeOrName (StockInfoQuery query) {
        LOGGER.info ("股票 模糊查询，参数keyword:{}", query.getKeyword ());
        if (StringUtils.isBlank (query.getKeyword ())) {
            return null;
        } else {
            return stockInfoDao.fuzzySearchByCodeOrName (query);
        }
    }

}
