package org.slstudio.hsinchuiot.upgrade;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.slstudio.hsinchuiot.Constants;
import org.slstudio.hsinchuiot.R;
import org.slstudio.hsinchuiot.service.ServiceContainer;
import org.slstudio.hsinchuiot.service.http.DownloadAsyncTask;
import org.slstudio.hsinchuiot.service.http.DownloadListener;
import org.slstudio.hsinchuiot.service.http.HttpConfig;
import org.slstudio.hsinchuiot.service.http.HttpRequest;
import org.slstudio.hsinchuiot.service.http.RequestListener;
import org.slstudio.hsinchuiot.service.http.RequestListener.DefaultRequestListener;
import org.slstudio.hsinchuiot.service.http.NoneAuthedHttpRequest;
import org.slstudio.hsinchuiot.util.IOTLog;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.widget.RemoteViews;
import android.widget.Toast;

public class UpgradeController extends
		DefaultRequestListener<CheckUpgradeResult> {

	public static final String TAG = "UC";

	public static final int MSG_SHOW_WAIT = 1000;

	public static final int MSG_SHOW_ERROR = 1001;

	public static final int MSG_FORCE_DOWNLOAD_COMPLETE = 1002;

	private UpgradeProcessor processor;
	private Context context;
	private UpgradeHandler upgradeHandler;

	private CheckUpgradeResult upgradeInfo;

	private class UpgradeProcessor extends HandlerThread {
		UpgradeProcessor() {
			super("UPGRADE_PROCESSOR");
		}
	}

	private class UpgradeHandler extends Handler {

		public UpgradeHandler(Looper looper) {
			super(looper);
		}

	}

	public UpgradeController(Context context) {
		this.context = context;
		processor = new UpgradeProcessor();
		processor.start();
		upgradeHandler = new UpgradeHandler(processor.getLooper());

	}

	private Runnable mrun;
	private NotificationManager mNotificationManager;
	private Notification notification;
	private RemoteViews remoteviews;
	private int count;

	public void checkVersion(RequestListener<CheckUpgradeResult> listener) {
		PackageManager nPackageManager = context.getPackageManager();
		try {
			final PackageInfo nPackageInfo = nPackageManager
					.getPackageInfo(context.getPackageName(),
							PackageManager.GET_CONFIGURATIONS);
			HttpRequest request = new NoneAuthedHttpRequest(
					new HttpConfig.PostHttpConfig(),
					Constants.ServerAPIURI.COMMON_CHKVERSION);
			if (ServiceContainer.getInstance().getContext().getPackageName()
					.equals("org.slstudio.hsinchuiot")) {
				request.addParameter("client_type", "1");
			} else {
				request.addParameter("client_type", "3");
			}
			request.addParameter("version", nPackageInfo.versionName);

			if (listener == null) {
				listener = this;
			}
			ServiceContainer.getInstance().getHttpHandler()
					.doRequest(request, listener);
			IOTLog.i(TAG, "Current version is " + nPackageInfo.versionName);
		} catch (NameNotFoundException e1) {
			IOTLog.e(TAG, e1.getMessage());
			e1.printStackTrace();
		}

	}

	public void sendNotification() {
		PackageManager nPackageManager = context.getPackageManager();
		try {
			final PackageInfo nPackageInfo = nPackageManager
					.getPackageInfo(context.getPackageName(),
							PackageManager.GET_CONFIGURATIONS);

			mNotificationManager = (NotificationManager) context
					.getSystemService(Context.NOTIFICATION_SERVICE);
			notification = new Notification(nPackageInfo.applicationInfo.icon,
					nPackageInfo.applicationInfo.name,
					System.currentTimeMillis());
			notification.flags |= Notification.FLAG_AUTO_CANCEL;
			remoteviews = new RemoteViews(context.getPackageName(),
					R.layout.common_upgrade_download_bar);
			notification.contentView = remoteviews;

			count = 0;
			mrun = new Runnable() {

				@Override
				public void run() {
					if (count < 98) {
						remoteviews.setProgressBar(
								R.id.common_upgrade_download_process_bar, 100,
								count, false);
						remoteviews.setTextViewText(
								R.id.common_download_percent_text, count + "%");

						mNotificationManager.notify(8888, notification);
						upgradeHandler.postDelayed(mrun, 1000);
					} else {
						remoteviews.setProgressBar(
								R.id.common_upgrade_download_process_bar, 100,
								100, false);
						remoteviews.setTextViewText(
								R.id.common_download_percent_text, 100 + "%");
						PendingIntent pendingintent = PendingIntent
								.getActivity(context, 0, getInstallIntent(),
										PendingIntent.FLAG_UPDATE_CURRENT);

						notification.contentIntent = pendingintent;
						mNotificationManager.notify(8888, notification);
						Toast.makeText(context,
								R.string.common_upgrade_download_over,
								Toast.LENGTH_SHORT).show();
					}
				}
			};
			upgradeHandler.postDelayed(mrun, 1000);
			File file = new File(Constants.UPGRADE_FILE_PATH,
					upgradeInfo.getFileName());
			if (!file.exists()) {
				DownloadAsyncTask updateAsyncTask = new DownloadAsyncTask(file,
						new DownloadListener() {

							@Override
							public void onStart() {

							}

							@Override
							public void onProcessUpdate(int value) {
								count = value;

							}

							@Override
							public void onComplete(File file) {

							}

							@Override
							public void onException(Exception exception) {

							}
						});
				try {
					IOTLog.i(TAG, "versionInfo getPackageName" + upgradeInfo);

					IOTLog.i(TAG, "upgrade url =" + upgradeInfo.getUrl());
					updateAsyncTask.execute(new URL(upgradeInfo.getUrl()));
				} catch (MalformedURLException e) {
					IOTLog.e(TAG, e.getMessage());
				}
			} else {
				count = 100;

			}
		} catch (NameNotFoundException e1) {
			IOTLog.e(TAG, e1.getMessage());
			e1.printStackTrace();
		}
	}

	public void install() {
		Intent notifyIntent = getInstallIntent();
		context.startActivity(notifyIntent);
	}

	private Intent getInstallIntent() {
		Intent notifyIntent = new Intent(Intent.ACTION_VIEW);
		notifyIntent.setDataAndType(Uri.fromFile(new File(
				Constants.UPGRADE_FILE_PATH, upgradeInfo.getFileName())),
				"application/vnd.android.package-archive");
		return notifyIntent;
	}

	@Override
	public void onRequestResult(CheckUpgradeResult result) {
		upgradeInfo = result;
		IOTLog.i(TAG, "upgrade info  " + upgradeInfo);
		final int code = upgradeInfo.getUpgradeSuggest();
		if (code != CheckUpgradeResult.NEEDLESS_UPGRADE) {
			final Intent intent = new Intent(context,
					StartInstallActivity.class);
			intent.putExtra(Constants.BundleKey.UPGRAGE_TYPE, code);
			context.startActivity(intent);
		}
	}

}
