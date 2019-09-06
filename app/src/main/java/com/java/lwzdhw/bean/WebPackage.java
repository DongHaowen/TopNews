package com.java.lwzdhw.bean;

import java.util.HashMap;
import java.util.Map;

public class WebPackage {
	public static final String OP_LOGIN = "login";
	public static final String OP_SIGNUP = "signup";
	public static final String OP_UPDATE_HISTORY = "uhis";
	public static final String OP_GET_HISTORY = "ghis";
	public static final String OP_UPDATE_FAVORITE = "ufav";
	public static final String OP_GET_FAVORITE = "gfav";
	public static final String OP_UPDATE_MODIFY = "umod";
	public static final String OP_GET_MODIFY = "gmod";

	public static final String DATA_OP = "opera";
	public static final String DATA_ID = "userid";
	public static final String DATA_PASS = "passwd";
	public static final String DATA_EXTRA = "extra";
	
	Map<String, String> bundle = new HashMap<String, String>();
	public String getString(final String key) {
		return bundle.get(key);
	}
	
	public void setItem(final String key, final String value) {
		bundle.put(key, value);
	}
}
