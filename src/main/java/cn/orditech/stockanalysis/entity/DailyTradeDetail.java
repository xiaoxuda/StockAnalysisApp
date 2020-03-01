
package cn.orditech.stockanalysis.entity;

import java.io.Serializable;

/**
 * @author kimi
 */
public class DailyTradeDetail extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 5454155825314635342L;

    /**
     * 数据id
     */
    private Long id;

    /**
     * 股票代码
     **/
    private java.lang.String code;

    /**
     * 交易日期:yyyy-MM-dd
     **/
    private java.lang.String date;

    /**
     * 昨天收盘价
     **/
    private java.lang.Float lastEndPrice;

    /**
     * 开盘价格
     **/
    private java.lang.Float startPrice;

    /**
     * 最高价格
     **/
    private java.lang.Float maxPrice;

    /**
     * 最低价格
     **/
    private java.lang.Float minPrice;

    /**
     * 收盘价格
     **/
    private java.lang.Float endPrice;

    /**
     * 交易量（股）
     **/
    private java.lang.Long tradeVolume;

    /**
     * 交易金额
     **/
    private java.lang.Double tradeAmt;

    /**
     * 当前市值
     */
    private java.lang.Double marketValue;

    /**
     * 当前股本
     */
    private Long sc;

    /**
     * 静态市盈利率
     */
    private Float peStatic;
    /**
     * 动态市盈利率
     */
    private Float peDynamic;

    public DailyTradeDetail () {
    }

    public DailyTradeDetail (
            java.lang.String code,
            java.lang.String date
    ) {
        this.code = code;
        this.date = date;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setCode (java.lang.String value) {
        this.code = value;
    }

    public java.lang.String getCode () {
        return this.code;
    }

    public void setDate (java.lang.String value) {
        this.date = value;
    }

    public java.lang.String getDate () {
        return this.date;
    }

    public void setLastEndPrice (java.lang.Float value) {
        this.lastEndPrice = value;
    }

    public java.lang.Float getLastEndPrice () {
        return this.lastEndPrice;
    }

    public void setStartPrice (java.lang.Float value) {
        this.startPrice = value;
    }

    public java.lang.Float getStartPrice () {
        return this.startPrice;
    }

    public void setMaxPrice (java.lang.Float value) {
        this.maxPrice = value;
    }

    public java.lang.Float getMaxPrice () {
        return this.maxPrice;
    }

    public void setMinPrice (java.lang.Float value) {
        this.minPrice = value;
    }

    public java.lang.Float getMinPrice () {
        return this.minPrice;
    }

    public void setEndPrice (java.lang.Float value) {
        this.endPrice = value;
    }

    public java.lang.Float getEndPrice () {
        return this.endPrice;
    }

    public void setTradeVolume (java.lang.Long value) {
        this.tradeVolume = value;
    }

    public java.lang.Long getTradeVolume () {
        return this.tradeVolume;
    }

    public void setTradeAmt (java.lang.Double value) {
        this.tradeAmt = value;
    }

    public java.lang.Double getTradeAmt () {
        return this.tradeAmt;
    }

    public Double getMarketValue () {
        return marketValue;
    }

    public void setMarketValue (Double marketValue) {
        this.marketValue = marketValue;
    }

    public Long getSc () {
        return sc;
    }

    public void setSc (Long sc) {
        this.sc = sc;
    }

    public Float getPeStatic() {
        return peStatic;
    }

    public void setPeStatic(Float peStatic) {
        this.peStatic = peStatic;
    }

    public Float getPeDynamic() {
        return peDynamic;
    }

    public void setPeDynamic(Float peDynamic) {
        this.peDynamic = peDynamic;
    }
}

