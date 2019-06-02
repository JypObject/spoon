package com.huadong.spoon.mybatis.dto;

public class OrderQueryDto {

	private String orderIndex;
	private String orderDir;		//页面排序字段的排序顺序,asc或者desc
	private String orderColumn;		//页面排序字段
	
	public String getOrderIndex() {
		return orderIndex;
	}
	public void setOrderIndex(String orderIndex) {
		this.orderIndex = orderIndex;
	}
	public String getOrderDir() {
		return orderDir;
	}
	public void setOrderDir(String orderDir) {
		this.orderDir = orderDir;
	}
	public String getOrderColumn() {
		return orderColumn;
	}
	public void setOrderColumn(String orderColumn) {
		this.orderColumn = orderColumn;
	}
	
}
