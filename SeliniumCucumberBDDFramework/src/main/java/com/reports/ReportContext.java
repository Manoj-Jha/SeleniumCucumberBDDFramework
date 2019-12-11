package com.reports;

import java.util.HashMap;
import java.util.Map;

public class ReportContext {
	private  static Map<String, String> reportContext = new HashMap<String,String>();

    public static void setContext(String key, String value) {
    	reportContext.put(key, value);
    }

    public static String getContext(String key){
        return reportContext.get(key);
    }

    public static Boolean isContains(String key){
        return reportContext.containsKey(key.toString());
    }
}
