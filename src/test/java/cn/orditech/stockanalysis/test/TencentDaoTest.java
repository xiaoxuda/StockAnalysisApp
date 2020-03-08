package cn.orditech.stockanalysis.test;

import cn.orditech.stockanalysis.dao.TencentDao;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author xiaoxuda
 * @createTime 2020-03-08 10:05
 * @description
 */
public class TencentDaoTest extends BaseTest {
    @Autowired
    private TencentDao tencentDao;

    @Test
    public void tencentTest(){
        //tencentDao.prcessFinanceDataTransfer();
        tencentDao.prcessTradeDataTransfer();
    }
}
