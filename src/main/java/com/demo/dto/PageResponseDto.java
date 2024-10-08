package com.demo.dto;

import java.util.List;

public class PageResponseDto<T> {
	
	private List<T> content;
    private long totalElements;
    private int totalPages;
    private int currentPage;
    
	public PageResponseDto(List<T> content, long totalElements, int totalPages, int currentPage) {
		super();
		this.content = content;
		this.totalElements = totalElements;
		this.totalPages = totalPages;
		this.currentPage = currentPage;
	}

	public List<T> getContent() {
		return content;
	}

	public void setContent(List<T> content) {
		this.content = content;
	}

	public long getTotalElements() {
		return totalElements;
	}

	public void setTotalElements(long totalElements) {
		this.totalElements = totalElements;
	}

	public int getTotalPages() {
		return totalPages;
	}

	public void setTotalPages(int totalPages) {
		this.totalPages = totalPages;
	}

	public int getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}
    
}
