
package cn.orditech.stockanalysis.entity;

import java.io.Serializable;

/**
 * @author kimi
 */
public class StockInfo extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 5454155825314635342L;

    private static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    //date formats
    public static final String FORMAT_MODIFY_TIME = DATE_TIME_FORMAT;

    private Long id;

    /**
     * 股票交易代码
     **/
    private java.lang.String code;

    /**
     * 股票类型：01沪市主板，02深市主板
     **/
    private java.lang.String type;

    /**
     * 股票名称
     **/
    private java.lang.String name;

    /**
     * 股本数量
     **/
    private java.lang.Long sc;

    /**
     * 当前价格
     **/
    private java.lang.Float price;

    public StockInfo () {
    }

    public StockInfo (
            java.lang.String code
    ) {
        this.code = code;
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

    public void setType (java.lang.String value) {
        this.type = value;
    }

    public java.lang.String getType () {
        return this.type;
    }

    public void setName (java.lang.String value) {
        this.name = value;
    }

    public java.lang.String getName () {
        return this.name;
    }

    public void setSc (java.lang.Long value) {
        this.sc = value;
    }

    public java.lang.Long getSc () {
        return this.sc;
    }

    public void setPrice (java.lang.Float value) {
        this.price = value;
    }

    public java.lang.Float getPrice () {
        return this.price;
    }

}

