package org.slstudio.hsinchuiot;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.slstudio.hsinchuiot.fragment.UserSiteHomePageFragment;
import org.slstudio.hsinchuiot.model.Device;
import org.slstudio.hsinchuiot.model.IOTMonitorData;
import org.slstudio.hsinchuiot.model.IOTMonitorThreshold;
import org.slstudio.hsinchuiot.model.Site;
import org.slstudio.hsinchuiot.service.ServiceContainer;
import org.slstudio.hsinchuiot.service.SessionService;
import org.slstudio.hsinchuiot.service.http.ForgroundRequestListener;
import org.slstudio.hsinchuiot.service.http.HttpConfig;
import org.slstudio.hsinchuiot.service.http.HttpRequest;
import org.slstudio.hsinchuiot.service.http.NoneAuthedHttpRequest;
import org.slstudio.hsinchuiot.service.http.RequestControl;
import org.slstudio.hsinchuiot.service.http.RequestListener;
import org.slstudio.hsinchuiot.util.IOTLog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class UserMainActivity extends BaseActivity {

	private ViewPager viewPager;
	private FragmentPagerAdapter pagerAdapter;
	private List<UserSiteHomePageFragment> fragments = new ArrayList<UserSiteHomePageFragment>();
	private int currentIndex = -1;
	private List<Site> siteList = new ArrayList<Site>();

	private Handler handler = new Handler(){
		
		@Override
		public void handleMessage(Message msg) {   
			switch (msg.what) {   
                 case Constants.MessageKey.MESSAGE_GET_REALTIME_DATA:   
                     UserSiteHomePageFragment currentFragment = fragments.get(currentIndex);
                     if(currentFragment != null){
                    	 sendQueryRealtimeDataRequest(currentFragment.getSite().getDevice().getDeviceID());
                     }
                     break;   
            }   
            super.handleMessage(msg);   
       }  
	};

	
	
	public List<Site> getSiteList() {
		return siteList;
	}

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_user_main);
		
		initViews();
		handler.post(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				getDeviceList();
			}

		});
	}
	
	

	@Override
	protected void onResume() {
		Toast.makeText(this, "onResume", Toast.LENGTH_SHORT).show();
		if(currentIndex >= 0 && currentIndex < fragments.size() -1){
			UserSiteHomePageFragment fragment = fragments.get(currentIndex);
			sendQueryRealtimeDataRequest(fragment.getSite().getDevice().getDeviceID());
		}
		
		super.onResume();
	}

	@Override
	protected void onPause() {

		Toast.makeText(this, "onPause", Toast.LENGTH_SHORT).show();
		handler.removeMessages(Constants.MessageKey.MESSAGE_GET_REALTIME_DATA);
		super.onPause();
	}

	@Override
	protected void onDestroy() {

		Toast.makeText(this, "onDestroy", Toast.LENGTH_SHORT).show();
		handler.removeMessages(Constants.MessageKey.MESSAGE_GET_REALTIME_DATA);
		super.onDestroy();
	}

	public void moveToPreSite() {
		int currentItem = viewPager.getCurrentItem();
		if (currentItem > 0) {
			viewPager.setCurrentItem(currentItem - 1);
		}
	}

	public void moveToNextSite() {
		int currentItem = viewPager.getCurrentItem();
		if (currentItem < fragments.size() - 1) {
			viewPager.setCurrentItem(currentItem + 1);
		}
	}
	
	public void resendMessage(){
		handler.removeMessages(Constants.MessageKey.MESSAGE_GET_REALTIME_DATA);
		
		if(currentIndex!=-1){
			Message msg = new Message();
			msg.what = Constants.MessageKey.MESSAGE_GET_REALTIME_DATA;
			handler.sendMessageDelayed(msg, 2000);
			//UserSiteHomePageFragment fragment = fragments.get(currentIndex);
			//sendQueryRealtimeDataRequest(fragment.getSite().getDevice().getDeviceID());
		}
	}
	

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			break;
		case R.id.menu_user_main_settings:
			Intent intent = new Intent(
					Constants.Action.HSINCHUIOT_USER_SETTINGS);
			startActivity(intent);
			break;
		case R.id.menu_user_main_logoff:
			
			ServiceContainer.getInstance().getSessionService().setLoginUser(null);
			ServiceContainer.getInstance().getSessionService().setSessionID(null);
			ServiceContainer.getInstance().getSessionService().setSessionValue(Constants.SessionKey.THRESHOLD_BREACH, null);
			ServiceContainer.getInstance().getSessionService().setSessionValue(Constants.SessionKey.THRESHOLD_WARNING, null);
			
			Intent loginIntent = new Intent(
					Constants.Action.HSINCHUIOT_LOGIN);
			startActivity(loginIntent);
			finish();
			break;
		}
		return true;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_user_main, menu);
		setIconEnable(menu, true);
		return true;
	}

	private void setIconEnable(Menu menu, boolean enable) {
		try {
			Class<?> clazz = Class
					.forName("com.android.internal.view.menu.MenuBuilder");
			Method m = clazz.getDeclaredMethod("setOptionalIconsVisible",
					boolean.class);
			m.setAccessible(true);

			// MenuBuilder实现Menu接口，创建菜单时，传进来的menu其实就是MenuBuilder对象(java的多态特征)
			m.invoke(menu, enable);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	private void initViews() {

		viewPager = (ViewPager) findViewById(R.id.vp_user_site_home);
		pagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {

			@Override
			public int getCount() {
				return fragments.size();
			}

			@Override
			public Fragment getItem(int arg0) {
				return fragments.get(arg0);
			}
		};

		viewPager.setAdapter(pagerAdapter);

		viewPager.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				currentIndex = position;
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});

	}

	private void getDeviceList() {
		String sessionID = ServiceContainer.getInstance().getSessionService().getSessionID();

		HttpRequest request = new NoneAuthedHttpRequest(new HttpConfig.GetHttpConfig(),
				Constants.ServerAPIURI.DEVICE_LIST);

		request.addParameter("dataType", "xml");
		request.addParameter("__session_id", sessionID);
		request.addParameter("__page_no", "1");
		request.addParameter("__page_size", "1000");
		request.addParameter("__sort", "-id");

		GetDeviceListListener listener = new GetDeviceListListener(this, true, getString(R.string.common_please_wait));

		ServiceContainer.getInstance().getHttpHandler().doRequest(request, listener);

	}

	private void createHomeFragments() {
		fragments.clear();

		for (int i = 0; i < siteList.size(); i++) {
			Site site = siteList.get(i);
			UserSiteHomePageFragment fragment = new UserSiteHomePageFragment();
			fragment.setSite(site);
			fragment.setIndex(i);
			fragments.add(fragment);
		}

		pagerAdapter.notifyDataSetChanged();

		if (fragments.size() > 0) {
			viewPager.setCurrentItem(0);
			currentIndex = 0;
			UserSiteHomePageFragment fragment = fragments.get(0);
			sendQueryRealtimeDataRequest(fragment.getSite().getDevice().getDeviceID());
		}

	}

	private void sendQueryRealtimeDataRequest(String deviceID) {
		Toast.makeText(this, "send request for site:" + deviceID , Toast.LENGTH_SHORT).show();
		HttpRequest request = new NoneAuthedHttpRequest(new HttpConfig.GetHttpConfig(),
				Constants.ServerAPIURI.GET_REALTIME_DATA);
		String sessionID = ServiceContainer.getInstance().getSessionService().getSessionID();
		request.addParameter("dataType", "xml");
		request.addParameter("__session_id", sessionID);
		request.addParameter("__page_no", "1");
		request.addParameter("__column", "did,sensor,name,value,t");
		request.addParameter("__having_max", "id");
		request.addParameter("__group_by", "did,name");
		request.addParameter("__sort", "-id");
		request.addParameter("did[0]", deviceID);
		GetRealtimeDataListener l = new GetRealtimeDataListener(deviceID);

		ServiceContainer.getInstance().getHttpHandler().doRequest(request, l);
	}

	private class GetRealtimeDataListener implements RequestListener<IOTMonitorData> {
		private RequestControl control;
		private String deviceID;

		public GetRealtimeDataListener(String deviceID) {
			this.deviceID = deviceID;
		}

		@Override
		public void onRequestCancelled() {
			if (control != null)
				control.cancel();

		}

		@Override
		public void onRequestResult(final IOTMonitorData result) {
			//IOTLog.e("xxx", "receive request for:" + deviceID);
			
			final UserSiteHomePageFragment fragment = fragments.get(currentIndex);
			
			if (fragment != null && fragment.getSite().getDevice().getDeviceID().equals(deviceID)) {
				handler.post(new Runnable() {

					@Override
					public void run() {
						//IOTLog.e("xxx", "update ui:" + deviceID);
						fragment.getSite().setMonitorData(result);
						fragment.updateUI();
					}
				});
			}
			Message msg = new Message();
			msg.what = Constants.MessageKey.MESSAGE_GET_REALTIME_DATA;
			
			handler.sendMessageDelayed(msg, 10000);
		}

		@Override
		public void onRequestGetControl(RequestControl control) {
			this.control = control;
		}

		@Override
		public void onRequestStart() {
			// TODO Auto-generated method stub

		}

		@Override
		public void onRequestError(Exception e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onRequestComplete() {
			// TODO Auto-generated method stub

		}
	}

	private class GetDeviceListListener extends ForgroundRequestListener<List<Device>> {

		public GetDeviceListListener(Context context, boolean isShowProgressDialog, String content) {
			super(context, isShowProgressDialog, content);
		}

		@Override
		public void onRequestComplete() {
			super.onRequestComplete();
		}

		@Override
		public void onRequestResult(final List<Device> result) {
			siteList.clear();
			for (Device d : result) {
				Site site = new Site();
				site.setSiteID(d.getDeviceID());
				site.setDevice(d);
				site.setSiteName(d.getSiteName());
				site.setSiteImageFilename("site_" + d.getDeviceSN() + ".png");
				site.setMonitorData(new IOTMonitorData(0, 0, 0));
				siteList.add(site);
			}

			handler.post(new Runnable() {

				@Override
				public void run() {
					createHomeFragments();
				}

			});

		}
	}

}
