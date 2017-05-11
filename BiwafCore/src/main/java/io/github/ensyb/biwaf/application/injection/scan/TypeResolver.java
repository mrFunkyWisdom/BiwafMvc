package io.github.ensyb.biwaf.application.injection.scan;

import java.util.ArrayList;
import java.util.List;

public class TypeResolver {

	private static List<ParseValueType> listOfUnboxedAndBoxedTypes = new ArrayList<>();

	static {
		listOfUnboxedAndBoxedTypes.add(
				new ParseValueType((Object value) -> Byte.parseByte(String.valueOf(value)), byte.class, Byte.class));
		listOfUnboxedAndBoxedTypes.add(new ParseValueType((Object value) -> Integer.parseInt(String.valueOf(value)),
				int.class, Integer.class));
		listOfUnboxedAndBoxedTypes.add(
				new ParseValueType((Object value) -> Long.parseLong(String.valueOf(value)), long.class, Long.class));
		listOfUnboxedAndBoxedTypes.add(new ParseValueType((Object value) -> Float.parseFloat(String.valueOf(value)),
				float.class, Float.class));
		listOfUnboxedAndBoxedTypes.add(new ParseValueType((Object value) -> Double.parseDouble(String.valueOf(value)),
				double.class, Double.class));
		listOfUnboxedAndBoxedTypes.add(new ParseValueType((Object value) -> Boolean.parseBoolean(String.valueOf(value)),
				boolean.class, Boolean.class));
		listOfUnboxedAndBoxedTypes
				.add(new ParseValueType((Object value) -> String.valueOf(value), String.class, String.class));
	}

	@SuppressWarnings("unchecked")
	public static <T> T tryRecreateType(Object value, Class<T> type) {
		Object valueReturn = value;

		if (ifNotNeadRecreate(value, type))
			return (T) valueReturn;

		for (ParseValueType recreate : listOfUnboxedAndBoxedTypes) {
			if (equalityCheck(type, recreate.getFirstNode(), recreate.getSecondNode())) {
				valueReturn = (T) recreate.getStrategy().parse(value);
			}
		}

		return (T) valueReturn;
	}

	private static boolean ifNotNeadRecreate(Object value, Class<?> type) {
		return (type == null || type.isAssignableFrom(value.getClass())) ? true : false;
	}

	private static boolean equalityCheck(Object test, Object primitiveClassObject, Object boxedClassObject) {
		if (test != null)
			return (test.equals(primitiveClassObject) || test.equals(boxedClassObject)) ? true : false;
		return false;
	}
}
