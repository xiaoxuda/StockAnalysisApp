
package cn.orditech.stockanalysis.dao;


import cn.orditech.stockanalysis.entity.WechatPublic;
import org.springframework.stereotype.Repository;

/**
 * 数据操作接口
 * @author kimi
 * @version 1.0
 */
@Repository
public class WechatPublicDao extends BaseDao<WechatPublic,String>{
    public WechatPublic selectByName(String name){
        return super.getSqlSession ().selectOne ("selectByName", name);
    }
}