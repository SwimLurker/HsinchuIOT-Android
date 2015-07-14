package org.slstudio.hsinchuiot;

import java.io.File;
import java.util.Iterator;
import java.util.Stack;

import org.apache.log4j.Level;
import org.slstudio.hsinchuiot.service.ServiceContainer;
import org.slstudio.hsinchuiot.service.push.ServerPushHelper;
import org.slstudio.hsinchuiot.util.IOTLog;
import org.slstudio.hsinchuiot.util.ImageUtil;

import com.baidu.android.pushservice.PushManager;
import com.baidu.frontia.FrontiaApplication;
import com.baidu.mapapi.SDKInitializer;

import de.mindpipe.android.logging.log4j.LogConfigurator;
import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Environment;

public class IOTApplication extends Application {
	private static Stack<BaseActivity> stack = new Stack<BaseActivity>();

	@Override
	public void onTerminate() {
		super.onTerminate();

		onDestroy();
		System.exit(0);
	}

	private void onDestroy() {
		//ServerPushHelper.setBind(getApplicationContext(), false, null, null);
		//PushManager.stopWork(getApplicationContext());

	}

	@Override
	public void onCreate() {

		super.onCreate();
		CrashHandler crashHandler = CrashHandler.getInstance();
		crashHandler.init(getApplicationContext());
		//ServerPushHelper.setBind(this, false, null, null);
		ServiceContainer.getInstance().init(getApplicationContext());
		FrontiaApplication.initFrontiaApplication(getApplicationContext());
		try {
			LogConfigurator logConfigurator = new LogConfigurator();

			String logDirPath = Constants.LOG_FILE_PATH;

			String logFilePath = logDirPath + "iot.log";
			checkAndCreateLogFolder(logDirPath);
			logConfigurator.setFileName(logFilePath);
			logConfigurator.setRootLevel(Level.INFO);
			// logConfigurator.setLevel("org.apache", Level.ERROR);
			logConfigurator.setFilePattern("%d %-5p [%c{2}]-[%L] %m%n");
			logConfigurator.setUseFileAppender(true);
			logConfigurator.setMaxFileSize(1024 * 1024 * 10);
			logConfigurator.setMaxBackupSize(7);
			logConfigurator.setImmediateFlush(true);
			logConfigurator.configure();
			IOTLog.f("App", "AD App start...");

			if (Environment.getExternalStorageDirectory() != null
					&& Environment.getExternalStorageDirectory().exists()) {
				File file = new File(Constants.ImageLoader.IMAGE_ENGINE_CACHE);
				if (!file.exists()) {
					file.mkdirs();
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		ImageUtil.initImageEngine(this,
				Constants.ImageLoader.IMAGE_ENGINE_CACHE);

		try {
			PackageManager packageManager = getApplicationContext()
					.getPackageManager();
			PackageInfo packInfo = packageManager.getPackageInfo(
					getPackageName(), 0);
			String version = packInfo.versionName;
			ServiceContainer.getInstance().setVersion(version);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		SDKInitializer.initialize(this);
	}

	public void checkAndCreateLogFolder(String path) {
		File file = new File(path);
		if (!file.exists()) {
			file.mkdirs();
		}
	}

	public void push(BaseActivity bact) {
		if (!stack.contains(bact)) {
			stack.push(bact);
		}
	}

	public void remove(BaseActivity act) {
		stack.remove(act);
		act.finish();
	}

	public void finishAll() {
		for (Iterator<BaseActivity> iterator = stack.iterator(); iterator
				.hasNext();) {
			BaseActivity baseActivity = iterator.next();
			iterator.remove();
			baseActivity.finish();
		}
	}

}
