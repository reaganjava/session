package com.listener;

import com.session.Session;

public interface SessionListener {
	
	public void sessionCreated(Session session);
	
	public void sessionDestroyed(Session session);
}
