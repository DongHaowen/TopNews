package com.java.lwzdhw.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.java.lwzdhw.bean.News;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


public class FileHandler {
	final static String home = "/data/user/0/com.example.topnews/files/";

	public FileHandler(){
		if(!new File(home).exists())
			new File(home).mkdirs();
	}

	public boolean newsCached(final String newsID){
		return exists(newsID);
	}

	public News load(final String newsID) throws FileNotFoundException {
		GsonBuilder builder = new GsonBuilder();
		Gson gson = builder.create();
		File file = new File(getAbsPath(newsID));
		if(!file.exists()) return null;
		BufferedReader buffer = new BufferedReader(new FileReader(file));
		News news = gson.fromJson(buffer, News.class);
		return news;
	}
	
	public void store(final News news) throws IOException {
		GsonBuilder builder = new GsonBuilder();
		Gson gson = builder.create();
		String name = getAbsPath(news.newsID );
		File file = new File(getAbsPath(news.newsID));
		// if(!file.exists()) file.createNewFile();
		BufferedWriter buffer = new BufferedWriter(new FileWriter(file));
		buffer.write(gson.toJson(news));
		buffer.close();
	}
	
	public boolean exists(final String newID) {
		try {
			File file = new File(getAbsPath(newID));
			return file.exists();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	private static String getAbsPath(final String newsID){
		return home + newsID+".json";
	}
}
