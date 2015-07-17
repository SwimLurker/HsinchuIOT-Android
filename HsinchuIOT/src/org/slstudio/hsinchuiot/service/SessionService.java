package org.slstudio.hsinchuiot.service;

import java.util.HashMap;
import java.util.Map;

import org.slstudio.hsinchuiot.model.User;

public class SessionService {
	public static final String THRESHOLD_WARNING = "org.slstudio.hsinchuiot.THRESHOLD_WARING";
	public static final String THRESHOLD_BREACH = "org.slstudio.hsinchuiot.THRESHOLD_BREACH";
	
	
	private Map<String, Object> sessionValues = new HashMap<String, Object>();
	private String sessionID;
	private User loginUser;
	

	public String getSessionID() {
		return sessionID;
	}

	public void setSessionID(String sessionID) {
		this.sessionID = sessionID;
	}

	public User getLoginUser() {
		return loginUser;
	}

	public void setLoginUser(User loginUser) {
		this.loginUser = loginUser;
	}

	public Object getSessionValue(String key) {
		return sessionValues.get(key);
	}

	public void setSessionValue(String key, Object value) {
		if(value == null){
			sessionValues.remove(key);
		}else{
			sessionValues.put(key, value);
		}
	}

}
