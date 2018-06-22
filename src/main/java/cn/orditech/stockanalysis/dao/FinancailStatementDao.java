
package cn.orditech.stockanalysis.dao;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.orditech.stockanalysis.entity.FinancailStatement;
import org.springframework.format.datetime.DateFormatter;
import org.springframework.stereotype.Repository;

/**
 * @author kimi
 * @version 0.99
 * @see BaseDao 继承基类，定义基本操作以外的数据库操作
 */
@Repository
public class FinancailStatementDao extends BaseDao<FinancailStatement, FinancailStatement> {

    /**
     * 按年份大小倒叙查询指定数量的数据
     *
     * @param code
     * @return 数据列表按年份大小倒叙排列
     */
    public List<FinancailStatement> selectListByYearDesc (String code) {
        Map<String, Object> parMap = new HashMap<String, Object> ();
        parMap.put ("code", code);
        Calendar calendar = Calendar.getInstance ();
        calendar.add(Calendar.YEAR, -4);
        calendar.set (Calendar.MONTH, 1);
        parMap.put("startDate", new SimpleDateFormat ("yyyy-MM-dd").format (calendar.getTime()));
        return this.getSqlSession ().selectList (this.getNameSpace () + ".selectListByQuarterDesc", parMap);
    }
}