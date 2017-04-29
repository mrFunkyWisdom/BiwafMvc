package io.github.ensyb.biwaf.application.injection;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import io.github.ensyb.biwaf.application.injection.scan.TypeResolver;

final class FieldInjection {

	private final Object onBean;
	private final String withName;
	private final Object value;

	public FieldInjection(final Object onBean, final String withName, final Object value) {
		this.onBean = onBean;
		this.withName = withName;
		this.value = value;
	}

	public void setProperty() {
		// try to inject field if direct setter is provided for field
		try {
			this.invokeSetterOfField(this.onBean, this.withName, this.value);
		} catch (NoSuchMethodException e) {
			// if failed to use setter inject direct on field
			this.injectOnField(this.onBean, this.withName, this.value);

		}
	}

	private Object invokeSetterOfField(Object object, String fieldName, Object valueToSet)
			throws NoSuchMethodException {
		String fieldNameWithFirstLetterCapitalized = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);

		String setterPrefix = "set";
		String methodName = setterPrefix + fieldNameWithFirstLetterCapitalized;
		Method method = findMethodOfName(object, methodName);

		try {
			Class<?>[] parameterList = method.getParameterTypes();
			valueToSet = TypeResolver.tryRecreateType(valueToSet, parameterList[0]);
		} catch (Exception e) {
			// setter methods have only one element in parameter list,
			// everything else is not setter method
			throw new NoSuchMethodException();
		}

		try {
			return method.invoke(object, new Object[] { valueToSet });
		} catch (Exception e) {
			throw new BiwafInjectionException(e);
		}
	}

	private Method findMethodOfName(Object object, String methodName) throws NoSuchMethodException {
		for (Method method : object.getClass().getMethods()) {
			if (method.getName().equals(methodName))
				return method;
		}
		throw new NoSuchMethodException();
	}

	private void injectOnField(Object object, String fieldName, Object valueToSet) {
		try {
			Field field = object.getClass().getDeclaredField(fieldName);
			field.setAccessible(true);
			field.set(object, valueToSet);
		} catch (Exception e) {
			throw new BiwafInjectionException(e);
		}
	}

}
