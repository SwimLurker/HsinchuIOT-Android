package org.slstudio.hsinchuiot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.slstudio.hsinchuiot.fragment.UserSiteHomePageFragment;
import org.slstudio.hsinchuiot.model.Device;
import org.slstudio.hsinchuiot.model.IOTMonitorData;
import org.slstudio.hsinchuiot.model.Site;
import org.slstudio.hsinchuiot.service.ServiceContainer;
import org.slstudio.hsinchuiot.service.http.ForgroundRequestListener;
import org.slstudio.hsinchuiot.service.http.HttpConfig;
import org.slstudio.hsinchuiot.service.http.HttpRequest;
import org.slstudio.hsinchuiot.service.http.NoneAuthedHttpRequest;
import org.slstudio.hsinchuiot.service.http.RequestControl;
import org.slstudio.hsinchuiot.util.ReportUtil;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.widget.ImageButton;

public class UserMainActivity extends BaseActivity {

	private ActionBar actionBar;

	private ViewPager viewPager;
	private FragmentPagerAdapter pagerAdapter;
	private List<UserSiteHomePageFragment> fragments = new ArrayList<UserSiteHomePageFragment>();

	private List<Device> deviceList = new ArrayList<Device>();

	private Handler handler;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_user_main);
		initViews();
		handler = new Handler();
		handler.post(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				getDeviceList();
			}

		});
	}
	
	@Override
	protected void setupActionBar() {
		actionBar = getActionBar();
		actionBar.setTitle("");
		actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);
		super.setupActionBar();
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

			private int currentIndex;

			@Override
			public void onPageSelected(int position) {
				UserSiteHomePageFragment fragment = fragments.get(position);
				Site site = fragment.getSite();
				actionBar.setTitle(site.getSiteName());
				
				currentIndex = position;
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});
		
		setupActionBar();
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
	
	private void createHomeFragments(List<Site> sites) {
		fragments.clear();
		
		for(Site site: sites){
			UserSiteHomePageFragment fragment = new UserSiteHomePageFragment();
			fragment.setSite(site);
			fragments.add(fragment);
		}
		pagerAdapter.notifyDataSetChanged();
		if(fragments.size() >0){
			viewPager.setCurrentItem(0);
		}
		
	}

	private class GetDeviceListListener extends
			ForgroundRequestListener<List<Device>> {

		public GetDeviceListListener(Context context,
				boolean isShowProgressDialog, String content) {
			super(context, isShowProgressDialog, content);
		}

		@Override
		public void onRequestComplete() {
			super.onRequestComplete();
		}

		@Override
		public void onRequestResult(final List<Device> result) {
			deviceList = result;
			final List<Site> sites = new ArrayList<Site>();
			for (Device d : deviceList) {
				Site site = new Site();
				site.setSiteID(d.getDeviceID());
				site.setDevice(d);
				site.setSiteName(d.getSiteName());
				site.setSiteImageFilename("site_" + d.getDeviceSN() + ".png");
				site.setMonitorData(new IOTMonitorData(0, 0, 0));
				sites.add(site);
			}
			
			handler.post(new Runnable() {

				@Override
				public void run() {
					createHomeFragments(sites);
				}

			});

		}
	}
}
