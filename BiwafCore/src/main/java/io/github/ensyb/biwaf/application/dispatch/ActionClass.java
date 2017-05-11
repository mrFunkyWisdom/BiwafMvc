package io.github.ensyb.biwaf.application.dispatch;

import java.lang.reflect.Method;

import io.github.ensyb.biwaf.application.annotations.Request;

final class ActionClass {

	private final Method method;
	private final Class<?> actionClass;

	public ActionClass(Method method, Class<?> actionClass) {
		this.method = method;
		this.actionClass = actionClass;
	}

	public Method getMethod() {
		return method;
	}

	public Class<?> getControllerClass() {
		return actionClass;
	}

	public static class RequestMethod {
		private Request.HttpMethod method;
		private String path;

		public RequestMethod(Request.HttpMethod method, String path) {
			this.method = method;
			this.path = path;
		}

		public Request.HttpMethod getMethod() {
			return method;
		}

		public String getPath() {
			return path;
		}
	}

}
