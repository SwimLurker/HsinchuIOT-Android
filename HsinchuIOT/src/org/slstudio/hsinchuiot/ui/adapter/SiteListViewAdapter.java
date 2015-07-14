package org.slstudio.hsinchuiot.ui.adapter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import org.slstudio.hsinchuiot.Constants;
import org.slstudio.hsinchuiot.R;
import org.slstudio.hsinchuiot.SuperUserSiteDetailActivity;
import org.slstudio.hsinchuiot.R.color;
import org.slstudio.hsinchuiot.model.IOTMonitorData;
import org.slstudio.hsinchuiot.model.IOTMonitorThreshold;
import org.slstudio.hsinchuiot.model.Site;
import org.slstudio.hsinchuiot.util.ImageUtil;

import com.nostra13.universalimageloader.core.ImageLoader;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.AnimationUtils;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewFlipper;

public class SiteListViewAdapter extends BaseAdapter {

	
	private List<Site> items = null;
	private IOTMonitorThreshold warningThreshold = null;
	private IOTMonitorThreshold breachThreshold = null;

	private LayoutInflater mInflater = null;
	private Context context = null;
	
	private int selectedPosition = -1;
	private int clickedPosition = -1;
	
	public SiteListViewAdapter(Context c, List<Site> items,
			IOTMonitorThreshold warningThreshold,
			IOTMonitorThreshold breachThreshold) {
		this.context = c;
		this.items = items;
		this.warningThreshold = warningThreshold;
		this.breachThreshold = breachThreshold;

	}

	public void setSelectedPosition(int position) {  
        selectedPosition = position;  
    } 
	
	public List<Site> getItems() {
		return items;
	}

	public void setItems(List<Site> items) {
		this.items = items;
	}

	public IOTMonitorThreshold getWarningThreshold() {
		return warningThreshold;
	}

	public void setWarningThreshold(IOTMonitorThreshold warningThreshold) {
		this.warningThreshold = warningThreshold;
	}

	public IOTMonitorThreshold getBreachThreshold() {
		return breachThreshold;
	}

	public void setBreachThreshold(IOTMonitorThreshold breachThreshold) {
		this.breachThreshold = breachThreshold;
	}

	public Site getSiteByDeviceID(String deviceID) {
		for (Site site : items) {
			if (site.getDevice() != null
					&& site.getDevice().getDeviceID().equals(deviceID)) {
				return site;
			}
		}
		return null;
	}

	@Override
	public int getCount() {
		return items == null ? 0 : items.size();
	}

	@Override
	public Object getItem(int position) {
		return items == null ? null : items.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final int pos = position;
		if (mInflater == null) {
			mInflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}
		Site site = items.get(position);
		IOTMonitorData data = site.getMonitorData();
		final Resources resources = context.getResources();

		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.listitem_site, parent,
					false);
		}

		// site image
		ImageView ivSiteImage = (ImageView) convertView
				.findViewById(R.id.li_iv_site);

		// String uri =
		// "file://"+Constants.ImageLoader.IMAGE_ENGINE_CACHE+"/thumbnail/" +
		// site.getSiteImageFilename();

		// ImageLoader.getInstance().displayImage(uri, ivSiteImage);

		FileInputStream fis = null;

		try {
			String siteImageFilename = Constants.ImageLoader.IMAGE_ENGINE_CACHE
					+ "/thumbnail/" + site.getSiteImageFilename();
			// if (siteImageFilename == null) {
			// ivSiteImage.setImageResource(R.drawable.site_unknown);
			// }
			File f = new File(siteImageFilename);

			if (f.exists() && f.isFile()) {
				fis = new FileInputStream(f);
				Bitmap bitmap = BitmapFactory.decodeStream(fis);
				ivSiteImage.setImageBitmap(bitmap);
			} else {
				ivSiteImage.setImageResource(R.drawable.site_unknown_thumbnail);
			}

		} catch (Exception exp) {
			ivSiteImage.setImageResource(R.drawable.site_unknown_thumbnail);
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
				}
			}
		}

		// site name
		TextView tvSiteName = (TextView) convertView
				.findViewById(R.id.li_tv_sitename);
		tvSiteName.setText(data == null ? null : site.getSiteName());

		int red = resources.getColor(R.color.red);
		int black = resources.getColor(R.color.black);
		// co2
		ImageView ivCO2 = (ImageView) convertView
				.findViewById(R.id.li_iv_icon_co2);
		TextView tvCO2 = (TextView) convertView
				.findViewById(R.id.li_tv_co2value);

		if (data.isCO2Breach(breachThreshold)) {
			tvCO2.setTextColor(red);
			ivCO2.setImageResource(R.drawable.co2_alarm);
		} else {
			tvCO2.setTextColor(black);
			ivCO2.setImageResource(R.drawable.co2);
		}
		tvCO2.setText(Float.toString(data.getCo2()) + " ppm");

		// temperature
		ImageView ivTemperature = (ImageView) convertView
				.findViewById(R.id.li_iv_icon_temperature);
		TextView tvTemperature = (TextView) convertView
				.findViewById(R.id.li_tv_temperaturevalue);
		tvTemperature.setText(Float.toString(data.getTemperature()) + " ℃");

		if (data.isTemperatureBreach(breachThreshold)) {
			tvTemperature.setTextColor(red);
			ivTemperature.setImageResource(R.drawable.temperature_alarm);
		} else {
			tvTemperature.setTextColor(black);
			ivTemperature.setImageResource(R.drawable.temperature);
		}

		// humidity
		ImageView ivHumidity = (ImageView) convertView
				.findViewById(R.id.li_iv_icon_humidity);
		TextView tvHumidity = (TextView) convertView
				.findViewById(R.id.li_tv_humidityvalue);
		
		tvHumidity.setText(Float.toString(data.getHumidity()) + " %");

		if (data.isHumidityBreach(breachThreshold)) {
			tvHumidity.setTextColor(red);
			ivHumidity.setImageResource(R.drawable.humidity_alarm);
		} else {
			tvHumidity.setTextColor(black);
			ivHumidity.setImageResource(R.drawable.humidity);
		}
		
		
		
		if(position == selectedPosition){
			convertView.setBackgroundColor(context.getResources().getColor(R.color.light_blue_hilight));
		}else{
			if(position == clickedPosition){
				convertView.setBackgroundColor(context.getResources().getColor(R.color.light_blue));
			}else{
				convertView.setBackgroundColor(context.getResources().getColor(R.color.white));
			}
		}
		
		convertView.setOnTouchListener(new OnTouchListener(){
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch(event.getAction()){
				case MotionEvent.ACTION_DOWN:
					clickedPosition = pos;
					notifyDataSetChanged();
					break;
				case MotionEvent.ACTION_UP:
					clickedPosition = -1;
					notifyDataSetChanged();
					break;

				case MotionEvent.ACTION_OUTSIDE:
					clickedPosition = -1;
					notifyDataSetChanged();
					break;
				}
				return false;
			}
			
		});
		
		return convertView;
	}

}
