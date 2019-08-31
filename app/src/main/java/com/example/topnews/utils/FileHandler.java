package com.example.topnews.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.example.topnews.bean.News;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


public class FileHandler {
	final static String home = "/data/user/0/com.tech.oddnews/files/";

	public void createNewsDir(final String newsID){
		if(newsCached(newsID)) return;
		File dir = new File(getAbsPath(newsID));
		dir.mkdir();
	}

	public boolean newsCached(final String newsID){
		return exists(getAbsPath(newsID));
	}

	public News load(final String name) throws FileNotFoundException {
		GsonBuilder builder = new GsonBuilder();
		Gson gson = builder.create();
		File file = new File(name);
		if(!file.exists()) return null;
		BufferedReader buffer = new BufferedReader(new FileReader(name));
		News news = gson.fromJson(buffer, News.class);
		return news;
	}
	
	public void store(final News news) throws IOException {
		GsonBuilder builder = new GsonBuilder();
		Gson gson = builder.create();
		String name = getAbsPath(news.newsID);
		File file = new File(name);
		if(!file.exists()) file.createNewFile();
		BufferedWriter buffer = new BufferedWriter(new FileWriter(name));
		buffer.write(gson.toJson(news));
		buffer.close();
	}
	
	public boolean exists(final String name) {
		try {
			File file = new File(name);
			return file == null;
		} catch (Exception e) {
			return false;
		}
	}

	private static String getAbsPath(final String path){
		return home + path;
	}
}
