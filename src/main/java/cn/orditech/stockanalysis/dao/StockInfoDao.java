
package cn.orditech.stockanalysis.dao;

import java.util.List;

import cn.orditech.stockanalysis.query.StockInfoQuery;
import cn.orditech.stockanalysis.entity.StockInfo;

/**
 * @author kimi
 * @version 1.0
 * @see BaseDao 继承基类，定义基本操作以外的数据库操作
 */
public class StockInfoDao extends BaseDao<StockInfo, java.lang.String> {

    /**
     * 按名称和股票代码搜索
     *
     * @param query
     * @return
     */
    public List<StockInfo> fuzzySearchByCodeOrName (StockInfoQuery query) {
        return this.getSqlSession ().selectList (getNameSpace () + ".fuzzySearchByCodeOrName", query);
    }
}