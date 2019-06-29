package cn.orditech.stockanalysis.service;

import cn.orditech.stockanalysis.dao.BaseDao;
import cn.orditech.stockanalysis.dao.WechatPublicDao;
import cn.orditech.stockanalysis.entity.WechatPublic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by kimi on 2018/10/27.
 */
@Service
public class WechatPublicService extends BaseService<WechatPublic, String> {
    @Autowired
    public WechatPublicDao wechatPublicDao;

    @Override
    protected
    BaseDao<WechatPublic,String> getDao () {
        return wechatPublicDao;
    }

    public WechatPublic selectByName(String name){
        return wechatPublicDao.selectByName (name);
    }
}