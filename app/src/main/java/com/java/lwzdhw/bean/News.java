package com.java.lwzdhw.bean;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.HashMap;
import java.util.Map;

public class News {
	public class ScoreWord {
		public double score;
		public String word;
		
		@Override
		public int hashCode() {
			return word.hashCode();
		}
	}
	private String image;
	public String publishTime;
	private ScoreWord[] keywords;
	private String language;
	public String video;
	public String title;
	public String newsID;
	private String crawlTime;
	public String publisher;
	public String category;
	public String content;
	
	private ScoreWord[] when;
	private ScoreWord[] where;
	private ScoreWord[] who;
	private PersonLink[] persons;
	private OrganizationLink[] organizations;
	private LocationLink[] locations;
	
	boolean mapped = false;
	Map<String, Double> scoreMap;
	Map<String, HyperLink> linkMap;

	public String[] getImage() {
		try {
			if(image.equals("[]")) return null;
			if(image.contains(",")) {
				String[] url = image.substring(1, image.length() - 1).split("[,]");
				return url;
			}
			else {
				return new String[]{image.substring(1, image.length() - 1)};
			}
		} catch (Exception e){
			return null;
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

	public ScoreWord[] getKeywords(){
		return keywords;
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

	public String getAbstract() {
//		String ret;
//		if (content.length() > 20) {
//			ret = content.substring(0, 20);
//			ret = ret + "...";
//		} else {
//			ret = content;
//			ret.replaceAll("\n", " ");
//		}
//		return ret;
		return content;
	}

	@Override
	public String toString() {
		return title + "\n" + content;
	}

	@Override
	public int hashCode() {
		return newsID.hashCode();
	}
}

