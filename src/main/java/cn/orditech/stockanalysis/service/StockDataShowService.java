/**
 *
 */
package cn.orditech.stockanalysis.service;

import cn.orditech.stockanalysis.dao.FinancailStatementDao;
import cn.orditech.stockanalysis.entity.DailyTradeDetail;
import cn.orditech.stockanalysis.result.BusinessCurveResult;
import cn.orditech.tools.DateUtils;
import cn.orditech.stockanalysis.dao.DailyTradeDetailDao;
import cn.orditech.stockanalysis.entity.FinancailStatement;
import cn.orditech.stockanalysis.enums.FinancailStatementAttrEnum;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.*;

/**
 * 股票信息处理服务，用于前端展示
 *
 * @author kimi
 */
@Service
public class StockDataShowService {
    private Logger LOGGER = LoggerFactory.getLogger (StockDataShowService.class);

    @Autowired
    private FinancailStatementDao financailStatementDao;

    @Autowired
    private DailyTradeDetailDao dailyTradeDetailDao;

    /**
     * 查询选定股票的财务信息，按展示要求做运算处理并以合适的方式返回
     *
     * @param code
     * @param quarterCount
     * @return
     */
    public Map<String, Object> generateBussinessCurveData (String code, Integer quarterCount) {
        if (StringUtils.isBlank (code)) {
            return null;
        }

        // 财务数据
        List<FinancailStatement> list = financailStatementDao.selectListByYearDesc (code, quarterCount);
        Map<String, BusinessCurveResult> toiMap = buildCurveData (list, FinancailStatementAttrEnum.ATTR_TOI);
        Map<String, BusinessCurveResult> mpMap = buildCurveData (list, FinancailStatementAttrEnum.ATTR_MP);
        Map<String, BusinessCurveResult> sgprMap = buildCurveData (list, FinancailStatementAttrEnum.ATTR_SGPR);
        Map<String, BusinessCurveResult> drarMap = buildCurveData (list, FinancailStatementAttrEnum.ATTR_DRAR);
        Map<String, BusinessCurveResult> seMap = buildCurveData (list, FinancailStatementAttrEnum.ATTR_SE);

        // 近六个月交易信息（大概120个交易日）
        List<DailyTradeDetail> tradeList = dailyTradeDetailDao.selectListByDateDesc (code, 120);
        if (tradeList != null && !tradeList.isEmpty ()) {
            Collections.sort (tradeList, new Comparator<DailyTradeDetail> () {
                @Override
                public int compare (DailyTradeDetail o1, DailyTradeDetail o2) {
                    return 0 - o1.getDate ().compareTo (o2.getDate ());
                }
            });
        }


        Map<String, Object> result = new HashMap<String, Object> ();

        result.put ("toiMap", toiMap);
        result.put ("mpMap", mpMap);
        result.put ("drarMap", drarMap);
        result.put ("sgprMap", sgprMap);
        result.put ("seMap", seMap);
        result.put ("tradeList", tradeList);

        return result;
    }

    /**
     * 按季度提取特定经营数据，比年计算同比变动率
     *
     * @param list
     * @param fsae
     * @return
     */
    public Map<String, BusinessCurveResult> buildCurveData (List<FinancailStatement> list,
                                                            FinancailStatementAttrEnum fsae) {
        if (list == null || list.isEmpty ()) {
            LOGGER.error ("list参数为空");
            return null;
        }
        if (fsae == null) {
            LOGGER.error ("FinanceStatementEnum为空");
            return null;
        }

        try {
            Field field = FinancailStatement.class.getDeclaredField (fsae.getName ());
            field.setAccessible (true);

            Map<String, BusinessCurveResult> result = new HashMap<String, BusinessCurveResult> ();

            for (FinancailStatement fs : list) {
                Object value = field.get (fs);
                if (value == null) {
                    continue;
                }
                BusinessCurveResult curveResult = new BusinessCurveResult (fs.getCode (), fs.getDate ());
                curveResult.setAmount (Double.valueOf (value.toString ()));
                result.put (curveResult.getDate (), curveResult);
            }
            // 计算同比变化率
            calculateRaiseRate (result);
            return result;
        } catch (Exception e) {
            LOGGER.error (e.toString ());
            return null;
        }
    }

    /**
     * 计算同比增长率
     */
    public void calculateRaiseRate (Map<String, BusinessCurveResult> result) {
        if (result == null || result.isEmpty ()) {
            return;
        }
        for (String key : result.keySet ()) {
            BusinessCurveResult crnow = result.get (key);
            try {
                String date_in_last_year = DateUtils.getThisDateInLastYear (key);
                BusinessCurveResult crcmp = result.get (date_in_last_year);
                if (crcmp != null && crnow != null && crcmp.getAmount () != null && crnow.getAmount () != null) {
                    // 前值小于等于零，对比较值做调整，基准值计为1；
                    double base = crcmp.getAmount ();
                    if (base <= 0) {
                        base = 1;
                    }
                    double raiseRate = (crnow.getAmount () - crcmp.getAmount ()) / base;
                    crnow.setRaiseRate (raiseRate);
                }
            } catch (Exception e) {
                e.printStackTrace ();
                LOGGER.error ("code:{},date:{},同比增长率计算失败。", crnow.getCode (), crnow.getDate ());
            }
        }
    }
}