package org.slstudio.hsinchuiot.service.http;

import org.slstudio.hsinchuiot.AppConfig;

public abstract class HttpConfig {

	private Method method = Method.POST;
	private String mimeType = "image/png";

	public abstract String getHostName();

	public abstract int getHostPort();

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	public Method getMethod() {
		return method;
	}

	public void setMethod(Method method) {
		this.method = method;
	}

	public enum Method {
		GET, POST, PUT, DELETE
	}

	static public class DefaultHttpConfig extends HttpConfig {

		@Override
		public String getHostName() {
			return AppConfig.HTTP_SCHEME + AppConfig.HTTP_DOMAIN;
		}

		@Override
		public int getHostPort() {
			return AppConfig.HTTP_PORT;
		}

	}

	static public class GetHttpConfig extends DefaultHttpConfig {

		public Method getMethod() {
			return Method.GET;
		}
	}

	static public class PostHttpConfig extends DefaultHttpConfig {

		public Method getMethod() {
			return Method.POST;
		}
	}

	static public class PutHttpConfig extends DefaultHttpConfig {

		public Method getMethod() {
			return Method.PUT;
		}
	}

	static public class DeleteHttpConfig extends DefaultHttpConfig {

		public Method getMethod() {
			return Method.DELETE;
		}
	}
}
