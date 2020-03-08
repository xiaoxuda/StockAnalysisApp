
package cn.orditech.stockanalysis.dao;

import cn.orditech.stockanalysis.entity.DailyTradeDetail;
import cn.orditech.stockanalysis.entity.FinancailStatement;
import cn.orditech.stockanalysis.entity.StockInfo;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author kimi
 * @version 0.99
 * @see BaseDao 继承基类，定义基本操作以外的数据库操作
 */
@Repository
public class TencentDao {
    @Autowired
    @Qualifier("tencentSqlSession")
    private SqlSessionTemplate tencentSqlSession;

    @Autowired
    @Qualifier("sqlSession")
    protected SqlSessionTemplate sqlSession;

    public void prcessTradeDataTransfer(){
        Long minId = 0L;
        List<DailyTradeDetail> list;
        Map<String, Object> map = new HashMap<>();
        do {
            list = sqlSession.selectList("cn.orditech.stockanalysis.dao.TencentDao.pageFindTradeDetail", minId);
            if(list.size() > 0){
                map.put("tradeList", list);
                tencentSqlSession.insert("cn.orditech.stockanalysis.dao.TencentDao.batchInsertTradeDetail", map);

                minId = list.get(list.size() - 1).getId();
            }
        } while (list.size() == 200);
    }

    public void prcessStockDataTransfer(){
        Long minId = 0L;
        List<StockInfo> list;
        Map<String, Object> map = new HashMap<>();
        do {
            list = sqlSession.selectList("cn.orditech.stockanalysis.dao.TencentDao.pageFindStockDetail", minId);
            if(list.size() > 0){
                map.put("stockList", list);
                tencentSqlSession.insert("cn.orditech.stockanalysis.dao.TencentDao.batchInsertStockDetail", map);

                minId = list.get(list.size() - 1).getId();
            }
        } while (list.size() == 200);
    }

    public void prcessFinanceDataTransfer(){
        Long minId = 0L;
        List<FinancailStatement> list;
        Map<String, Object> map = new HashMap<>();
        do {
            list = sqlSession.selectList("cn.orditech.stockanalysis.dao.TencentDao.pageFindFinanceDetail", minId);
            if(list.size() > 0){
                map.put("financeList", list);
                tencentSqlSession.insert("cn.orditech.stockanalysis.dao.TencentDao.batchInsertFinanceDetail", map);

                minId = list.get(list.size() - 1).getId();
            }
        } while (list.size() == 200);
    }
}