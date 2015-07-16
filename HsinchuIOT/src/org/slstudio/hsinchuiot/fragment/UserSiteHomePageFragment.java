package org.slstudio.hsinchuiot.fragment;

import org.slstudio.hsinchuiot.R;
import org.slstudio.hsinchuiot.model.Site;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class UserSiteHomePageFragment extends Fragment {
	
	private Site site;
	
	private TextView tvCO2Value;
	private TextView tvTemperatureValue;
	private TextView tvHumidityValue;

	
	public UserSiteHomePageFragment(){
		super();
	}

	public Site getSite() {
		return site;
	}


	public void setSite(Site site) {
		this.site = site;
	}


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_user_site_homepage, container,
				false);

		Typeface typeFace = Typeface.createFromAsset(getActivity().getAssets(),
				"fonts/Clubland.ttf");

		tvCO2Value = (TextView) view.findViewById(R.id.site_tv_co2value);
		tvCO2Value.setTypeface(typeFace);

		tvTemperatureValue = (TextView) view
				.findViewById(R.id.site_tv_temperaturevalue);
		tvTemperatureValue.setTypeface(typeFace);

		tvHumidityValue = (TextView) view
				.findViewById(R.id.site_tv_humidityvalue);
		tvHumidityValue.setTypeface(typeFace);

		return view;
	}
}
