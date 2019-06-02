package com.huadong.spoon.mybatis.dto;

public class PageQueryDto {
	
	private Integer startIndex = 0;//默认值
	
	private Integer currentPage = 1;
	
	private Integer pageSize = 20;
	
	public PageQueryDto(){}
	
	public PageQueryDto(Integer currentPage, Integer pageSize){
		this.currentPage = currentPage;
		this.pageSize = pageSize;
		this.startIndex = (currentPage - 1) * pageSize;
	}

	public Integer getStartIndex() {
		if(currentPage <= 1){
			return 0;
		}else{
			return this.startIndex = (this.currentPage - 1) * pageSize;
		}
	}

	public void setStartIndex(Integer startIndex) {
		this.startIndex = startIndex;
	}

	public Integer getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(Integer currentPage) {
		this.currentPage = currentPage;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	
}
