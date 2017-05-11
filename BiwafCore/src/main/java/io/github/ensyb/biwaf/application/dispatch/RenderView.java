package io.github.ensyb.biwaf.application.dispatch;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import io.github.ensyb.biwaf.application.annotations.RequestParam;
import io.github.ensyb.biwaf.application.dispatch.response.BiwafContext;
import io.github.ensyb.biwaf.application.dispatch.response.Response;
import io.github.ensyb.biwaf.application.injection.BiwafInjectionException;
import io.github.ensyb.biwaf.application.injection.Injector;
import io.github.ensyb.biwaf.application.injection.scan.TypeResolver;

public final class RenderView {

	public void invokeController(HttpServletRequest request, HttpServletResponse response, ActionClass actionMethod) {
		Method method = actionMethod.getMethod();
		Parameter[] parameters = method.getParameters();
		List<Object> methodParamList = getRequestParamMap(request, parameters);

		//i will replace this with loading once not per every request
		String basePath = (String)request.getServletContext().getAttribute("basepath");
		//bindings can be loaded on application startup
		Object controllerMethodResult = invokeActionMehod(actionMethod.getControllerClass(), method, basePath,
				methodParamList.toArray());

		resolveView(request, response, controllerMethodResult, actionMethod);

	}

	private void resolveView(HttpServletRequest request, HttpServletResponse response, Object controllerMethodResult,
			ActionClass controller) {
		Response methodResponse = (Response) controllerMethodResult;
		methodResponse.render(new BiwafContext(request, response));
	}

	private Object invokeActionMehod(Class<?> klasa, Method method, String injectionPath, Object... parametarList) {
		Class<?>[] parametarTypes = method.getParameterTypes();
		int argCount = parametarList == null ? 0 : parametarList.length;

		if (argCount != parametarTypes.length)
			throw new BiwafInjectionException("error on action request method, parametar count dont match");

		try {
			for (int i = 0; i < argCount; i++)
				parametarList[i] = TypeResolver.tryRecreateType(parametarList[i], parametarTypes[i]);

			// run injection if necessary
			Injector injector = new Injector(injectionPath);
			return method.invoke(injector.createInstanceWithInjectedField(klasa).orElse(klasa.newInstance()), parametarList);
		
		} catch (Exception e) {
			throw new BiwafInjectionException("method invocation failed");
		}
	}

	private List<Object> getRequestParamMap(HttpServletRequest request, Parameter[] methodParametarList) {
		List<Object> requestParamList = new ArrayList<>();

		for (Parameter parametar : methodParametarList) {
			RequestParam annotation = parametar.getAnnotation(RequestParam.class);
			addParametar(request, requestParamList, parametar.getType(),
					(annotation == null || annotation.value().trim().isEmpty()) ? "" : annotation.value());

		}
		return requestParamList;
	}

	private void addParametar(HttpServletRequest request, List<Object> requestParamList, Class<?> parametarType,
			String parametarName) {
		String[] paramValues = request.getParameterValues(parametarName);
		if (paramValues != null) {
			if (1 == paramValues.length) {
				String paramValue = paramValues[0];
				requestParamList.add(TypeResolver.tryRecreateType(paramValue, parametarType));
			}
		} else {
			String emptyParametar = "";
			requestParamList.add(emptyParametar);
		}
	}

}
