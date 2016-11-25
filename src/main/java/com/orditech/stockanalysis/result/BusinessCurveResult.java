/**
 * 
 */
package com.orditech.stockanalysis.result;

/**
 * 经营业绩曲线值
 * 
 * @author kimi
 * 
 */
public class BusinessCurveResult {
	/** 股票代码 **/
	private String code;
	/** 财务季度 **/
	private String date;

	/** 经营发生额 **/
	private Double amount;

	/** 同比变化率 **/
	private Double raiseRate;

	public BusinessCurveResult() {
	}

	public BusinessCurveResult(String code, String date) {
		this.code = code;
		this.date = date;
	}

	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * @param code
	 *            the code to set
	 */
	public void setCode(String code) {
		this.code = code;
	}

	/**
	 * @return the date
	 */
	public String getDate() {
		return date;
	}

	/**
	 * @param date
	 *            the date to set
	 */
	public void setDate(String date) {
		this.date = date;
	}

	/**
	 * @return the amount
	 */
	public Double getAmount() {
		return amount;
	}

	/**
	 * @param amount
	 *            the amount to set
	 */
	public void setAmount(Double amount) {
		this.amount = amount;
	}

	/**
	 * @return the raiseRate
	 */
	public Double getRaiseRate() {
		return raiseRate;
	}

	/**
	 * @param raiseRate
	 *            the raiseRate to set
	 */
	public void setRaiseRate(Double raiseRate) {
		this.raiseRate = raiseRate;
	}

}
