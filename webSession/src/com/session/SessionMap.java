package com.session;

import java.io.Serializable;
import java.util.Map;

public class SessionMap implements Serializable {

	private static Map<String, Object> values = new java.util.concurrent.ConcurrentHashMap<String, Object>();
	
	private static long maxInactiveInterval = -1;
	
	private static long sessionTime = 0;
	
	public SessionMap() {
		sessionTime = System.currentTimeMillis();
	}
	
	public void setAttribute(String key, Object value) {
		values.put(key, value);
	}
	
	public Object getAttribute(String key) {
		return values.get(key);
	}
	
	public void removeSession(String key) {
		values.remove(key);
	}
	
	public void clearSession() {
		values.clear();
	}
	
	public static long getSessionTime() {
		return sessionTime;
	}

	public void setMaxInactiveInterval(long maxInactiveInterval) {
		this.maxInactiveInterval = maxInactiveInterval;
	}
	
	public long getMaxInactiveInterval() {
		return this.maxInactiveInterval;
	}
}
