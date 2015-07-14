package org.slstudio.hsinchuiot.service;

import org.slstudio.hsinchuiot.AppConfig;
import org.slstudio.hsinchuiot.Constants;

import android.content.Context;


public class PerferenceService {
	PerferenceService() {
    }
    
    public String getSessionId(Context context) {
        return context.getSharedPreferences(AppConfig.PREF_NAME, Context.MODE_PRIVATE).getString(Constants.PreferenceKey.SESSION_ID, "");

    }
    
    public void clear(Context context) {
        context.getSharedPreferences(AppConfig.PREF_NAME, Context.MODE_PRIVATE).edit().clear().commit();
    }

    public void setSessionId(Context context, String sessionId) {
        context.getSharedPreferences(AppConfig.PREF_NAME, Context.MODE_PRIVATE).edit().putString(Constants.PreferenceKey.SESSION_ID, sessionId)
                .commit();
    }
    
    public void setValue(Context context, String key,String value) {
        context.getSharedPreferences(AppConfig.PREF_NAME, Context.MODE_PRIVATE).edit().putString(key, value)
                .commit();
    }
    
    public String getValue(Context context, String key) {
    	 return context.getSharedPreferences(AppConfig.PREF_NAME, Context.MODE_PRIVATE).getString(key, "");
    }
    
    public void setUserId(Context context, String sessionId) {
        context.getSharedPreferences(AppConfig.PREF_NAME, Context.MODE_PRIVATE).edit().putString(Constants.PreferenceKey.USER_ID, sessionId)
                .commit();
    }

	public String getUserId(Context context) {
		 return context.getSharedPreferences(AppConfig.PREF_NAME, Context.MODE_PRIVATE).getString(Constants.PreferenceKey.USER_ID, "");

	}
}
