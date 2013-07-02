package com.test;

import java.util.UUID;

import com.session.SessionManager;
import com.session.SessionMap;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String userId = UUID.randomUUID().toString();
		SessionManager sessionManager = new SessionManager();
		SessionMap session = sessionManager.getSession(userId);
		session.setMaxInactiveInterval(3000);
		session.setAttribute("reaganjava", 90000);
		System.out.println(session.getAttribute("reaganjava"));
	}

}
