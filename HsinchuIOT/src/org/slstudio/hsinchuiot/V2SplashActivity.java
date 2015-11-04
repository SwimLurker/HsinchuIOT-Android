package org.slstudio.hsinchuiot;

import java.io.File;
import java.util.Locale;

import org.slstudio.hsinchuiot.model.User;
import org.slstudio.hsinchuiot.service.IOTException;
import org.slstudio.hsinchuiot.service.LoginService;
import org.slstudio.hsinchuiot.service.ServiceContainer;
import org.slstudio.hsinchuiot.util.ImageUtil;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.Window;

public class V2SplashActivity extends BaseActivity {

	private final int SPLASH_DISPLAY_LENGTH = 1000;
	private ProgressDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.v2_activity_splash);

		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				// show progress dialog in UI thread
				showProgressDialog();
				// start new thread for handle the login
				new Thread(new Runnable() {

					@Override
					public void run() {
						prepare();
						login();
						//showDebugActivity(Constants.Action.HSINCHUIOT_USER_CHART_SETTINGS);
					}

				}).start();

			}

		}, SPLASH_DISPLAY_LENGTH);

	}

	private void showProgressDialog() {
		progressDialog = ProgressDialog.show(V2SplashActivity.this, "", getString(R.string.common_please_wait), true);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.setCancelable(true);
		progressDialog.setOnCancelListener(new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
			}
		});
	}

	private void showLoginActivity() {
		Intent i = new Intent(Constants.Action.HSINCHUIOT_LOGIN);
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(i);
		progressDialog.dismiss();
		finish();

	}

	protected void gotoNormalUserMainScreen() {
		Intent intent = new Intent(Constants.Action.HSINCHUIOT_USER_MAIN);
		startActivity(intent);
		progressDialog.dismiss();
		finish();
	}

	protected void gotoAdminUserMainScreen() {
		Intent intent = new Intent(Constants.Action.HSINCHUIOT_SUPERUSER_MAIN);
		startActivity(intent);
		progressDialog.dismiss();
		finish();
	}
	
	private void showDebugActivity(String action){
		Intent intent = new Intent(action);
		startActivity(intent);
		progressDialog.dismiss();
		finish();
	}

	private void prepare() {
		//prepareThumbnailImage();
		ServiceContainer.getInstance().getUpgradeController().resetFirstTimeCheckFlag();
		setCurrentLanguage();
		
	}

	private void prepareThumbnailImage() {
		String imageDir = Constants.ImageLoader.IMAGE_ENGINE_CACHE;
		String siteImageFilename = "site_59.png";
		String thumbnailDir = imageDir + "/thumbnail";

		File dir = new File(thumbnailDir);
		if (!dir.exists()) {
			dir.mkdirs();
		}

		Bitmap siteImage = ImageUtil.getBitmapFromResource(this.getResources(), R.drawable.site_a);
		ImageUtil.writeBitmapToFile(siteImage, imageDir + "/" + siteImageFilename);

		Bitmap thumbnailImage = ImageUtil.getImageThumbnail(imageDir + "/" + siteImageFilename, 128, 128);
		ImageUtil.writeBitmapToFile(thumbnailImage, thumbnailDir + "/" + siteImageFilename);

	}

	private void login() {
		String loginName = ServiceContainer.getInstance().getPerferenceService().getValue(V2SplashActivity.this,
				Constants.PreferenceKey.LOGINNAME);
		String password = ServiceContainer.getInstance().getPerferenceService().getValue(V2SplashActivity.this,
				Constants.PreferenceKey.PASSWORD);

		if (loginName.equals("") || password.equals("")) {
			showLoginActivity();
		} else {
			try {
				if (LoginService.getInstance().login(loginName, password)) {
					ServiceContainer.getInstance().getSessionService().setSessionValue(Constants.SessionKey.THRESHOLD_WARNING,
							LoginService.getWarningThreshold(this));
					ServiceContainer.getInstance().getSessionService().setSessionValue(Constants.SessionKey.THRESHOLD_BREACH,
							LoginService.getBreachThreshold(this));
					User loginUser = ServiceContainer.getInstance().getSessionService().getLoginUser();

					if (loginUser == null) {
						showLoginActivity();
					} else {
						if (loginUser.isAdminUser()) {
							gotoAdminUserMainScreen();
						} else if(loginUser.isNormalUser()) {
							int refreshTime = 10;
							String refreshTimeStr = ServiceContainer.getInstance().getPerferenceService().getValue(this, Constants.PreferenceKey.REALTIME_DATA_MONITOR_REFRESH_TIME);
							if(!"".equals(refreshTimeStr)){
								refreshTime = Integer.parseInt(refreshTimeStr);
							}
							
							ServiceContainer.getInstance().getSessionService().setSessionValue(Constants.SessionKey.REALTIME_DATA_MONITOR_REFRESH_TIME, refreshTime);
							gotoNormalUserMainScreen();
						}else{
							showLoginActivity();
						}
					}
				} else {
					showLoginActivity();
				}
			} catch (IOTException e) {
				showLoginActivity();
			}

		}
	}
	
	private void setCurrentLanguage() {
		String currentLanguage = ServiceContainer.getInstance()
				.getPerferenceService()
				.getValue(this, Constants.PreferenceKey.LANGUAGE);

		Resources resources = getResources();
		Configuration config = resources.getConfiguration();
		
		if(!currentLanguage.equals("")){
			switchLanguage(currentLanguage);
		}else if((!config.locale.equals(Locale.ENGLISH))
				&&(!config.locale.equals(Locale.CHINA))
				&&(!config.locale.equals(Locale.CHINA))){
			switchLanguage(Constants.Language.TW);
		}
	}

	private void switchLanguage(String language) {
		Resources resources = getResources();
		Configuration config = resources.getConfiguration();
		DisplayMetrics dm = resources.getDisplayMetrics();
		if (language.equals(Constants.Language.EN)) {
			config.locale = Locale.ENGLISH;
		} else if (language.equals(Constants.Language.CN)) {
			config.locale = Locale.CHINA;
		} else {
			config.locale = Locale.TAIWAN;
		}
		resources.updateConfiguration(config, dm);

		ServiceContainer.getInstance().getPerferenceService()
				.setValue(this, Constants.PreferenceKey.LANGUAGE, language);
	}
}
