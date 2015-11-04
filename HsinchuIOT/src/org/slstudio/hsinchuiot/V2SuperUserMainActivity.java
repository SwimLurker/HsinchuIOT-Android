package org.slstudio.hsinchuiot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slstudio.hsinchuiot.fragment.V2AverageValueTab;
import org.slstudio.hsinchuiot.fragment.V2ListViewTab;
import org.slstudio.hsinchuiot.fragment.V2RTDataTab;
import org.slstudio.hsinchuiot.service.ServiceContainer;
import org.slstudio.hsinchuiot.ui.MenuView;
import org.slstudio.hsinchuiot.ui.adapter.V2SiteListViewAdapter;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

public class V2SuperUserMainActivity extends BaseActivity {

	private RadioGroup radioGroup;

	private RadioButton averageValueRadioBtn;
	private RadioButton realtimeValueRadioBtn;

	private ViewPager viewPager;
	private FragmentPagerAdapter pagerAdapter;
	private List<V2ListViewTab> fragments = new ArrayList<V2ListViewTab>();

	private ImageButton logoffBtn;
	private Button sortBtn;
	private TextView titleTV;

	private MenuView menuListView = null;

	private OnItemClickListener listClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View view, int arg2,
				long arg3) {
			int currentFragment = viewPager.getCurrentItem();
			V2ListViewTab tab = fragments.get(currentFragment);

			int sortBy = V2SiteListViewAdapter.SORT_BY_STATUS;

			Resources resources = getResources();

			int key = Integer.parseInt(view.getTag().toString());
			switch (key) {
			case MenuView.MENU_SORT_BY_STATUS:
				sortBy = V2SiteListViewAdapter.SORT_BY_STATUS;
				sortBtn.setText(resources
						.getString(R.string.menuitem_sortbystatus));
				break;
			case MenuView.MENU_SORT_BY_CO2:
				sortBy = V2SiteListViewAdapter.SORT_BY_CO2;
				sortBtn.setText(resources
						.getString(R.string.menuitem_sortbyco2));
				break;
			case MenuView.MENU_SORT_BY_TEMPERATURE:
				sortBy = V2SiteListViewAdapter.SORT_BY_TEMPERATURE;
				sortBtn.setText(resources
						.getString(R.string.menuitem_sortbytemperature));
				break;
			case MenuView.MENU_SORT_BY_HUMIDITY:
				sortBy = V2SiteListViewAdapter.SORT_BY_HUMIDITY;
				sortBtn.setText(resources
						.getString(R.string.menuitem_sortbyhumidity));
				break;
			case MenuView.MENU_SORT_BY_LOCATION:
				sortBy = V2SiteListViewAdapter.SORT_BY_LOCATION;
				sortBtn.setText(resources
						.getString(R.string.menuitem_sortbylocation));
				break;
			default:
				break;
			}

			tab.sortListView(sortBy);

			menuListView.close();
		}

	};

	private Handler handler = new Handler() {

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case Constants.MessageKey.V2_MESSAGE_UPDATE_TIME:
				titleTV.setText(sdf.format(new Date()));

				handler.sendEmptyMessageDelayed(
						Constants.MessageKey.V2_MESSAGE_UPDATE_TIME, 1000);
				break;
			}
			super.handleMessage(msg);
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.v2_activity_superuser_main);

		initViews();

		handler.sendEmptyMessage(Constants.MessageKey.V2_MESSAGE_UPDATE_TIME);
		
		ServiceContainer.getInstance().getUpgradeController()
				.checkVersion(null, true);
	}

	@Override
	protected void onResume() {
		handler.sendEmptyMessage(Constants.MessageKey.V2_MESSAGE_UPDATE_TIME);
		super.onResume();
	}

	@Override
	protected void onPause() {
		handler.removeMessages(Constants.MessageKey.V2_MESSAGE_UPDATE_TIME);
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		handler.removeMessages(Constants.MessageKey.V2_MESSAGE_UPDATE_TIME);
		super.onDestroy();
	}

	
	
	@Override
	public void onAttachFragment(Fragment fragment) {
		// TODO Auto-generated method stub
		super.onAttachFragment(fragment);
		if(fragment instanceof V2AverageValueTab){
			V2AverageValueTab tab = (V2AverageValueTab)fragment;
			tab.updateListView();
		}
	}

	protected void switchSysMenuShow() {

		initSysMenu();
		if (!menuListView.getIsShow()) {
			menuListView.show();
		} else {
			menuListView.close();
		}
	}

	private void initSysMenu() {
		if (menuListView == null) {
			menuListView = new MenuView(this);
		}

		Resources resources = getResources();
		Configuration config = resources.getConfiguration();

		LinearLayout layout = (LinearLayout) findViewById(R.id.superuser_title_layout);
		int height = layout.getHeight();
		menuListView.setTopMargin(height - 10);
		menuListView.listView.setOnItemClickListener(listClickListener);
		menuListView.clear();
		menuListView.add(MenuView.MENU_SORT_BY_STATUS,
				getString(R.string.menuitem_sortbystatus));
		menuListView.add(MenuView.MENU_SORT_BY_CO2,
				getString(R.string.menuitem_sortbyco2));
		menuListView.add(MenuView.MENU_SORT_BY_TEMPERATURE,
				getString(R.string.menuitem_sortbytemperature));
		menuListView.add(MenuView.MENU_SORT_BY_HUMIDITY,
				getString(R.string.menuitem_sortbyhumidity));
		menuListView.add(MenuView.MENU_SORT_BY_LOCATION,
				getString(R.string.menuitem_sortbylocation));

	}

	private void initViews() {

		logoffBtn = (ImageButton) findViewById(R.id.btn_logoff);
		logoffBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				logoff();
			}

		});
		
		titleTV = (TextView)findViewById(R.id.superuser_title);
		
		sortBtn = (Button) findViewById(R.id.btn_sort);
		sortBtn.setText(getResources().getString(
				R.string.menuitem_sortbylocation));
		sortBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View parent) {
				// TODO Auto-generated method stub
				switchSysMenuShow();
			}
		});

		viewPager = (ViewPager) findViewById(R.id.id_superuser_main_viewpager);

		V2AverageValueTab tab01 = new V2AverageValueTab();
		V2RTDataTab tab02 = new V2RTDataTab();

		fragments.add(tab01);
		fragments.add(tab02);

		radioGroup = (RadioGroup) findViewById(R.id.main_tab);
		radioGroup
				.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(RadioGroup group, int checkedId) {
						if (checkedId == R.id.tab_averagevalue) {
							viewPager.setCurrentItem(0);
						} else if (checkedId == R.id.tab_realtimevalue) {
							viewPager.setCurrentItem(1);
						}
					}

				});

		averageValueRadioBtn = (RadioButton) findViewById(R.id.tab_averagevalue);

		realtimeValueRadioBtn = (RadioButton) findViewById(R.id.tab_realtimevalue);

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
				switch (position) {

				case 0:
					averageValueRadioBtn.setChecked(true);
					break;
				case 1:
					realtimeValueRadioBtn.setChecked(true);
					break;
				}
				V2ListViewTab tab = fragments.get(position);
				tab.updateListView();

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

	private void logoff() {
		ServiceContainer.getInstance().getSessionService().setLoginUser(null);
		ServiceContainer.getInstance().getSessionService().setSessionID(null);
		ServiceContainer.getInstance().getSessionService()
				.setSessionValue(Constants.SessionKey.THRESHOLD_BREACH, null);
		ServiceContainer.getInstance().getSessionService()
				.setSessionValue(Constants.SessionKey.THRESHOLD_WARNING, null);

		Intent loginIntent = new Intent(Constants.Action.HSINCHUIOT_LOGIN);
		startActivity(loginIntent);
		finish();
	}

}
