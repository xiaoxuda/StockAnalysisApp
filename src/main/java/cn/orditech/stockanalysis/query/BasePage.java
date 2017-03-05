/**
 *
 */
package cn.orditech.stockanalysis.query;

/**
 * @author kimi
 */
public class BasePage {
    /**
     * 单页最大数量，防止数据单次量过大
     */
    private static final Integer MaxPageSize = 100;

    /**
     * 默认单页数量
     */
    private static final Integer DefaultPageSize = 10;

    /**
     * 默认页码
     */
    private static final Integer DefaultPageNum = 1;

    /**
     * 每页数据条数，用于分页查询，非空
     */
    private Integer pageSize = DefaultPageSize;

    /**
     * 页码，用于分页查询，非空
     */
    private Integer pageNum = DefaultPageNum;

    /**
     * 起始索引值
     */
    private Integer index = (pageNum - 1) * pageSize;

    /**
     * 排序依赖字段
     */
    private String orderBy;

    /**
     * 排序顺序
     */
    private String direct;

    /**
     * @return the pageSize
     */
    public Integer getPageSize () {
        return pageSize;
    }

    /**
     * @param pageSize the pageSize to set
     */
    public void setPageSize (Integer pageSize) {
        if (pageSize == null || pageSize > MaxPageSize) {
            this.pageSize = DefaultPageSize;
        } else {
            this.pageSize = pageSize;
        }
        setIndex ();
    }

    /**
     * @return the pageNum
     */
    public Integer getPageNum () {
        return pageNum;
    }

    /**
     * @param pageNum the pageNum to set
     */
    public void setPageNum (Integer pageNum) {
        if (pageNum == null) {
            this.pageNum = DefaultPageNum;
        } else {
            this.pageNum = pageNum;
        }
        setIndex ();
    }

    /**
     * 设置索引
     */
    public void setIndex () {
        this.index = this.pageNum * this.pageSize;
    }

    public Integer getIndex () {
        return this.index;
    }

    /**
     * @return the orderBy
     */
    public String getOrderBy () {
        return orderBy;
    }

    /**
     * @param orderBy the orderBy to set
     */
    public void setOrderBy (String orderBy) {
        this.orderBy = orderBy;
    }

    /**
     * @return the direct
     */
    public String getDirect () {
        return direct;
    }

    /**
     * @param direct the direct to set
     */
    public void setDirect (String direct) {
        this.direct = direct;
    }


}
