package com.session;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import com.cached.SpyMemcachedServer;
import com.cached.SpyMemcachedManager;
import com.listener.SessionListener;



public class Session {
	
	
	private SpyMemcachedManager manager = null;
	
	//过期时间为-1时不过期
	private long maxInactiveInterval = -1;
	
	//活动时间
	private long activitiTime = 0;
	
	//sessionID
	private String sessionId;
	
	//事件句柄
	private SessionListener listener = null;

	public Session() {
		try {
			String[][] servs = new String[][] { 
			   { "localhost", "11211" },
			// {"localhost", "11212"}
			};
			List<SpyMemcachedServer> servers = new ArrayList<SpyMemcachedServer>();
			for (int i = 0; i < servs.length; i++) {
				SpyMemcachedServer server = new SpyMemcachedServer();
				server.setIp(servs[i][0]);
				server.setPort(Integer.parseInt(servs[i][1]));
				servers.add(server);
			}
			manager = new SpyMemcachedManager(servers);
			manager.connect();
				
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * 介绍：SESSION初始化建立如果没有MAP创建后放入缓存中
	 * 参数：STRING:SESSIONID
	 **/
	public void init(String sessionId) {
		this.sessionId = sessionId;
		Map<String, Object> sessionValue = (Map<String, Object>) manager.get(sessionId);
		if(sessionValue == null) {
			sessionValue = new HashMap<String, Object>();
			//事件不为空时调用
			if(listener != null) {
				//创建SESSION
				listener.sessionCreated(this);
			}
		}
		manager.set(sessionId, sessionValue, 3000);
		this.activitiTime = System.currentTimeMillis();
	}
	
	/**
	 * 介绍：装入值
	 * 参数：STRING:KEY  OBJECT:VALUE
	 **/
	public void setAttribute(String key, Object value) {
		Map<String, Object> sessionValue = (Map<String, Object>) manager.get(this.sessionId);
		sessionValue.put(key, value);
		manager.set(sessionId, sessionValue, 3000);
	}
	
	/**
	 * 介绍：获取值
	 * 参数：STRING:KEY
	 **/
	public Object getAttribute(String key) {
		Map<String, Object> sessionValue = (Map<String, Object>) manager.get(this.sessionId);
		return sessionValue.get(key);
	}
	
	/**
	 * 介绍：删除指定的值
	 * 参数：STRING:KEY
	 **/
	public void removeSession(String key) {
		Map<String, Object> sessionValue = (Map<String, Object>) manager.get(this.sessionId);
		sessionValue.remove(key);
		manager.set(sessionId, sessionValue, 3000);
	}
	
	/**
	 * 介绍：清除所有的值
	 * 参数：无
	 **/
	public void clearSession() {
		Map<String, Object> sessionValue = (Map<String, Object>) manager.get(this.sessionId);
		sessionValue.clear();
		sessionValue = null;
		manager.set(sessionId, sessionValue, 3000);
	}
	
	/**
	 * 介绍：销毁SESSION
	 * 参数：无
	 **/
	public void sessionDestroy() {
		if(listener != null) {
			//销毁SESSION
			listener.sessionDestroyed(this);
		}
		manager.delete(sessionId);
	}
	
	public long getActivitiTime() {
		return activitiTime;
	}

	public void setActivitiTime(long activitiTime) {
		this.activitiTime = activitiTime;
	}

	public void setMaxInactiveInterval(long maxInactiveInterval) {
		this.maxInactiveInterval = maxInactiveInterval;
	}
	
	public long getMaxInactiveInterval() {
		return this.maxInactiveInterval;
	}
	
	public String getSessionId() {
		return sessionId;
	}

	public void addSessionListener(SessionListener listener) {
		this.listener = listener;
	}

	public SessionListener getListener() {
		return listener;
	}

}
