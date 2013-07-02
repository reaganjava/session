package com.session;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

import com.cached.SpyMemcachedServer;
import com.cached.SpyMemcachedManager;
import com.util.EncryptionByMD5;


public class SessionManager {
	
	private static SpyMemcachedManager manager = null;
	
	public SessionManager() {
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
			Thread thread = new Thread(new SessionThread());
			thread.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	public SessionMap getSession(String userId) {
		String sessionId = EncryptionByMD5.getMD5(userId);
		Map<String, SessionMap> sessionContext = (Map<String, SessionMap>) manager.get("CONTEXT");
		if(sessionContext == null) {
			sessionContext = new ConcurrentHashMap<String, SessionMap>();
		}
		SessionMap session = new SessionMap();
		sessionContext.put(sessionId, session);
		manager.set("CONTEXT", sessionContext, 3000);
		return session;
	}
	
	class SessionThread implements Runnable {

		@Override
		public void run() {
			while(true) {
				Queue<String> timeoutQueue = new LinkedList<String>();
				
				Map<String, SessionMap> sessionContext = (Map<String, SessionMap>) manager.get("CONTEXT");
				if(sessionContext != null) {
					for(String sessionId : sessionContext.keySet()) {
						SessionMap session = sessionContext.get(sessionId);
						if(session.getMaxInactiveInterval() != -1) {
							long inactiveInterval = session.getMaxInactiveInterval();
							long currentTime = System.currentTimeMillis();
							long sessionTime = session.getSessionTime();
							System.out.println(currentTime + ":" + sessionTime + ":" + inactiveInterval);
							if((currentTime - sessionTime) > inactiveInterval) {
								session.clearSession();
								timeoutQueue.add(sessionId);
							}
						}
					}
					for(int i = 0; i < timeoutQueue.size(); i++) {
						String sessionId = timeoutQueue.poll();
						System.out.println("remove:" + sessionId);
						sessionContext.remove(sessionId);
					}
					timeoutQueue.clear();
					timeoutQueue = null;
					
					manager.set("CONTEXT", sessionContext, 3000);
				}
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
