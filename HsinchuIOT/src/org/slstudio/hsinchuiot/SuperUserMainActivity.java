package org.slstudio.hsinchuiot;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.slstudio.hsinchuiot.model.Device;
import org.slstudio.hsinchuiot.model.DeviceWithAggregationData;
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

import org.slstudio.hsinchuiot.ui.adapter.SiteListViewAdapter;
import org.slstudio.hsinchuiot.util.ImageUtil;
import org.slstudio.hsinchuiot.util.ReportUtil;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.DateUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.State;
import com.handmark.pulltorefresh.library.PullToRefreshSlideListView;
import com.handmark.pulltorefresh.library.extras.SoundPullEventListener;
import com.mobeta.android.dslv.DragSortController;
import com.mobeta.android.dslv.DragSortListView;

public class SuperUserMainActivity extends BaseActivity {
	public static final String SELECTED_SITE = "org.slstudio.hsinchuiot.SELECTED_SITE";
	public static final String WARNING_THRESHOLD = "org.slstudio.hsinchuiot.WARNING_THRESHOLD";
	public static final String ALARM_THRESHOLD = "org.slstudio.hsinchuiot.ALARM_THRESHOLD";

	private PullToRefreshSlideListView siteListView;
	private TextView tv8hr;
	private TextView tv1hr;

	private SiteListViewAdapter siteListViewAdatper = new SiteListViewAdapter(
			this, new ArrayList<Site>(), new IOTMonitorThreshold(),
			new IOTMonitorThreshold());

	private List<Device> deviceList = new ArrayList<Device>();

	private List<DeviceWithAggregationData> deviceWithAggDataList = new ArrayList<DeviceWithAggregationData>();

	private Handler handler;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_superuser_main);

		IOTMonitorThreshold warningThreshold = (IOTMonitorThreshold) ServiceContainer
				.getInstance().getSessionService()
				.getSessionValue(SessionService.THRESHOLD_WARNING);
		IOTMonitorThreshold breachThreshold = (IOTMonitorThreshold) ServiceContainer
				.getInstance().getSessionService()
				.getSessionValue(SessionService.THRESHOLD_BREACH);

		if (warningThreshold != null) {
			siteListViewAdatper.setWarningThreshold(warningThreshold);
		}
		if (breachThreshold != null) {
			siteListViewAdatper.setBreachThreshold(breachThreshold);
		}

		initViews();

		handler = new Handler();
		handler.post(new Runnable() {

			@Override
			public void run() {
				// use new api
				//getDeviceList();
				getDeviceListWithAggregationData();
			}

		});

	}

	@Override
	protected void onResume() {
		siteListViewAdatper.notifyDataSetChanged();
		super.onResume();
	}

	private void getDeviceList() {
		String sessionID = ServiceContainer.getInstance().getSessionService()
				.getSessionID();

		HttpRequest request = new NoneAuthedHttpRequest(
				new HttpConfig.GetHttpConfig(),
				Constants.ServerAPIURI.DEVICE_LIST);

		request.addParameter("dataType", "xml");
		request.addParameter("__session_id", sessionID);
		request.addParameter("__page_no", "1");
		request.addParameter("__page_size", "1000");
		request.addParameter("__sort", "-id");

		GetDeviceListListener listener = new GetDeviceListListener(this, true,
				getString(R.string.common_please_wait));

		ServiceContainer.getInstance().getHttpHandler()
				.doRequest(request, listener);

	}

	private void getDeviceListWithAggregationData() {
		String sessionID = ServiceContainer.getInstance().getSessionService()
				.getSessionID();

		HttpRequest request = new NoneAuthedHttpRequest(
				new HttpConfig.GetHttpConfig(),
				Constants.ServerAPIURI.GET_DEVICE_LIST_WITH_AGG_DATA);

		request.addParameter("dataType", "xml");
		request.addParameter("__session_id", sessionID);

		GetDeviceListWithAggDataListener listener = new GetDeviceListWithAggDataListener(
				this, true, getString(R.string.common_please_wait));

		ServiceContainer.getInstance().getHttpHandler()
				.doRequest(request, listener);

	}

	private void initViews() {
		// title
		tv8hr = (TextView) findViewById(R.id.id_topinfo_tv_timeperiod_8hr);
		tv1hr = (TextView) findViewById(R.id.id_topinfo_tv_timeperiod_1hr);

		Calendar c8[] = ReportUtil.get8HoursTimePeriod();
		Calendar c1[] = ReportUtil.get1HourTimePeriod();

		String str8hr = ReportUtil.get8HoursTimePeriodString(c8[0], c8[1]);
		String str1hr = ReportUtil.get1HourTimePeriodString(c1[0], c1[1]);

		tv8hr.setText(this.getResources().getString(
				R.string.superuser_main_8hrtitle)
				+ str8hr);
		tv1hr.setText(this.getResources().getString(
				R.string.superuser_main_1hrtitle)
				+ str1hr);

		// list
		siteListView = (PullToRefreshSlideListView) findViewById(R.id.id_lv_superuser_site);

		// siteListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		siteListView.setVisibility(View.VISIBLE);
		siteListView.setAdapter(siteListViewAdatper);

		siteListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				if (position > 0) {
					// arg1.setSelected(true);
					siteListViewAdatper.setSelectedPosition(position - 1);
					siteListViewAdatper.notifyDataSetChanged();
					Intent intent = new Intent(
							Constants.Action.HSINCHUIOT_SUPERUSER_SITEDETAIL);
					intent.putExtra(SELECTED_SITE,
							(Site) siteListViewAdatper.getItem(position - 1));
					intent.putExtra(
							WARNING_THRESHOLD,
							(IOTMonitorThreshold) ServiceContainer
									.getInstance()
									.getSessionService()
									.getSessionValue(
											SessionService.THRESHOLD_WARNING));
					intent.putExtra(
							ALARM_THRESHOLD,
							(IOTMonitorThreshold) ServiceContainer
									.getInstance()
									.getSessionService()
									.getSessionValue(
											SessionService.THRESHOLD_BREACH));
					startActivity(intent);
				}
			}

		});

		DragSortListView actualLV = siteListView.getRefreshableView();
		// actrualLV.setDragEnabled(false);

		DragSortController controller = new DragSortController(actualLV);
		controller.setDragHandleId(R.id.drag_handle);
		controller.setClickRemoveId(R.id.click_remove);
		controller.setRemoveEnabled(true);
		controller.setSortEnabled(false);
		controller.setDragInitMode(DragSortController.ON_DRAG);
		controller.setRemoveMode(DragSortController.FLING_REMOVE);

		actualLV.setOnTouchListener(controller);
		actualLV.setDragEnabled(true);
		actualLV.setRemoveListener(new DragSortListView.RemoveListener() {

			@Override
			public void remove(int position) {
				// arg1.setSelected(true);
				siteListViewAdatper.setSelectedPosition(position);
				siteListViewAdatper.notifyDataSetChanged();
				Intent intent = new Intent(
						Constants.Action.HSINCHUIOT_SUPERUSER_SITEDETAIL);
				intent.putExtra(SELECTED_SITE,
						(Site) siteListViewAdatper.getItem(position));

				intent.putExtra(
						WARNING_THRESHOLD,
						(IOTMonitorThreshold) ServiceContainer
								.getInstance()
								.getSessionService()
								.getSessionValue(
										SessionService.THRESHOLD_WARNING));

				intent.putExtra(
						ALARM_THRESHOLD,
						(IOTMonitorThreshold) ServiceContainer
								.getInstance()
								.getSessionService()
								.getSessionValue(
										SessionService.THRESHOLD_BREACH));
				startActivity(intent);
				overridePendingTransition(R.anim.slide_in_right2,
						R.anim.slide_out_left2);
			}

		});

		siteListView
				.setOnRefreshListener(new OnRefreshListener<DragSortListView>() {
					@Override
					public void onRefresh(
							PullToRefreshBase<DragSortListView> refreshView) {
						String label = DateUtils.formatDateTime(
								getApplicationContext(),
								System.currentTimeMillis(),
								DateUtils.FORMAT_SHOW_TIME
										| DateUtils.FORMAT_SHOW_DATE
										| DateUtils.FORMAT_ABBREV_ALL);

						// Update the LastUpdatedLabel
						refreshView.getLoadingLayoutProxy()
								.setLastUpdatedLabel(label);

						// Do work to refresh the list here.
						handler.post(new Runnable() {

							@Override
							public void run() {
								// TODO Auto-generated method stub
								//getDeviceList();
								getDeviceListWithAggregationData();
							}

						});

					}
				});

		SoundPullEventListener<DragSortListView> soundListener = new SoundPullEventListener<DragSortListView>(
				this);
		soundListener.addSoundEvent(State.PULL_TO_REFRESH, R.raw.pull_event);
		soundListener.addSoundEvent(State.RESET, R.raw.reset_sound);
		soundListener.addSoundEvent(State.REFRESHING, R.raw.refreshing_sound);
		siteListView.setOnPullEventListener(soundListener);

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			break;
		case R.id.menu_superuser_main_settings:
			Intent intent = new Intent(
					Constants.Action.HSINCHUIOT_SUPERUSER_SETTINGS);
			startActivity(intent);
			break;
		case R.id.menu_superuser_main_logoff:
			
			ServiceContainer.getInstance().getSessionService().setLoginUser(null);
			ServiceContainer.getInstance().getSessionService().setSessionID(null);
			ServiceContainer.getInstance().getSessionService().setSessionValue(SessionService.THRESHOLD_BREACH, null);
			ServiceContainer.getInstance().getSessionService().setSessionValue(SessionService.THRESHOLD_WARNING, null);
			
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
		getMenuInflater().inflate(R.menu.activity_superuser_main, menu);
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

	private void updateListView() {

		Random r = new Random();
		r.setSeed(System.currentTimeMillis());

		List<Site> sites = new ArrayList<Site>();
		for (Device d : deviceList) {
			Site site = new Site();
			site.setDevice(d);
			site.setSiteName(d.getSiteName());
			site.setSiteImageFilename("site_" + d.getDeviceSN() + ".png");
			site.setMonitorData(new IOTMonitorData(900 + r.nextInt(200), 20 + r
					.nextInt(20), r.nextInt(100)));
			sites.add(site);
		}
		siteListViewAdatper.setItems(sites);

		siteListViewAdatper.notifyDataSetChanged();
	}

	private void sendQueryRealtimeDataRequest(String deviceID) {
		HttpRequest request = new NoneAuthedHttpRequest(
				new HttpConfig.GetHttpConfig(),
				Constants.ServerAPIURI.GET_REALTIME_DATA);
		String sessionID = ServiceContainer.getInstance().getSessionService()
				.getSessionID();
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

	private void sendQuery1HourAggDataRequest(String deviceID, String from,
			String to) {
		HttpRequest request = new NoneAuthedHttpRequest(
				new HttpConfig.GetHttpConfig(),
				Constants.ServerAPIURI.GET_HOUR_AGG_DATA);
		String sessionID = ServiceContainer.getInstance().getSessionService()
				.getSessionID();
		request.addParameter("dataType", "xml");
		request.addParameter("__session_id", sessionID);
		request.addParameter("__page_no", "1");
		request.addParameter("__page_size", "10000");
		request.addParameter("__column",
				"did%2Csensor%2Cname%2Cvalue%2Chour_in_epoch");
		request.addParameter("__group_by",
				"did%2Csensor%2Cname%2Chour_in_epoch");
		request.addParameter("__sort", "-id");
		request.addParameter("hour_in_epoch__from", from);
		request.addParameter("hour_in_epoch__to", to);
		request.addParameter("did%5B0%5D", deviceID);
		Get1HourAggDataListener l = new Get1HourAggDataListener(deviceID, from,
				to);

		ServiceContainer.getInstance().getHttpHandler().doRequest(request, l);
	}

	private void sendQuery8HoursAggDataRequest(String deviceID, String from,
			String to) {
		HttpRequest request = new NoneAuthedHttpRequest(
				new HttpConfig.GetHttpConfig(),
				Constants.ServerAPIURI.GET_HOURS_AGG_DATA);
		String sessionID = ServiceContainer.getInstance().getSessionService()
				.getSessionID();
		request.addParameter("dataType", "xml");
		request.addParameter("__session_id", sessionID);
		request.addParameter("__page_no", "1");
		request.addParameter("__page_size", "10000");
		request.addParameter("__column",
				"did%2Csensor%2Cname%2Cvalue%2Chours_in_epoch");
		request.addParameter("__group_by",
				"did%2Csensor%2Cname%2Chours_in_epoch");
		request.addParameter("__sort", "-id");
		request.addParameter("hours_in_epoch__from", from);
		request.addParameter("hours_in_epoch__to", to);

		request.addParameter("did[0]", deviceID);
		Get8HoursAggDataListener l = new Get8HoursAggDataListener(deviceID,
				from, to);

		ServiceContainer.getInstance().getHttpHandler().doRequest(request, l);
	}

	private void prepareThumbnailImage(String siteImageFilename) {
		String imageDir = Constants.ImageLoader.IMAGE_ENGINE_CACHE;
		String thumbnailDir = imageDir + "/thumbnail";

		File dir = new File(thumbnailDir);
		if (!dir.exists()) {
			dir.mkdirs();
		}

		File thumbnailFile = new File(thumbnailDir + "/" + siteImageFilename);
		
		if(thumbnailFile.exists()&&thumbnailFile.isFile()){
			return;
		}
		
		File originalFile = new File(imageDir+"/" + siteImageFilename);
		if(!originalFile.exists() ||!originalFile.isFile()){
			return;
		}
		

		Bitmap thumbnailImage = ImageUtil.getImageThumbnail(imageDir + "/" + siteImageFilename, 128, 128);
		ImageUtil.writeBitmapToFile(thumbnailImage, thumbnailDir + "/" + siteImageFilename);

	}
	
	private class GetDeviceListListener extends
			ForgroundRequestListener<List<Device>> {

		public GetDeviceListListener(Context context,
				boolean isShowProgressDialog, String content) {
			super(context, isShowProgressDialog, content);
		}


		@Override
		public void onRequestComplete() {
			handler.post(new Runnable() {

				@Override
				public void run() {
					if (siteListView.isRefreshing()) {
						siteListView.onRefreshComplete();
					}
				}
			});

			super.onRequestComplete();
		}

		@Override
		public void onRequestResult(final List<Device> result) {
			deviceList = result;
			List<Site> sites = new ArrayList<Site>();
			for (Device d : deviceList) {
				Site site = new Site();
				site.setSiteID(d.getDeviceID());
				site.setDevice(d);
				site.setSiteName(d.getSiteName());
				site.setSiteImageFilename("site_" + d.getDeviceSN() + ".png");
				site.setMonitorData(new IOTMonitorData(0, 0, 0));
				sites.add(site);
				
				prepareThumbnailImage(site.getSiteImageFilename());
			}
			siteListViewAdatper.setItems(sites);

			final Calendar c8[] = ReportUtil.get8HoursTimePeriod();
			final Calendar c1[] = ReportUtil.get1HourTimePeriod();

			String time8hFrom = ReportUtil.getServerTimeString(c8[0]);
			String time8hTo = ReportUtil.getServerTimeString(c8[1]);

			String time1hFrom = ReportUtil.getServerTimeString(c1[0]);
			String time1hTo = ReportUtil.getServerTimeString(c1[1]);

			for (Device d : deviceList) {

				sendQuery1HourAggDataRequest(d.getDeviceID(), time1hFrom,
						time1hTo);
				sendQuery8HoursAggDataRequest(d.getDeviceID(), time8hFrom,
						time8hTo);

			}

			handler.post(new Runnable() {

				@Override
				public void run() {
					String str8hr = ReportUtil.get8HoursTimePeriodString(c8[0],
							c8[1]);
					String str1hr = ReportUtil.get1HourTimePeriodString(c1[0],
							c1[1]);

					tv8hr.setText(SuperUserMainActivity.this.getResources()
							.getString(R.string.superuser_main_8hrtitle)
							+ str8hr);
					tv1hr.setText(SuperUserMainActivity.this.getResources()
							.getString(R.string.superuser_main_1hrtitle)
							+ str1hr);

					siteListViewAdatper.notifyDataSetChanged();
				}

			});

		}


	}

	private class GetDeviceListWithAggDataListener extends
			ForgroundRequestListener<List<DeviceWithAggregationData>> {
		
		public GetDeviceListWithAggDataListener(Context context,
				boolean isShowProgressDialog, String content) {
			super(context, isShowProgressDialog, content);
		}

		

		@Override
		public void onRequestComplete() {
			handler.post(new Runnable() {

				@Override
				public void run() {
					if (siteListView.isRefreshing()) {
						siteListView.onRefreshComplete();
					}
				}
			});

			super.onRequestComplete();
		}

		@Override
		public void onRequestResult(final List<DeviceWithAggregationData> result) {
			deviceWithAggDataList = result;
			List<Site> sites = new ArrayList<Site>();
			for (DeviceWithAggregationData dWAD : deviceWithAggDataList) {
				Device d = dWAD.getDevice();
				IOTMonitorData aggrData = dWAD.getAggregationData();
				
				Site site = new Site();
				site.setSiteID(d.getDeviceID());
				site.setDevice(d);
				site.setSiteName(d.getSiteName());
				site.setSiteImageFilename("site_" + d.getDeviceSN() + ".png");
				site.setMonitorData(aggrData);
				sites.add(site);
			}
			siteListViewAdatper.setItems(sites);

			final Calendar c8[] = ReportUtil.get8HoursTimePeriod();
			final Calendar c1[] = ReportUtil.get1HourTimePeriod();


			handler.post(new Runnable() {

				@Override
				public void run() {
					String str8hr = ReportUtil.get8HoursTimePeriodString(c8[0],
							c8[1]);
					String str1hr = ReportUtil.get1HourTimePeriodString(c1[0],
							c1[1]);

					tv8hr.setText(SuperUserMainActivity.this.getResources()
							.getString(R.string.superuser_main_8hrtitle)
							+ str8hr);
					tv1hr.setText(SuperUserMainActivity.this.getResources()
							.getString(R.string.superuser_main_1hrtitle)
							+ str1hr);

					siteListViewAdatper.notifyDataSetChanged();
				}

			});

		}

	}

	private class GetRealtimeDataListener implements
			RequestListener<IOTMonitorData> {
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
			Site s = siteListViewAdatper.getSiteByDeviceID(deviceID);
			if (s != null) {
				s.setMonitorData(result);
			}
			handler.post(new Runnable() {

				@Override
				public void run() {
					siteListViewAdatper.notifyDataSetChanged();
				}
			});

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

	private class Get1HourAggDataListener implements
			RequestListener<Map<String, IOTMonitorData>> {
		private RequestControl control;
		private String deviceID;
		private String from;
		private String to;

		public Get1HourAggDataListener(String deviceID, String from, String to) {
			this.deviceID = deviceID;
			this.from = from;
			this.to = to;
		}

		@Override
		public void onRequestCancelled() {
			if (control != null)
				control.cancel();

		}

		@Override
		public void onRequestResult(final Map<String, IOTMonitorData> result) {
			Site s = siteListViewAdatper.getSiteByDeviceID(deviceID);
			if (s != null) {
				if (result.containsKey(from)) {
					s.getMonitorData().setTemperature(
							result.get(from).getTemperature());
					s.getMonitorData().setHumidity(
							result.get(from).getHumidity());
				}

			}
			handler.post(new Runnable() {

				@Override
				public void run() {
					siteListViewAdatper.notifyDataSetChanged();
				}
			});

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

	private class Get8HoursAggDataListener implements
			RequestListener<Map<String, IOTMonitorData>> {
		private RequestControl control;
		private String deviceID;
		private String from;
		private String to;

		public Get8HoursAggDataListener(String deviceID, String from, String to) {
			this.deviceID = deviceID;
			this.from = from;
			this.to = to;
		}

		@Override
		public void onRequestCancelled() {
			if (control != null)
				control.cancel();

		}

		@Override
		public void onRequestResult(final Map<String, IOTMonitorData> result) {
			Site s = siteListViewAdatper.getSiteByDeviceID(deviceID);
			if (s != null) {
				if (result.containsKey(from)) {
					s.getMonitorData().setCo2(result.get(from).getCo2());
				}
			}
			handler.post(new Runnable() {

				@Override
				public void run() {
					siteListViewAdatper.notifyDataSetChanged();
				}
			});

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
}
