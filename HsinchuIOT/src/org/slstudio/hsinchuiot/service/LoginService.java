package org.slstudio.hsinchuiot.service;

import java.util.TimeZone;

import org.slstudio.hsinchuiot.Constants;
import org.slstudio.hsinchuiot.model.IOTMonitorThreshold;
import org.slstudio.hsinchuiot.model.Session;
import org.slstudio.hsinchuiot.model.User;
import org.slstudio.hsinchuiot.service.http.HttpConfig;
import org.slstudio.hsinchuiot.service.http.HttpRequest;
import org.slstudio.hsinchuiot.service.http.NoneAuthedHttpRequest;
import org.slstudio.hsinchuiot.service.http.RequestControl;
import org.slstudio.hsinchuiot.service.http.RequestListener;
import org.slstudio.hsinchuiot.util.EncryptUtil;
import org.slstudio.hsinchuiot.util.IOTLog;

public class LoginService {
	private static LoginService _instance = null;

	private Object lock = new Object();
	private boolean succeed = false;
	private String sessionID = null;
	private User loginUser = null;
	private IOTException exception = null;

	private LoginService() {

	}

	public static LoginService getInstance() {
		if (_instance == null) {
			_instance = new LoginService();
		}
		return _instance;
	}

	public boolean login(String loginName, String password) throws IOTException {

		HttpRequest request = new NoneAuthedHttpRequest(
				new HttpConfig.GetHttpConfig(),
				Constants.ServerAPIURI.GET_SESSION_ID);
		request.addParameter("dataType", "json");
		ServiceContainer.getInstance().getHttpHandler()
				.doRequest(request, new RequestListener<Session>() {

					@Override
					public void onRequestStart() {
						// TODO Auto-generated method stub

					}

					@Override
					public void onRequestGetControl(RequestControl control) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onRequestCancelled() {
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
					public void onRequestResult(Session result) {
						// TODO Auto-generated method stub
						succeed = true;
						sessionID = result.getSessionID();
					}

					@Override
					public void onRequestComplete() {
						// TODO Auto-generated method stub
						synchronized (lock) {
							lock.notify();
						}
					}

				});
		synchronized (lock) {
			try {
				lock.wait();
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			IOTLog.d("LOGINService", sessionID == null ? "null" : sessionID);

		}

		if (!succeed && exception != null)
			throw exception;

		HttpRequest request2 = new NoneAuthedHttpRequest(
				new HttpConfig.GetHttpConfig(), Constants.ServerAPIURI.LOGIN);

		String mangledPwd = EncryptUtil
				.getStringMD5(password + ":" + sessionID);

		request2.addParameter("dataType", "json");
		request2.addParameter("__session_id", sessionID);
		request2.addParameter("username", loginName);
		request2.addParameter("mangled_password", mangledPwd);
		request2.addParameter("lang", "zh-cn");
		request2.addParameter("timezone",
				Integer.toString(TimeZone.getDefault().getRawOffset()));

		ServiceContainer.getInstance().getHttpHandler()
				.doRequest(request2, new RequestListener<User>() {

					@Override
					public void onRequestStart() {
						// TODO Auto-generated method stub

					}

					@Override
					public void onRequestGetControl(RequestControl control) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onRequestCancelled() {
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
					public void onRequestResult(User result) {
						// TODO Auto-generated method stub
						succeed = true;
						loginUser = result;
					}

					@Override
					public void onRequestComplete() {
						// TODO Auto-generated method stub
						synchronized (lock) {
							lock.notify();
						}
					}

				});
		synchronized (lock) {
			try {
				lock.wait();
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			IOTLog.d("LOGINService",
					loginUser == null ? "null" : loginUser.getUserID());

		}

		if (!succeed && exception != null)
			throw exception;

		if (succeed) {
			ServiceContainer.getInstance().getSessionService()
					.setSessionID(sessionID);
			ServiceContainer.getInstance().getSessionService()
					.setLoginUser(loginUser);
			ServiceContainer
					.getInstance()
					.getSessionService()
					.setSessionValue(
							SessionService.THRESHOLD_WARNING,
							new IOTMonitorThreshold(Integer.MIN_VALUE, 800, 16,
									27, 0, 100));
			ServiceContainer
					.getInstance()
					.getSessionService()
					.setSessionValue(
							SessionService.THRESHOLD_BREACH,
							new IOTMonitorThreshold(Integer.MIN_VALUE, 1000,
									15, 28, 0, 100));
		}

		return succeed;
	}
}
