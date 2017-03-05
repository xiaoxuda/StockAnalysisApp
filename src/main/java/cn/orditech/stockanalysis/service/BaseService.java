package cn.orditech.stockanalysis.service;

import java.util.List;

import cn.orditech.stockanalysis.dao.BaseDao;

/**
 * 服务基类
 * T数据实体类，K主键（如果主键有一个以上则K与T相同）
 * @author kimi
 * @version 0.99
 */
public abstract class BaseService<T, K> {
    protected abstract BaseDao<T, K> getDao ();

    /**
     * 插入操作
     *
     * @param entity 数据表对应的实体类
     * @return
     */
    public int insert (T entity) {
        return this.getDao ().insert (entity);
    }

    ;

    /**
     * 根据主键删除对应数据
     * @param key
     * @return
     */
    public int delete (K key) {
        return this.getDao ().delete (key);
    }

    ;

    /**
     * 根据主键更新对应数据条目
     *
     * @param entity
     * @return
     */
    public int update (T entity) {
        return this.getDao ().update (entity);
    }

    ;

    /**
     * 根据主键更新对应数据条目，如果entity数据域为null则不更新对应数据域
     *
     * @param entity
     * @return
     */
    public int updateSelective (T entity) {
        return this.getDao ().updateSelective (entity);
    }

    ;

    /**
     * 根据主键查询对应数据条目
     *
     * @param key
     * @return
     */
    public T selectOne (K key) {
        return this.getDao ().selectOne (key);
    }

    ;

    /**
     * 根据查询条件返回相应数据条目，entity中值为null的数据域不作为查询条件
     *
     * @param entity
     * @return
     */
    public List<T> selectList (T entity) {
        return this.getDao ().selectList (entity);
    }

    ;
}
