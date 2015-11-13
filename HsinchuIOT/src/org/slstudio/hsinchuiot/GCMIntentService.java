package org.slstudio.hsinchuiot;

import org.slstudio.hsinchuiot.service.IOTException;
import org.slstudio.hsinchuiot.service.ServiceContainer;
import org.slstudio.hsinchuiot.util.IOTLog;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.google.android.gcm.GCMBaseIntentService;

public class GCMIntentService extends GCMBaseIntentService{

	public GCMIntentService(){
		super(AppConfig.GCM_SENDER_ID);
	}
	
	@Override
	protected void onError(Context context, String error) {
		IOTLog.e("GCMIntentService", "GCM onError:" + error);	
		Toast.makeText(context, context.getString(R.string.error_message_gcm_register_failed), Toast.LENGTH_SHORT).show();
	}

	@Override
	protected void onMessage(Context context, Intent intent) {
		String message = intent.getDataString();
		IOTLog.d("GCMIntentService", "GCM onMessage:" + message);	
		Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
	}

	@Override
	protected void onRegistered(Context context, String regID) {
		IOTLog.d("GCMIntentService", "GCM onRegistered:" + regID);
		
		try {
			ServiceContainer.getInstance().getPushService().registerToServer(regID);
		} catch (IOTException e) {
			IOTLog.e("GCMIntentService", "GCM register to server failed" , e);
			Toast.makeText(context, context.getString(R.string.error_message_gcm_register_failed), Toast.LENGTH_SHORT).show();
		}
		
	}

	@Override
	protected void onUnregistered(Context context, String regID) {
		IOTLog.d("GCMIntentService", "GCM onUnregistered:" + regID);
	}

}
