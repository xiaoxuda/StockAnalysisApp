/**
 * 
 */
package com.orditech.stockanalysis.enums;

/**
 * @author kimi
 *
 */
public enum FinancailStatementAttrEnum {
	//属性名称
	ATTR_SGPR("sgpr","销售毛利率"),
	ATTR_DRAR("dtar","资产负债率"),
	ATTR_TOI("toi","营业总收入"),
	ATTR_MP("mp","净利润"),
	ATTR_SE("se","股东权益");
	
	private String name;
	private String comment;
	
	private FinancailStatementAttrEnum(String name, String comment){
		this.name=name;
		this.comment=comment;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the comment
	 */
	public String getComment() {
		return comment;
	}
	
	
}
