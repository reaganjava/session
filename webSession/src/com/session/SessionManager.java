package com.session;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class SessionManager {
	
	private static Map<String, Session> sessionMap = new ConcurrentHashMap<String, Session>();
	
	private static ExecutorService exs = null;
	
	static {
		exs = Executors.newSingleThreadExecutor();
		exs.execute(new Runnable() {

			@Override
			public void run() {
				while(!Thread.interrupted()) {
					System.out.println("clear time out");
					List<String> clearList = new ArrayList<String>();
					for(String sessionId : sessionMap.keySet()) {
						Session session = sessionMap.get(sessionId);
						if(session.getMaxInactiveInterval() != -1) {
							long currentTime = System.currentTimeMillis();
							long activitiTime = session.getActivitiTime();
							//SESSION过期时清除
							if((currentTime - activitiTime) >=  session.getMaxInactiveInterval()) {
								session.sessionDestroy();
								//记录清除的ID
								clearList.add(sessionId);
							}
						}
					}
					for(String sessionId : clearList) {
						//清除MAP信息
						sessionMap.remove(sessionId);
					}
					try {
						Thread.sleep(10000);
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
						e.printStackTrace();
					}
				}
				
			}
			
		});
	}
	/**
	 * 介绍：通过系统本生的SESSIONID来创建分布式内存SESSION
	 * 参数：STRING:SESSION
	 **/
	public static Session getSession(String sessionId){
		//每个SESSIONID创建一个实例
		Session session = sessionMap.get(sessionId);
		
		if(session == null) {
			session = new Session();
			session.init(sessionId);
			sessionMap.put(sessionId, session);
		}
		
		return session;
	}
	
	public static void main(String[] args) {
		SessionManager.getSession("xxxx");
	}
}
