package com.example.topnews.bean;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.HashMap;
import java.util.Map;

public class News {
	class ScoreWord {
		double score;
		String word;
		
		@Override
		public int hashCode() {
			return word.hashCode();
		}
	}
	String image;
	public String publishTime;
	ScoreWord[] keywords;
	String language;
	public String video;
	public String title;
	public String newsID;
	String crawlTime;
	public String publisher;
	public String category;
	public String content;
	
	ScoreWord[] when; 
	ScoreWord[] where; 
	ScoreWord[] who; 
	PersonLink[] persons; 
	OrganizationLink[] organizations; 
	LocationLink[] locations;
	
	boolean mapped = false;
	Map<String, Double> scoreMap;
	Map<String, HyperLink> linkMap;

	public String[] getImage() {
		if(image.equals("[]")) return null;
		if(image.contains(",")) {
			String[] url = image.substring(1, image.length() - 1).split("[,]");
			return url;
		}
		else {
			return new String[]{image.substring(1, image.length() - 1)};
		}
	}
	
	void buildMap() {
		scoreMap = new HashMap<String, Double>();
		linkMap = new HashMap<String, HyperLink>();
		for (ScoreWord v : when) {
			scoreMap.put(v.word, v.score);
		}
		for (ScoreWord v : who) {
			scoreMap.put(v.word, v.score);
		}
		for (ScoreWord v : where) {
			scoreMap.put(v.word, v.score);
		}
		
		for (HyperLink link : persons) {
			linkMap.put(link.mention, link);
		}
		for (HyperLink link : organizations) {
			linkMap.put(link.mention, link);
		}
		for (HyperLink link : locations) {
			linkMap.put(link.mention, link);
		}
		mapped = true;
	}
	
	double getScore(final String key) {
		if (!mapped) buildMap();
		return scoreMap.get(key);
	}
	
	HyperLink getHyperLink(final String key) {
		if (!mapped) buildMap();
		return linkMap.get(key);
	}

	public String toJson() {
		GsonBuilder builder = new GsonBuilder();
		Gson gson = builder.create();
		return gson.toJson(this);
	}

	public Map<String, HyperLink> getLinkMap(){
		if(!mapped) buildMap();
		return linkMap;
	}

	@Override
	public String toString() {
		return title + "\n" + content;
	}
}

