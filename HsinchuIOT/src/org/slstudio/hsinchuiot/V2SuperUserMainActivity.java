package org.slstudio.hsinchuiot;

import java.util.ArrayList;
import java.util.List;

import org.slstudio.hsinchuiot.fragment.V2AverageValueTab;
import org.slstudio.hsinchuiot.fragment.V2RTDataTab;
import org.slstudio.hsinchuiot.ui.MenuView;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.AdapterView.OnItemClickListener;

public class V2SuperUserMainActivity extends BaseActivity {

	private RadioGroup radioGroup;

	private ViewPager viewPager;
	private FragmentPagerAdapter pagerAdapter;
	private List<Fragment> fragments = new ArrayList<Fragment>();


	private MenuView menuListView = null;
	
	OnItemClickListener listClickListener = new OnItemClickListener(){

		@Override
		public void onItemClick(AdapterView<?> arg0, View view, int arg2,
				long arg3) {
			int key = Integer.parseInt(view.getTag().toString());
			switch(key){
				case MenuView.MENU_SORT_BY_STATUS:
					break;
				case MenuView.MENU_SORT_BY_CO2:
					break;
				case MenuView.MENU_SORT_BY_TEMPERATURE:
					break;
				case MenuView.MENU_SORT_BY_HUMIDITY:
					break;
				case MenuView.MENU_SORT_BY_LOCATION:
					break;
				default:
					break;
			}
			menuListView.close();
		}
    	
    };
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.v2_activity_superuser_main);

		
		Button sortBtn = (Button) findViewById(R.id.btn_sort);
		sortBtn.setOnClickListener(new OnClickListener(){

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
					radioGroup.check(R.id.tab_averagevalue);
					break;
				case 1:
					radioGroup.check(R.id.tab_realtimevalue);
					break;
				}

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
	
	
	protected void switchSysMenuShow(){
    	
    	initSysMenu();
    	if(!menuListView.getIsShow()){
    		menuListView.show();
    	}else{
    		menuListView.close();
    	}
    }
    
    private void initSysMenu(){
    	if(menuListView == null){
    		menuListView = new MenuView(this);
    	}
    	LinearLayout layout = (LinearLayout)findViewById(R.id.superuser_title_layout);
        int height = layout.getHeight();
        menuListView.setTopMargin(height-10);
    	menuListView.listView.setOnItemClickListener(listClickListener);
    	menuListView.clear();
    	menuListView.add(MenuView.MENU_SORT_BY_STATUS, getString(R.string.menuitem_sortbystatus));
    	menuListView.add(MenuView.MENU_SORT_BY_CO2, getString(R.string.menuitem_sortbyco2));
    	menuListView.add(MenuView.MENU_SORT_BY_TEMPERATURE, getString(R.string.menuitem_sortbytemperature));
    	menuListView.add(MenuView.MENU_SORT_BY_HUMIDITY, getString(R.string.menuitem_sortbyhumidity));
    	menuListView.add(MenuView.MENU_SORT_BY_LOCATION, getString(R.string.menuitem_sortbylocation));
    	
    } 

}
