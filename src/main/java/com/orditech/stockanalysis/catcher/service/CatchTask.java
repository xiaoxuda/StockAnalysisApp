package com.orditech.stockanalysis.catcher.service;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public class CatchTask implements Serializable{
	private static final long serialVersionUID = 6388382222106508105L;
	
	public String 				type;//任务类型
	public String 				url;//目标url
	public Map<String,Object> 	info;//任务信息
	
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}

	public Object getInfoValue(String key) {
		if(this.info==null){
			return null;
		}
		return this.info.get(key);
	}
	public void addInfo(String key,Object data) {
		if(this.info==null){
			this.info=new HashMap<String,Object>();
		}
		this.info.put(key,data);
	}
	
	public Map<String,Object> getInfo(){
		return this.info;
	}
	
	public void setInfo(Map<String,Object> info){
		this.info = info;
	}
	
	@Override
	public String toString(){
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
