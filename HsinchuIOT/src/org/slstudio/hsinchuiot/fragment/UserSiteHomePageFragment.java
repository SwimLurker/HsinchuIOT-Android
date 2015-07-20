package org.slstudio.hsinchuiot.fragment;

import org.slstudio.hsinchuiot.Constants;
import org.slstudio.hsinchuiot.R;
import org.slstudio.hsinchuiot.UserMainActivity;
import org.slstudio.hsinchuiot.model.IOTMonitorData;
import org.slstudio.hsinchuiot.model.IOTMonitorThreshold;
import org.slstudio.hsinchuiot.model.Site;
import org.slstudio.hsinchuiot.service.ServiceContainer;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class UserSiteHomePageFragment extends Fragment {

	private Site site;
	private int index;

	private ImageButton btnPreSite;
	private ImageButton btnNextSite;
	private TextView tvTitle;

	private TextView tvCO2Value;
	private TextView tvTemperatureValue;
	private TextView tvHumidityValue;
	private ImageView ivCO2Status;

	private Handler handler;

	public UserSiteHomePageFragment() {
		super();
	}

	public Site getSite() {
		return site;
	}

	public void setSite(Site site) {
		this.site = site;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_user_site_homepage, container, false);
		handler = new Handler();
		initViews(view);
		updateUI();
		UserMainActivity parentActivity = (UserMainActivity) this.getActivity();
		parentActivity.resendMessage();
		
		return view;
	}

	public void updateUI() {
		if (site != null) {
			IOTMonitorThreshold warningThreshold = (IOTMonitorThreshold)ServiceContainer.getInstance().getSessionService().getSessionValue(Constants.SessionKey.THRESHOLD_WARNING);
			IOTMonitorThreshold breachThreshold = (IOTMonitorThreshold)ServiceContainer.getInstance().getSessionService().getSessionValue(Constants.SessionKey.THRESHOLD_BREACH);
			
			tvTitle.setText(site.getSiteName());
			
			
			int alarm = getActivity().getResources().getColor(R.color.status_alarm);
			int warning = getActivity().getResources().getColor(R.color.status_warning);
			int normal = getActivity().getResources().getColor(R.color.status_ok);
			
			IOTMonitorData data = site.getMonitorData();
			
			if (breachThreshold != null && data.isCO2Breach(breachThreshold)) {
				tvCO2Value.setTextColor(alarm);
				ivCO2Status.setImageResource(R.drawable.status_co2_alarm);
			}else if (warningThreshold != null && data.isCO2Breach(warningThreshold)) {
				tvCO2Value.setTextColor(warning);
				ivCO2Status.setImageResource(R.drawable.status_co2_warning);
			}else {
				tvCO2Value.setTextColor(normal);
				ivCO2Status.setImageResource(R.drawable.status_co2_ok);
			}
			tvCO2Value.setText(Float.toString(data.getCo2()));
			
			if (breachThreshold != null && data.isTemperatureBreach(breachThreshold)) {
				tvTemperatureValue.setTextColor(alarm);
			}else if (warningThreshold != null && data.isTemperatureBreach(warningThreshold)) {
				tvTemperatureValue.setTextColor(warning);
			}else {
				tvTemperatureValue.setTextColor(normal);
			}
			tvTemperatureValue.setText(Float.toString(site.getMonitorData().getTemperature()));
			
			if (breachThreshold != null && data.isHumidityBreach(breachThreshold)) {
				tvHumidityValue.setTextColor(alarm);
			}else if (warningThreshold != null && data.isHumidityBreach(warningThreshold)) {
				tvHumidityValue.setTextColor(warning);
			}else {
				tvHumidityValue.setTextColor(normal);
			}
			tvHumidityValue.setText(Float.toString(site.getMonitorData().getHumidity()));
			
			
			Toast.makeText(getActivity(), "update value for site:" + site.getSiteName(), Toast.LENGTH_SHORT).show();
		}
	}

	private void initViews(View parentView) {
		final UserMainActivity parentActivity = (UserMainActivity) this.getActivity();

		btnPreSite = (ImageButton) parentView.findViewById(R.id.ib_pre_site);
		if (index == 0) {
			btnPreSite.setVisibility(View.INVISIBLE);
		} else {
			btnPreSite.setVisibility(View.VISIBLE);
		}
		btnPreSite.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				parentActivity.moveToPreSite();
			}

		});

		btnNextSite = (ImageButton) parentView.findViewById(R.id.ib_next_site);
		if (index == parentActivity.getSiteList().size() - 1) {
			btnNextSite.setVisibility(View.INVISIBLE);
		} else {
			btnNextSite.setVisibility(View.VISIBLE);
		}
		btnNextSite.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				parentActivity.moveToNextSite();
			}

		});

		tvTitle = (TextView) parentView.findViewById(R.id.tv_userhome_title);

		Typeface typeFace = Typeface.createFromAsset(getActivity().getAssets(), "fonts/DS-DIGIB.TTF");

		tvCO2Value = (TextView) parentView.findViewById(R.id.tv_userhome_co2value);
		tvCO2Value.setTypeface(typeFace);

		tvTemperatureValue = (TextView) parentView.findViewById(R.id.tv_userhome_temperaturevalue);
		tvTemperatureValue.setTypeface(typeFace);

		tvHumidityValue = (TextView) parentView.findViewById(R.id.tv_userhome_humidityvalue);
		tvHumidityValue.setTypeface(typeFace);

		ivCO2Status = (ImageView) parentView.findViewById(R.id.iv_userhome_co2_status);

	}
}
