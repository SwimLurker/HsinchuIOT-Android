package org.slstudio.hsinchuiot.fragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.chart.TimeChart;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.BasicStroke;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import org.slstudio.hsinchuiot.Constants;
import org.slstudio.hsinchuiot.R;
import org.slstudio.hsinchuiot.UserMainActivity;
import org.slstudio.hsinchuiot.model.IOTMonitorData;
import org.slstudio.hsinchuiot.model.IOTMonitorThreshold;
import org.slstudio.hsinchuiot.model.IOTSampleData;
import org.slstudio.hsinchuiot.model.Site;
import org.slstudio.hsinchuiot.service.ServiceContainer;
import org.slstudio.hsinchuiot.util.IOTLog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.Paint.Align;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
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

	private XYMultipleSeriesDataset chartDataset = new XYMultipleSeriesDataset();
	private XYMultipleSeriesRenderer chartRenderer = new XYMultipleSeriesRenderer(3);
	private XYSeries co2Series;
	private XYSeriesRenderer co2Renderer;
	private XYSeries temperatureSeries;
	private XYSeriesRenderer temperatureRenderer;
	private XYSeries humiditySeries;
	private XYSeriesRenderer humidityRenderer;
	private XYSeries co2WarningSeries;
	private XYSeriesRenderer co2WarningRenderer;

	private GraphicalView chartView;
	private LinearLayout chartLayout;
	private TextView tvChartTitle;
	private Button btnChartSettings;

	private List<IOTSampleData> chartData = new ArrayList<IOTSampleData>();
	
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
		initViews(view);
		
		createChart(view);
		
		updateUI();
		
		UserMainActivity parentActivity = (UserMainActivity) this.getActivity();
		parentActivity.resendMessage();

		return view;
	}

	
	@Override
	public void onDestroyView() {
		destroyChart();
		super.onDestroyView();
	}

	@Override
	public void onResume() {
		super.onResume();
		if (chartView != null) {
			chartView.repaint();
		}
		
	}

	@SuppressLint("NewApi")
	public void updateUI() {
		if (site != null && getActivity() != null && !getActivity().isDestroyed()) {
			IOTMonitorThreshold warningThreshold = (IOTMonitorThreshold) ServiceContainer.getInstance()
					.getSessionService().getSessionValue(Constants.SessionKey.THRESHOLD_WARNING);
			IOTMonitorThreshold breachThreshold = (IOTMonitorThreshold) ServiceContainer.getInstance()
					.getSessionService().getSessionValue(Constants.SessionKey.THRESHOLD_BREACH);

			tvTitle.setText(site.getSiteName());

			int alarm = getActivity().getResources().getColor(R.color.status_alarm);
			int warning = getActivity().getResources().getColor(R.color.status_warning);
			int normal = getActivity().getResources().getColor(R.color.status_ok);

			IOTMonitorData data = site.getMonitorData();

			if (breachThreshold != null && data.isCO2Breach(breachThreshold)) {
				tvCO2Value.setTextColor(alarm);
				ivCO2Status.setImageResource(R.drawable.status_co2_alarm);
			} else if (warningThreshold != null && data.isCO2Breach(warningThreshold)) {
				tvCO2Value.setTextColor(warning);
				ivCO2Status.setImageResource(R.drawable.status_co2_warning);
			} else {
				tvCO2Value.setTextColor(normal);
				ivCO2Status.setImageResource(R.drawable.status_co2_ok);
			}
			tvCO2Value.setText(Float.toString(data.getCo2()));

			if (breachThreshold != null && data.isTemperatureBreach(breachThreshold)) {
				tvTemperatureValue.setTextColor(alarm);
			} else if (warningThreshold != null && data.isTemperatureBreach(warningThreshold)) {
				tvTemperatureValue.setTextColor(warning);
			} else {
				tvTemperatureValue.setTextColor(normal);
			}
			tvTemperatureValue.setText(Float.toString(site.getMonitorData().getTemperature()));

			if (breachThreshold != null && data.isHumidityBreach(breachThreshold)) {
				tvHumidityValue.setTextColor(alarm);
			} else if (warningThreshold != null && data.isHumidityBreach(warningThreshold)) {
				tvHumidityValue.setTextColor(warning);
			} else {
				tvHumidityValue.setTextColor(normal);
			}
			tvHumidityValue.setText(Float.toString(site.getMonitorData().getHumidity()));
			
			if(chartView != null){
				IOTLog.d("UserSiteHomePageFragment", "charview refresh");
				chartView.repaint();
			}else{
				IOTLog.e("UserSiteHomePageFragment", "charview is null");
			}
			
		}
	}
	
	public void updateChartData(List<IOTSampleData> samples){
		chartData = samples;
		updateChartData();
		chartView.repaint();
	}
	
	private void updateChartData(){

		Set<Long> timeSet = new HashSet<Long>();
		
		for (IOTSampleData sample: chartData) {
			if(sample.getType() == IOTSampleData.IOTSampleDataType.CO2){
				co2Series.add(sample.getTime().getTime(), sample.getValue());
				timeSet.add(sample.getTime().getTime());
			}else if(sample.getType() == IOTSampleData.IOTSampleDataType.TEMPERATURE){
				temperatureSeries.add(sample.getTime().getTime(), sample.getValue());
				timeSet.add(sample.getTime().getTime());
			}else if(sample.getType() == IOTSampleData.IOTSampleDataType.HUMIDITY){
				humiditySeries.add(sample.getTime().getTime(), sample.getValue());
				timeSet.add(sample.getTime().getTime());
			}
			
		}
		
		for(long time: timeSet){
			co2WarningSeries.add(time , 800);
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

		chartLayout = (LinearLayout) parentView.findViewById(R.id.id_chart);
		tvChartTitle = (TextView)parentView.findViewById(R.id.tv_chart_title);
		btnChartSettings = (Button)parentView.findViewById(R.id.btn_chart_settings);
		
	}

	private void createChart(View parentView) {
		chartDataset.clear();
		chartRenderer.removeAllRenderers();

		Resources resources = getActivity().getResources();
		
		chartRenderer.setApplyBackgroundColor(true);// 设置是否显示背景色
		chartRenderer.setBackgroundColor(resources.getColor(R.color.white));// 设置背景色
		chartRenderer.setMargins(new int[] { 20, 20, 5, 20 });// 设置图表的外边框(上/左/下/右)
		chartRenderer.setMarginsColor(resources.getColor(R.color.white));
		
		chartRenderer.setChartTitleTextSize(0);// ?设置整个图表标题文字大小
		//chartRenderer.setChartTitle("2015/07/20 15:00:00 - 2015/07/20 16:00:00");
		
		chartRenderer.setAxesColor(resources.getColor(R.color.dark_gray));
		chartRenderer.setAxisTitleTextSize(16); // 设置轴标题文字的大小
		chartRenderer.setLabelsColor(resources.getColor(R.color.red));
		
		chartRenderer.setLabelsTextSize(15);// 设置刻度显示文字的大小(XY轴都会被设置)
		chartRenderer.setLegendTextSize(15);// 图例文字大小
		
		chartRenderer.setXLabelsColor(resources.getColor(R.color.dark_gray));
		chartRenderer.setXTitle("时间");
		
		chartRenderer.setYLabelsColor(0, resources.getColor(R.color.title_bk_green));
		chartRenderer.setYTitle("ppm", 0);
		chartRenderer.setYAxisAlign(Align.LEFT, 0);
		chartRenderer.setYLabelsAlign(Align.LEFT, 0);

		chartRenderer.setYLabelsColor(1, resources.getColor(R.color.title_bk_brown));
		chartRenderer.setYTitle("℃", 1);
		chartRenderer.setYAxisAlign(Align.CENTER, 1);
		chartRenderer.setYLabelsAlign(Align.CENTER, 1);

		chartRenderer.setYLabelsColor(2, resources.getColor(R.color.title_bk_purple));
		chartRenderer.setYTitle("%", 2);
		chartRenderer.setYAxisAlign(Align.RIGHT, 2);
		chartRenderer.setYLabelsAlign(Align.RIGHT, 2);
		
		
		
		chartRenderer.setZoomButtonsVisible(true);// 是否显示放大缩小按钮
		chartRenderer.setPointSize(3);// 设置点的大小(图上显示的点的大小和图例中点的大小都会被设置)
		chartRenderer.setPanEnabled(true);
		//chartRenderer.setClickEnabled(true);
	
		co2WarningSeries = new XYSeries("",0);// 定义XYSeries
		chartDataset.addSeries(co2WarningSeries);// 在XYMultipleSeriesDataset中添加XYSeries
		co2WarningRenderer = new XYSeriesRenderer();// 定义XYSeriesRenderer
		
		chartRenderer.addSeriesRenderer(co2WarningRenderer);// 将单个XYSeriesRenderer增加到XYMultipleSeriesRenderer
		co2WarningRenderer.setPointStyle(PointStyle.POINT);// 点的类型是圆形
		co2WarningRenderer.setFillPoints(true);// 设置点是否实心
		co2WarningRenderer.setColor(resources.getColor(R.color.title_bk_green));
		co2WarningRenderer.setLineWidth(1);
		co2WarningRenderer.setStroke(BasicStroke.DOTTED);
		co2WarningRenderer.setShowLegendItem(false);
		
		co2Series = new XYSeries(resources.getString(R.string.co2),0);// 定义XYSeries
		chartDataset.addSeries(co2Series);// 在XYMultipleSeriesDataset中添加XYSeries
		co2Renderer = new XYSeriesRenderer();// 定义XYSeriesRenderer
		
		chartRenderer.addSeriesRenderer(co2Renderer);// 将单个XYSeriesRenderer增加到XYMultipleSeriesRenderer
		co2Renderer.setPointStyle(PointStyle.CIRCLE);// 点的类型是圆形
		co2Renderer.setFillPoints(true);// 设置点是否实心
		co2Renderer.setColor(resources.getColor(R.color.title_bk_green));
		co2Renderer.setLineWidth(3);

		temperatureSeries = new XYSeries(resources.getString(R.string.temperature), 1);// 定义XYSeries
		chartDataset.addSeries(temperatureSeries);// 在XYMultipleSeriesDataset中添加XYSeries
		temperatureRenderer = new XYSeriesRenderer();// 定义XYSeriesRenderer
		chartRenderer.addSeriesRenderer(temperatureRenderer);// 将单个XYSeriesRenderer增加到XYMultipleSeriesRenderer
		temperatureRenderer.setPointStyle(PointStyle.CIRCLE);// 点的类型是圆形
		temperatureRenderer.setFillPoints(true);// 设置点是否实心
		temperatureRenderer.setColor(resources.getColor(R.color.title_bk_brown));
		temperatureRenderer.setLineWidth(3);

		humiditySeries = new XYSeries(resources.getString(R.string.humidity), 2);// 定义XYSeries
		chartDataset.addSeries(humiditySeries);// 在XYMultipleSeriesDataset中添加XYSeries
		humidityRenderer = new XYSeriesRenderer();// 定义XYSeriesRenderer
		chartRenderer.addSeriesRenderer(humidityRenderer);// 将单个XYSeriesRenderer增加到XYMultipleSeriesRenderer
		humidityRenderer.setPointStyle(PointStyle.CIRCLE);// 点的类型是圆形
		humidityRenderer.setPointStrokeWidth(2);
		humidityRenderer.setFillPoints(true);// 设置点是否实心
		humidityRenderer.setColor(resources.getColor(R.color.title_bk_purple));
		humidityRenderer.setLineWidth(3);
		
		/*
		Calendar now = Calendar.getInstance();
		Calendar lastHour = Calendar.getInstance();
		lastHour.add(Calendar.HOUR, -1);

		Random r = new Random();

		for (int i = 0; i < 60; i++) {
			Calendar c = Calendar.getInstance();
			c.add(Calendar.HOUR, -1);
			c.add(Calendar.MINUTE, i);
			co2WarningSeries.add(c.getTime().getTime() , 800);
			co2Series.add(c.getTime().getTime() , Math.abs(r.nextInt() % 2000));
			temperatureSeries.add(c.getTime().getTime(), Math.abs(r.nextInt(4000)) / 100.0f);
			humiditySeries.add(c.getTime().getTime(), Math.abs(r.nextInt(10000)) / 100.0f);
		}
		*/
		
		updateChartData();
		chartView = ChartFactory.getTimeChartView(getActivity(), chartDataset, chartRenderer, "yyyy-MM-dd HH:mm:ss");
		chartRenderer.setClickEnabled(true);// 设置图表是否允许点击
		chartRenderer.setSelectableBuffer(100);// 设置点的缓冲半径值(在某点附件点击时,多大范围内都算点击这个点)
		
		chartLayout.removeAllViews();
		chartLayout.addView(chartView, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
	}


	private void destroyChart(){
		chartDataset.clear();
		chartRenderer.removeAllRenderers();
	}
}
