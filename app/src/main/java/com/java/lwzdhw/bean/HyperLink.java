package com.java.lwzdhw.bean;

abstract public class HyperLink {
	int count = 0;
	public String linkedURL;
	public String mention;
	
	@Override
	public int hashCode() {
		return mention.hashCode();
	}
}

class PersonLink extends HyperLink {
	
}

class OrganizationLink extends HyperLink {
	
}

class LocationLink extends HyperLink {
	double lng = 0.0;
	double lat = 0.0;
}