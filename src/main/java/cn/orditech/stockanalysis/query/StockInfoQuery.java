/**
 *
 */
package cn.orditech.stockanalysis.query;

/**
 * @author kimi 分页模糊查询
 */
public class StockInfoQuery extends BasePage {
    /**
     *
     */
    @SuppressWarnings("unused")
    private static final long serialVersionUID = 1L;

    /**
     * 搜索关键词，用于匹配股票名称或者股票代码
     */
    private String keyword;

    /**
     * @return the keyword
     */
    public String getKeyword () {
        return keyword;
    }

    /**
     * @param keyword the keyword to set
     */
    public void setKeyword (String keyword) {
        this.keyword = keyword;
    }

}
