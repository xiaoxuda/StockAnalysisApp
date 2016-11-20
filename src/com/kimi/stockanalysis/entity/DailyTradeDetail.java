
package com.kimi.stockanalysis.entity;

import java.io.Serializable;
/**
 * @author kimi
 */
public class DailyTradeDetail implements Serializable {
	private static final long serialVersionUID = 5454155825314635342L;
	
	/** 股票代码 **/
	private java.lang.String code;
	
	/** 交易日期:yyyy-MM-dd **/
	private java.lang.String date;
	
	/** 昨天收盘价 **/
	private java.lang.Float lastEndPrice;
	
	/** 开盘价格 **/
	private java.lang.Float startPrice;
	
	/** 最高价格 **/
	private java.lang.Float maxPrice;
	
	/** 最低价格 **/
	private java.lang.Float minPrice;
	
	/** 收盘价格 **/
	private java.lang.Float endPrice;
	
	/** 交易量（股） **/
	private java.lang.Long tradeVolume;
	
	/** 交易金额 **/
	private java.lang.Double tradeAmt;
	

	public DailyTradeDetail(){
	}

	public DailyTradeDetail(
		java.lang.String code,
		java.lang.String date
	){
		this.code = code;
		this.date = date;
	}

	public void setCode(java.lang.String value) {
		this.code = value;
	}
	
	public java.lang.String getCode() {
		return this.code;
	}
	public void setDate(java.lang.String value) {
		this.date = value;
	}
	
	public java.lang.String getDate() {
		return this.date;
	}
	public void setLastEndPrice(java.lang.Float value) {
		this.lastEndPrice = value;
	}
	
	public java.lang.Float getLastEndPrice() {
		return this.lastEndPrice;
	}
	public void setStartPrice(java.lang.Float value) {
		this.startPrice = value;
	}
	
	public java.lang.Float getStartPrice() {
		return this.startPrice;
	}
	public void setMaxPrice(java.lang.Float value) {
		this.maxPrice = value;
	}
	
	public java.lang.Float getMaxPrice() {
		return this.maxPrice;
	}
	public void setMinPrice(java.lang.Float value) {
		this.minPrice = value;
	}
	
	public java.lang.Float getMinPrice() {
		return this.minPrice;
	}
	public void setEndPrice(java.lang.Float value) {
		this.endPrice = value;
	}
	
	public java.lang.Float getEndPrice() {
		return this.endPrice;
	}
	public void setTradeVolume(java.lang.Long value) {
		this.tradeVolume = value;
	}
	
	public java.lang.Long getTradeVolume() {
		return this.tradeVolume;
	}
	public void setTradeAmt(java.lang.Double value) {
		this.tradeAmt = value;
	}
	
	public java.lang.Double getTradeAmt() {
		return this.tradeAmt;
	}

	//region toString & equals & clone
    @Override
    public String toString() {
        return "DailyTradeDetail{" +
				 "code=" + code +  "," +
				 "date=" + date +  "," +
				 "lastEndPrice=" + lastEndPrice +  "," +
				 "startPrice=" + startPrice +  "," +
				 "maxPrice=" + maxPrice +  "," +
				 "minPrice=" + minPrice +  "," +
				 "endPrice=" + endPrice +  "," +
				 "tradeVolume=" + tradeVolume +  "," +
				 "tradeAmt=" + tradeAmt + 
                '}';
    }	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
        if(obj instanceof DailyTradeDetail == false) return false;
		DailyTradeDetail other = (DailyTradeDetail)obj;
		if (!code.equals(other.code)) return false;
		if (!date.equals(other.date)) return false;
		if (!lastEndPrice.equals(other.lastEndPrice)) return false;
		if (!startPrice.equals(other.startPrice)) return false;
		if (!maxPrice.equals(other.maxPrice)) return false;
		if (!minPrice.equals(other.minPrice)) return false;
		if (!endPrice.equals(other.endPrice)) return false;
		if (!tradeVolume.equals(other.tradeVolume)) return false;
		if (!tradeAmt.equals(other.tradeAmt)) return false;
        return true;			
	}
	@Override
    public int hashCode() {
        int result = super.hashCode();
		result = 31 * result + code.hashCode();
		result = 31 * result + date.hashCode();
		result = 31 * result + lastEndPrice.hashCode();
		result = 31 * result + startPrice.hashCode();
		result = 31 * result + maxPrice.hashCode();
		result = 31 * result + minPrice.hashCode();
		result = 31 * result + endPrice.hashCode();
		result = 31 * result + tradeVolume.hashCode();
		result = 31 * result + tradeAmt.hashCode();
        return result;
    }
	public DailyTradeDetail clone(){
		DailyTradeDetail newobj=new DailyTradeDetail();
			 newobj.code=this.code;
			 newobj.date=this.date;
			 newobj.lastEndPrice=this.lastEndPrice;
			 newobj.startPrice=this.startPrice;
			 newobj.maxPrice=this.maxPrice;
			 newobj.minPrice=this.minPrice;
			 newobj.endPrice=this.endPrice;
			 newobj.tradeVolume=this.tradeVolume;
			 newobj.tradeAmt=this.tradeAmt;
		return newobj;
	}
	//endregion
}

