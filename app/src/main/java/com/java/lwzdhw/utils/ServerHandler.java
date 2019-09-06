package com.java.lwzdhw.utils;

import android.util.Log;

import com.java.lwzdhw.bean.Page;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.Proxy;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;

public class ServerHandler {
	private final static String TAG = "NewsFragment";
	final String requestHead = "https://api2.newsminer.net/svc/news/queryNewsList?";
	int size = 0;
	String startDate = "";
	String endDate = "";
	String words = "";
	String categories = "";
	
	final int outOfTimeLimit = 10*1000;
	
	private String getRequest() throws UnsupportedEncodingException {
		return requestHead + "size=" + size + "&" + 
				"startDate=" + startDate + "&" +
				"endDate=" + endDate + "&" +
				"words=" + URLEncoder.encode(words,"UTF-8") + "&" + 
				"categories=" + URLEncoder.encode(categories,"UTF-8");
	}
	
	public Proxy setProxy() throws UnknownHostException {
		SocketAddress sa = new InetSocketAddress(InetAddress.getLocalHost(), 80);
		System.out.println(InetAddress.getLocalHost());
		return new Proxy(Proxy.Type.HTTP, sa);
	}
	
	public void setStartDate(final String start) {
		startDate = start;
	}
	
	public void setEndDate(final String end) {
		endDate = end;
	}
	
	public void setSize(final int s) {
		size = s;
	}
	
	public void setWords(final String w) {
		words = w;
	}
	
	public void setCategories(final String cate) {
		categories = cate;
	}
	
	public String getJson() throws IOException {
		URL url = new URL(getRequest());
		Log.d(TAG, "getJson: " + getRequest());

		InputStreamReader reader = new InputStreamReader(url.openStream());
		BufferedReader buffer = new BufferedReader(reader);
		
		String json = "";

		json = buffer.readLine();
		buffer.close();
		return json;
	}

	public Page getPage(){
		String json = null;
		try {
			json = getJson();
		} catch (IOException e){
			return null;
		}
		// Log.d("ServerHandler",json);
		GsonBuilder builder = new GsonBuilder();
		Gson gson = builder.create();
		Page page = gson.fromJson(json, Page.class);
		return page;
	}

}