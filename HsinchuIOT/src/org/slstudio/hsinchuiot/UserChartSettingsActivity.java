package org.slstudio.hsinchuiot;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout.LayoutParams;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class UserChartSettingsActivity extends BaseActivity {

	public static final int CHART_TYPE_REALTIME = 0;
	public static final int CHART_TYPE_AGGRAGATION = 1;

	public static final int GRANULARITY_HOUR = 0;
	public static final int GRANULARITY_HOURS = 1;
	public static final int GRANULARITY_DAY = 2;
	public static final int GRANULARITY_WEEK = 3;
	public static final int GRANULARITY_MONTH = 4;

	public static final int TIME_PERIOD_TODAY = 0;
	public static final int TIME_PERIOD_YESTERDAY = 1;
	public static final int TIME_PERIOD_THIS_WEEK = 2;
	public static final int TIME_PERIOD_LAST_WEEK = 3;
	public static final int TIME_PERIOD_THIS_MONTH = 4;
	public static final int TIME_PERIOD_LAST_MONTH = 5;
	public static final int TIME_PERIOD_LAST_3DAYS = 6;
	public static final int TIME_PERIOD_LAST_5DAYS = 7;
	public static final int TIME_PERIOD_LAST_7DAYS = 8;
	public static final int TIME_PERIOD_LAST_1WEEK = 9;
	public static final int TIME_PERIOD_LAST_2WEEKS = 10;
	public static final int TIME_PERIOD_LAST_3WEEKS = 11;
	public static final int TIME_PERIOD_LAST_1MONTH = 12;
	public static final int TIME_PERIOD_LAST_2MONTHS = 13;
	public static final int TIME_PERIOD_LAST_3MONTHS = 14;

	private View chartTypeLayout;
	private TextView tvChartType;

	private LinearLayout rtChartParameterLayout;
	private LinearLayout aggrChartParameterLayout;

	private View granularityTypeLayout;
	private TextView tvGranularityType;

	private View timePeriodLayout;

	private View timePeriodStartTimeLayout;
	private TextView tvTimePeriodStartTime;
	private View timePeriodEndTimeLayout;
	private TextView tvTimePeriodEndTime;

	private static final String[] CHART_TYPE = new String[] { "即時資料", "歷史統計" };
	private int currentChartType = CHART_TYPE_REALTIME;

	private static final String[] GRANULARITY_TYPE = new String[] { "1小時",
			"8小時", "日", "周", "月" };
	private int currentGranularityType = GRANULARITY_HOUR;

	private static final String[] TIME_PERIOD_QUICKLINK = new String[] { "今天",
			"昨天", "本周", "上周", "本月", "上月", "過去3天", "過去5天", "過去7天", "過去1周",
			"過去2周", "過去3周", "過去1月", "過去2月", "過去3月" };
	private int currentTimePeriodQuickLink = -1;

	private Date startTime;
	private Date endTime;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_chart_settings);
		initViews();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_user_chart_settings, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();

			break;
		case R.id.menu_ok:
			finish();
			break;

		}
		return true;
	}

	@Override
	protected void setupActionBar() {
		ActionBar actionBar = getActionBar();
		actionBar.setHomeButtonEnabled(true);
		actionBar.setIcon(R.drawable.icon_back);
		actionBar.setTitle(R.string.action_settings);
		super.setupActionBar();
	}

	private void initViews() {

		final Resources resources = getResources();

		tvChartType = (TextView) findViewById(R.id.tv_chartsettings_type);
		chartTypeLayout = findViewById(R.id.layout_chartsettings_type);
		rtChartParameterLayout = (LinearLayout) findViewById(R.id.layout_chartsettings_rtchart_parameter);
		aggrChartParameterLayout = (LinearLayout) findViewById(R.id.layout_chartsettings_aggrchart_parameter);
		tvGranularityType = (TextView) findViewById(R.id.tv_chartsettings_aggrchart_granularity);
		granularityTypeLayout = findViewById(R.id.layout_chartsettings_aggrchart_granularity);
		timePeriodLayout = findViewById(R.id.layout_chartsettings_aggrchart_timeperiod);
		tvTimePeriodStartTime = (TextView) findViewById(R.id.tv_chartsettings_aggrchart_starttime);
		tvTimePeriodEndTime = (TextView) findViewById(R.id.tv_chartsettings_aggrchart_endtime);
		timePeriodStartTimeLayout = findViewById(R.id.layout_chartsettings_aggrchart_starttime);
		timePeriodEndTimeLayout = findViewById(R.id.layout_chartsettings_aggrchart_endtime);

		tvChartType.setText(CHART_TYPE[currentChartType]);

		if (currentChartType == CHART_TYPE_AGGRAGATION) {
			rtChartParameterLayout.setVisibility(View.INVISIBLE);
			aggrChartParameterLayout.setVisibility(View.VISIBLE);
		} else {
			rtChartParameterLayout.setVisibility(View.VISIBLE);
			aggrChartParameterLayout.setVisibility(View.INVISIBLE);
		}

		chartTypeLayout.setOnClickListener(new View.OnClickListener() {
			private int tempSelect = 0;

			@Override
			public void onClick(View v) {

				Dialog dialog = new AlertDialog.Builder(
						UserChartSettingsActivity.this)
						.setTitle("請選擇圖表類型")
						.setIcon(android.R.drawable.ic_menu_more)
						.setPositiveButton(
								resources.getString(R.string.action_ok),
								new DialogInterface.OnClickListener() {

									public void onClick(DialogInterface dialog,
											int which) {
										currentChartType = tempSelect;
										tvChartType
												.setText(CHART_TYPE[currentChartType]);
										if (currentChartType == CHART_TYPE_AGGRAGATION) {
											rtChartParameterLayout
													.setVisibility(View.INVISIBLE);
											aggrChartParameterLayout
													.setVisibility(View.VISIBLE);
										} else {
											rtChartParameterLayout
													.setVisibility(View.VISIBLE);
											aggrChartParameterLayout
													.setVisibility(View.INVISIBLE);
										}
									}
								})
						.setSingleChoiceItems(CHART_TYPE, currentChartType,
								new DialogInterface.OnClickListener() {

									public void onClick(DialogInterface dialog,
											int which) {
										tempSelect = which;
									}
								})
						.setNegativeButton(
								resources.getString(R.string.action_cancel),
								new DialogInterface.OnClickListener() {

									public void onClick(DialogInterface dialog,
											int which) {

									}
								}).create();
				dialog.show();
			}
		});

		tvGranularityType.setText(GRANULARITY_TYPE[currentGranularityType]);

		granularityTypeLayout.setOnClickListener(new View.OnClickListener() {
			private int tempSelect = 0;

			@Override
			public void onClick(View v) {

				Dialog dialog = new AlertDialog.Builder(
						UserChartSettingsActivity.this)
						.setTitle("請選擇統計單位")
						.setIcon(android.R.drawable.ic_menu_more)
						.setPositiveButton(
								resources.getString(R.string.action_ok),
								new DialogInterface.OnClickListener() {

									public void onClick(DialogInterface dialog,
											int which) {
										currentGranularityType = tempSelect;
										tvGranularityType
												.setText(GRANULARITY_TYPE[currentGranularityType]);
									}
								})
						.setSingleChoiceItems(GRANULARITY_TYPE,
								currentGranularityType,
								new DialogInterface.OnClickListener() {

									public void onClick(DialogInterface dialog,
											int which) {
										tempSelect = which;
									}
								})
						.setNegativeButton(
								resources.getString(R.string.action_cancel),
								new DialogInterface.OnClickListener() {

									public void onClick(DialogInterface dialog,
											int which) {

									}
								}).create();
				dialog.show();
			}
		});

		timePeriodLayout.setOnClickListener(new View.OnClickListener() {
			private int tempSelect = 0;

			@Override
			public void onClick(View v) {

				Dialog dialog = new AlertDialog.Builder(
						UserChartSettingsActivity.this)
						.setTitle("請選擇統計週期")
						.setIcon(android.R.drawable.ic_menu_more)
						.setPositiveButton(
								resources.getString(R.string.action_ok),
								new DialogInterface.OnClickListener() {

									public void onClick(DialogInterface dialog,
											int which) {
										currentTimePeriodQuickLink = tempSelect;
										startTime = getStartTime(currentTimePeriodQuickLink);
										endTime = getEndTime(currentTimePeriodQuickLink);

										SimpleDateFormat sdf = new SimpleDateFormat(
												"yyyy-MM-dd HH:mm:ss");
										if (startTime != null) {
											tvTimePeriodStartTime.setText(sdf
													.format(startTime));
										}
										if (endTime != null) {
											tvTimePeriodEndTime.setText(sdf
													.format(endTime));
										}
									}
								})
						.setSingleChoiceItems(TIME_PERIOD_QUICKLINK,
								currentTimePeriodQuickLink,
								new DialogInterface.OnClickListener() {

									public void onClick(DialogInterface dialog,
											int which) {
										tempSelect = which;
									}
								})
						.setNegativeButton(
								resources.getString(R.string.action_cancel),
								new DialogInterface.OnClickListener() {

									public void onClick(DialogInterface dialog,
											int which) {

									}
								}).create();
				dialog.show();
			}
		});

		timePeriodStartTimeLayout
				.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {

						View view = View.inflate(getApplicationContext(),
								R.layout.date_time_picker, null);
						final DatePicker datePicker = (DatePicker) view
								.findViewById(R.id.date_picker);
						final TimePicker timePicker = (TimePicker) view
								.findViewById(R.id.time_picker);

						LinearLayout dpContainer = (LinearLayout) datePicker
								.getChildAt(0);
						LinearLayout dpSpinner = (LinearLayout) dpContainer
								.getChildAt(0);

						for (int i = 0; i < dpSpinner.getChildCount(); i++) {
							NumberPicker numPicker = (NumberPicker) dpSpinner
									.getChildAt(i);
							for(int j=0; j<numPicker.getChildCount(); j++){
								View child = numPicker.getChildAt(j);
//								if(child instanceof EditText){
//									((EditText)child).setTextColor(UserChartSettingsActivity.this.getResources().getColor(R.color.dark_gray));
//								}
							}
							
							/*
							LayoutParams params1 = new LayoutParams(120,
									LayoutParams.WRAP_CONTENT);
							params1.leftMargin = 0;
							params1.rightMargin = 30;
							numPicker.setLayoutParams(params1);
							*/
						}

						LinearLayout tpContainer = (LinearLayout) timePicker
								.getChildAt(0);
						LinearLayout tpSpinner = (LinearLayout) tpContainer
								.getChildAt(0);
						for (int i = 0; i < tpSpinner.getChildCount(); i++) {
							if (i == 1) {
								continue;
							}
							NumberPicker numPicker = (NumberPicker) tpSpinner
									.getChildAt(i);
							for(int j=0; j<numPicker.getChildCount(); j++){
								View child = numPicker.getChildAt(j);
								if(child instanceof EditText){
									((EditText)child).setTextColor(UserChartSettingsActivity.this.getResources().getColor(R.color.dark_gray));
								}else if(child instanceof TextView){
									((TextView)child).setTextColor(UserChartSettingsActivity.this.getResources().getColor(R.color.dark_gray));
								}
							}
						}

						initDatePicker(startTime, datePicker);
						initTimePicker(startTime, timePicker);

						AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(UserChartSettingsActivity.this, R.style.Theme_picker))
								.setView(view)
								.setTitle("請設定開始時間")
								.setIcon(android.R.drawable.ic_menu_more)
								.setPositiveButton(
										resources.getString(R.string.action_ok),
										new DialogInterface.OnClickListener() {

											public void onClick(
													DialogInterface dialog,
													int which) {
												int year = datePicker.getYear();
												int month = datePicker
														.getMonth();
												int day = datePicker
														.getDayOfMonth();
												int hour = timePicker
														.getCurrentHour();
												int minute = timePicker
														.getCurrentMinute();

												Calendar c = Calendar
														.getInstance();
												c.set(Calendar.YEAR, year);
												c.set(Calendar.MONTH, month);
												c.set(Calendar.DAY_OF_MONTH,
														day);
												c.set(Calendar.HOUR_OF_DAY,
														hour);
												c.set(Calendar.MINUTE, minute);
												c.set(Calendar.SECOND, 0);

												startTime = c.getTime();
												SimpleDateFormat sdf = new SimpleDateFormat(
														"yyyy-MM-dd HH:mm:ss");
												if (startTime != null) {
													tvTimePeriodStartTime.setText(sdf
															.format(startTime));
												}
											}
										})
								.setNegativeButton(
										resources
												.getString(R.string.action_cancel),
										new DialogInterface.OnClickListener() {

											public void onClick(
													DialogInterface dialog,
													int which) {

											}
										});
						builder.create().show();
					}

				});
		timePeriodEndTimeLayout
				.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {

						View view = View.inflate(getApplicationContext(),
								R.layout.date_time_picker, null);
						final DatePicker datePicker = (DatePicker) view
								.findViewById(R.id.date_picker);
						final TimePicker timePicker = (TimePicker) view
								.findViewById(R.id.time_picker);

						LinearLayout dpContainer = (LinearLayout) datePicker
								.getChildAt(0);
						LinearLayout dpSpinner = (LinearLayout) dpContainer
								.getChildAt(0);

						for (int i = 0; i < dpSpinner.getChildCount(); i++) {
							NumberPicker numPicker = (NumberPicker) dpSpinner
									.getChildAt(i);
							for(int j=0; j<numPicker.getChildCount(); j++){
								View child = numPicker.getChildAt(j);
								if(child instanceof EditText){
									((EditText)child).setTextColor(UserChartSettingsActivity.this.getResources().getColor(R.color.dark_gray));
								}
							}
						}

						LinearLayout tpContainer = (LinearLayout) timePicker
								.getChildAt(0);
						LinearLayout tpSpinner = (LinearLayout) tpContainer
								.getChildAt(0);
						for (int i = 0; i < tpSpinner.getChildCount(); i++) {
							if (i == 1) {
								continue;
							}
							NumberPicker numPicker = (NumberPicker) tpSpinner
									.getChildAt(i);
							for(int j=0; j<numPicker.getChildCount(); j++){
								View child = numPicker.getChildAt(j);
								if(child instanceof EditText){
									((EditText)child).setTextColor(UserChartSettingsActivity.this.getResources().getColor(R.color.dark_gray));
								}
							}
						}

						initDatePicker(endTime, datePicker);
						initTimePicker(endTime, timePicker);

						AlertDialog.Builder builder = new AlertDialog.Builder(
								UserChartSettingsActivity.this)
								.setView(view)
								.setTitle("請設定結束時間")
								.setIcon(android.R.drawable.ic_menu_more)
								.setPositiveButton(
										resources.getString(R.string.action_ok),
										new DialogInterface.OnClickListener() {

											public void onClick(
													DialogInterface dialog,
													int which) {
												int year = datePicker.getYear();
												int month = datePicker
														.getMonth();
												int day = datePicker
														.getDayOfMonth();
												int hour = timePicker
														.getCurrentHour();
												int minute = timePicker
														.getCurrentMinute();

												Calendar c = Calendar
														.getInstance();
												c.set(Calendar.YEAR, year);
												c.set(Calendar.MONTH, month);
												c.set(Calendar.DAY_OF_MONTH,
														day);
												c.set(Calendar.HOUR_OF_DAY,
														hour);
												c.set(Calendar.MINUTE, minute);
												c.set(Calendar.SECOND, 0);

												endTime = c.getTime();
												SimpleDateFormat sdf = new SimpleDateFormat(
														"yyyy-MM-dd HH:mm:ss");
												if (endTime != null) {
													tvTimePeriodEndTime.setText(sdf
															.format(endTime));
												}
											}
										})
								.setNegativeButton(
										resources
												.getString(R.string.action_cancel),
										new DialogInterface.OnClickListener() {

											public void onClick(
													DialogInterface dialog,
													int which) {

											}
										});
						builder.create().show();
					}

				});
		setupActionBar();
	}

	private void initDatePicker(Date time, DatePicker datePicker) {
		int year;
		int month;
		int day;

		Calendar c = Calendar.getInstance();
		if (time != null) {
			c.setTime(time);
		}
		year = c.get(Calendar.YEAR);
		month = c.get(Calendar.MONTH);
		day = c.get(Calendar.DAY_OF_MONTH);

		datePicker.init(year, month, day, null);
	}

	private void initTimePicker(Date time, TimePicker timePicker) {
		int hour;
		int minute;

		Calendar c = Calendar.getInstance();
		if (time != null) {
			c.setTime(time);
		}
		hour = c.get(Calendar.HOUR_OF_DAY);
		minute = c.get(Calendar.MINUTE);

		timePicker.setIs24HourView(true);
		timePicker.setCurrentHour(hour);
		timePicker.setCurrentMinute(minute);
	}

	private Date getStartTime(int preDefinedTimePeriod) {
		Date result = null;
		switch (preDefinedTimePeriod) {
		case TIME_PERIOD_TODAY: {
			Calendar now = Calendar.getInstance();
			now.set(Calendar.HOUR_OF_DAY, 0);
			now.set(Calendar.MINUTE, 0);
			now.set(Calendar.SECOND, 0);
			result = now.getTime();
			break;
		}
		case TIME_PERIOD_YESTERDAY: {
			Calendar now = Calendar.getInstance();
			now.set(Calendar.HOUR_OF_DAY, 0);
			now.set(Calendar.MINUTE, 0);
			now.set(Calendar.SECOND, 0);
			now.add(Calendar.DATE, -1);

			result = now.getTime();
			break;
		}
		case TIME_PERIOD_THIS_WEEK: {
			Calendar now = Calendar.getInstance();
			now.set(Calendar.HOUR_OF_DAY, 0);
			now.set(Calendar.MINUTE, 0);
			now.set(Calendar.SECOND, 0);
			while (now.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
				now.add(Calendar.DATE, -1);
			}

			result = now.getTime();
			break;
		}
		case TIME_PERIOD_LAST_WEEK: {
			Calendar now = Calendar.getInstance();
			now.set(Calendar.HOUR_OF_DAY, 0);
			now.set(Calendar.MINUTE, 0);
			now.set(Calendar.SECOND, 0);
			while (now.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
				now.add(Calendar.DATE, -1);
			}
			now.add(Calendar.DATE, -7);

			result = now.getTime();
			break;
		}
		case TIME_PERIOD_THIS_MONTH: {
			Calendar now = Calendar.getInstance();
			now.set(Calendar.HOUR_OF_DAY, 0);
			now.set(Calendar.MINUTE, 0);
			now.set(Calendar.SECOND, 0);
			now.set(Calendar.DAY_OF_MONTH, 1);

			result = now.getTime();
			break;
		}
		case TIME_PERIOD_LAST_MONTH: {
			Calendar now = Calendar.getInstance();
			now.set(Calendar.HOUR_OF_DAY, 0);
			now.set(Calendar.MINUTE, 0);
			now.set(Calendar.SECOND, 0);
			now.set(Calendar.DAY_OF_MONTH, 1);
			now.add(Calendar.MONTH, -1);
			result = now.getTime();
			break;
		}
		case TIME_PERIOD_LAST_3DAYS: {
			Calendar now = Calendar.getInstance();
			now.set(Calendar.HOUR_OF_DAY, 0);
			now.set(Calendar.MINUTE, 0);
			now.set(Calendar.SECOND, 0);
			now.add(Calendar.DATE, -2);
			result = now.getTime();
			break;
		}
		case TIME_PERIOD_LAST_5DAYS: {
			Calendar now = Calendar.getInstance();
			now.set(Calendar.HOUR_OF_DAY, 0);
			now.set(Calendar.MINUTE, 0);
			now.set(Calendar.SECOND, 0);
			now.add(Calendar.DATE, -4);
			result = now.getTime();
			break;
		}
		case TIME_PERIOD_LAST_7DAYS: {
			Calendar now = Calendar.getInstance();
			now.set(Calendar.HOUR_OF_DAY, 0);
			now.set(Calendar.MINUTE, 0);
			now.set(Calendar.SECOND, 0);
			now.add(Calendar.DATE, -6);
			result = now.getTime();
			break;
		}
		case TIME_PERIOD_LAST_1WEEK: {
			Calendar now = Calendar.getInstance();
			now.set(Calendar.HOUR_OF_DAY, 0);
			now.set(Calendar.MINUTE, 0);
			now.set(Calendar.SECOND, 0);
			while (now.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
				now.add(Calendar.DATE, -1);
			}
			now.add(Calendar.DATE, -7);

			result = now.getTime();
			break;
		}
		case TIME_PERIOD_LAST_2WEEKS: {
			Calendar now = Calendar.getInstance();
			now.set(Calendar.HOUR_OF_DAY, 0);
			now.set(Calendar.MINUTE, 0);
			now.set(Calendar.SECOND, 0);
			while (now.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
				now.add(Calendar.DATE, -1);
			}
			now.add(Calendar.DATE, -14);

			result = now.getTime();
			break;
		}
		case TIME_PERIOD_LAST_3WEEKS: {
			Calendar now = Calendar.getInstance();
			now.set(Calendar.HOUR_OF_DAY, 0);
			now.set(Calendar.MINUTE, 0);
			now.set(Calendar.SECOND, 0);
			while (now.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
				now.add(Calendar.DATE, -1);
			}
			now.add(Calendar.DATE, -21);

			result = now.getTime();
			break;
		}
		case TIME_PERIOD_LAST_1MONTH: {
			Calendar now = Calendar.getInstance();
			now.set(Calendar.HOUR_OF_DAY, 0);
			now.set(Calendar.MINUTE, 0);
			now.set(Calendar.SECOND, 0);
			now.set(Calendar.DAY_OF_MONTH, 1);
			now.add(Calendar.MONTH, -1);
			result = now.getTime();
			break;
		}
		case TIME_PERIOD_LAST_2MONTHS: {
			Calendar now = Calendar.getInstance();
			now.set(Calendar.HOUR_OF_DAY, 0);
			now.set(Calendar.MINUTE, 0);
			now.set(Calendar.SECOND, 0);
			now.set(Calendar.DAY_OF_MONTH, 1);
			now.add(Calendar.MONTH, -2);
			result = now.getTime();
			break;
		}
		case TIME_PERIOD_LAST_3MONTHS: {
			Calendar now = Calendar.getInstance();
			now.set(Calendar.HOUR_OF_DAY, 0);
			now.set(Calendar.MINUTE, 0);
			now.set(Calendar.SECOND, 0);
			now.set(Calendar.DAY_OF_MONTH, 1);
			now.add(Calendar.MONTH, -3);
			result = now.getTime();
			break;
		}
		}

		return result;
	}

	private Date getEndTime(int preDefinedTimePeriod) {
		Date startTime = getStartTime(preDefinedTimePeriod);
		if (startTime == null) {
			return null;
		} else {
			Calendar cal = Calendar.getInstance();
			cal.setTime(startTime);

			Date result = null;
			switch (preDefinedTimePeriod) {
			case TIME_PERIOD_TODAY: {
				cal.add(Calendar.DATE, 1);
				result = cal.getTime();
				break;
			}
			case TIME_PERIOD_YESTERDAY: {
				cal.add(Calendar.DATE, 1);
				result = cal.getTime();
				break;
			}
			case TIME_PERIOD_THIS_WEEK: {
				cal.add(Calendar.DATE, 7);
				result = cal.getTime();
				break;
			}
			case TIME_PERIOD_LAST_WEEK: {
				cal.add(Calendar.DATE, 7);
				result = cal.getTime();
				break;
			}
			case TIME_PERIOD_THIS_MONTH: {
				cal.add(Calendar.MONTH, 1);
				result = cal.getTime();
				break;
			}
			case TIME_PERIOD_LAST_MONTH: {
				cal.add(Calendar.MONTH, 1);
				result = cal.getTime();
				break;
			}
			case TIME_PERIOD_LAST_3DAYS: {
				cal.add(Calendar.DATE, 3);
				result = cal.getTime();
				break;
			}
			case TIME_PERIOD_LAST_5DAYS: {
				cal.add(Calendar.DATE, 5);
				result = cal.getTime();
				break;
			}
			case TIME_PERIOD_LAST_7DAYS: {
				cal.add(Calendar.DATE, 7);
				result = cal.getTime();
				break;
			}
			case TIME_PERIOD_LAST_1WEEK: {
				result = getEndTime(TIME_PERIOD_TODAY);
				break;
			}
			case TIME_PERIOD_LAST_2WEEKS: {
				result = getEndTime(TIME_PERIOD_TODAY);
				break;
			}
			case TIME_PERIOD_LAST_3WEEKS: {
				result = getEndTime(TIME_PERIOD_TODAY);
				break;
			}
			case TIME_PERIOD_LAST_1MONTH: {
				result = getEndTime(TIME_PERIOD_TODAY);
				break;
			}
			case TIME_PERIOD_LAST_2MONTHS: {
				result = getEndTime(TIME_PERIOD_TODAY);
				break;
			}
			case TIME_PERIOD_LAST_3MONTHS: {
				result = getEndTime(TIME_PERIOD_TODAY);
				break;
			}
			}
			return result;
		}
	}
}
