package com.example.topnews.bean;

public class Page {
	String pageSize;
	int total;
	public News[] data;
	String currentPage;
	
	public int getPageSize() {
		return Integer.parseInt(pageSize);
	}
	
	public int getCurrentPage() {
		return Integer.parseInt(currentPage);
	}

	public News getNews(int index){
		try {
			return data[index];
		} catch (Exception e){
			return null;
		}
	}
}
