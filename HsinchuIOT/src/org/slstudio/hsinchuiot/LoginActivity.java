package org.slstudio.hsinchuiot;

import java.util.TimeZone;

import org.slstudio.hsinchuiot.model.Session;
import org.slstudio.hsinchuiot.model.User;
import org.slstudio.hsinchuiot.service.IOTException;
import org.slstudio.hsinchuiot.service.LoginService;
import org.slstudio.hsinchuiot.service.ServiceContainer;
import org.slstudio.hsinchuiot.service.SessionService;
import org.slstudio.hsinchuiot.service.http.ForgroundRequestListener;
import org.slstudio.hsinchuiot.service.http.HttpConfig;
import org.slstudio.hsinchuiot.service.http.HttpRequest;
import org.slstudio.hsinchuiot.service.http.NoneAuthedHttpRequest;
import org.slstudio.hsinchuiot.service.http.RequestControl;
import org.slstudio.hsinchuiot.service.http.RequestListener;
import org.slstudio.hsinchuiot.util.EncryptUtil;
import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

public class LoginActivity extends BaseActivity {

	private Object lock = new Object();

	private EditText usernameET;
	private EditText passwordET;
	private Button signInBtn;
	private GetSessionIDListener listener;
	private LoginListener listener2;
	private Handler handler;

	private String sessionID;
	private String username;
	private String password;
	private User loginUser;

	private boolean succeed;
	private IOTException exception;

	private CheckBox rememberPassword;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.common_login);
		
		
		usernameET = (EditText) findViewById(R.id.login_username);

		passwordET = (EditText) findViewById(R.id.login_password);
		signInBtn = (Button) findViewById(R.id.login_signin);
		signInBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				doSignIn();
				// gotoMainScreen();
			}
		});

		rememberPassword = (CheckBox) findViewById(R.id.remeber_password);

		listener = new GetSessionIDListener(this, true, getString(R.string.common_please_wait));
		listener2 = new LoginListener();
		handler = new Handler();
		ServiceContainer.getInstance().getUpgradeController().checkVersion(null, true);
		
	}

	protected void doSignIn() {

		username = usernameET.getText().toString().trim();
		password = passwordET.getText().toString().trim();

		if (username.equals("")) {
			setException(new IOTException(-1, getString(R.string.error_message_require_loginname_text)));
			showDialog(DIALOG_ERROR);
			return;
		}

		if (password.equals("")) {
			setException(new IOTException(-1, getString(R.string.error_message_require_pwd_text)));
			showDialog(DIALOG_ERROR);
			return;
		}

		HttpRequest request = new NoneAuthedHttpRequest(new HttpConfig.GetHttpConfig(),
				Constants.ServerAPIURI.GET_SESSION_ID);

		request.addParameter("dataType", "xml");

		ServiceContainer.getInstance().getHttpHandler().doRequest(request, listener);

	}

	protected void gotoNormalUserMainScreen() {
		Intent intent = new Intent(Constants.Action.HSINCHUIOT_USER_MAIN);
		startActivity(intent);
		finish();
	}

	protected void gotoAdminUserMainScreen() {
		Intent intent = new Intent(Constants.Action.HSINCHUIOT_SUPERUSER_MAIN);
		startActivity(intent);
		finish();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == 1) {
			finish();
		}
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			moveTaskToBack(true);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private class GetSessionIDListener extends ForgroundRequestListener<Session> {
		private RequestControl control;

		public GetSessionIDListener(Context context, boolean isShowProgressDialog, String content) {
			super(context, isShowProgressDialog, content);
		}

		@Override
		public void onRequestCancelled() {
			if (control != null)
				control.cancel();

		}

		@Override
		public void onRequestResult(final Session result) {
			sessionID = result.getSessionID();

			String pwdMD5 = EncryptUtil.getStringMD5(password);

			String mangledPwd = EncryptUtil.getStringMD5(pwdMD5 + ":" + sessionID);

			HttpRequest request2 = new NoneAuthedHttpRequest(new HttpConfig.GetHttpConfig(),
					Constants.ServerAPIURI.LOGIN);

			request2.addParameter("dataType", "json");
			request2.addParameter("__session_id", sessionID);
			request2.addParameter("username", username);
			request2.addParameter("mangled_password", mangledPwd);
			request2.addParameter("lang", "zh-cn");
			request2.addParameter("timezone", Integer.toString(TimeZone.getDefault().getRawOffset()));
			ServiceContainer.getInstance().getHttpHandler().doRequest(request2, listener2);
			synchronized (lock) {
				try {
					lock.wait();
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			}

			if (!succeed) {
				onRequestError(exception);
				return;
			}

			if (rememberPassword.isChecked()) {
				ServiceContainer.getInstance().getPerferenceService().setValue(
						Constants.PreferenceKey.LOGINNAME, username);

				ServiceContainer.getInstance().getPerferenceService().setValue(
						Constants.PreferenceKey.PASSWORD, EncryptUtil.getStringMD5(password));
			} else {
				ServiceContainer.getInstance().getPerferenceService().setValue(
						Constants.PreferenceKey.LOGINNAME, null);

				ServiceContainer.getInstance().getPerferenceService().setValue(
						Constants.PreferenceKey.PASSWORD, null);
			}

			ServiceContainer.getInstance().getSessionService().setSessionID(sessionID);

			loginUser.setLoginName(username);
			loginUser.setPassword(pwdMD5);
			ServiceContainer.getInstance().getSessionService().setLoginUser(loginUser);

			ServiceContainer.getInstance().getSessionService().setSessionValue(Constants.SessionKey.THRESHOLD_WARNING,
					LoginService.getWarningThreshold(LoginActivity.this));
			ServiceContainer.getInstance().getSessionService().setSessionValue(Constants.SessionKey.THRESHOLD_BREACH,
					LoginService.getBreachThreshold(LoginActivity.this));

			if(loginUser.isNormalUser()){
				int refreshTime = 10;
				String refreshTimeStr = ServiceContainer.getInstance().getPerferenceService().getValue(Constants.PreferenceKey.REALTIME_DATA_MONITOR_REFRESH_TIME);
				if(!"".equals(refreshTimeStr)){
					refreshTime = Integer.parseInt(refreshTimeStr);
				}
				
				ServiceContainer.getInstance().getSessionService().setSessionValue(Constants.SessionKey.REALTIME_DATA_MONITOR_REFRESH_TIME, refreshTime);
			}
			
			handler.post(new Runnable() {

				@Override
				public void run() {
					/*
					 * username.setText(ServiceContainer.getInstance ()
					 * .getPerferenceService()
					 * .getSessionId(LoginActivity.this));
					 */
					if (loginUser.isAdminUser()) {
						gotoAdminUserMainScreen();
					} else if (loginUser.isNormalUser()){
						gotoNormalUserMainScreen();
					}else{
						setException(new IOTException(-1, getString(R.string.error_message_user_permission_wrong)));
						showDialog(DIALOG_ERROR);
						return;
					}
				}
			});

		}

		@Override
		public void onRequestGetControl(RequestControl control) {
			this.control = control;
		}
	}

	private class LoginListener implements RequestListener<User> {
		private RequestControl control;

		@Override
		public void onRequestCancelled() {
			if (control != null)
				control.cancel();

		}

		@Override
		public void onRequestResult(final User result) {
			succeed = true;
			loginUser = result;
		}

		@Override
		public void onRequestGetControl(RequestControl control) {
			this.control = control;
		}

		@Override
		public void onRequestStart() {
			// TODO Auto-generated method stub

		}

		@Override
		public void onRequestError(Exception e) {
			succeed = false;
			if (e instanceof IOTException) {
				exception = (IOTException) e;
			} else {
				exception = new IOTException(-1, e.getMessage());
			}
		}

		@Override
		public void onRequestComplete() {
			synchronized (lock) {
				lock.notify();
			}
		}
	}

	protected void setupActionBar() {
		ActionBar actionBar = getActionBar();
		actionBar.setHomeButtonEnabled(true);
		actionBar.setTitle(R.string.common_sign_in);
		super.setupActionBar();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		final int btnId = item.getItemId();
		if (btnId == android.R.id.home) {
			finish();
		}
		return true;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}

}
