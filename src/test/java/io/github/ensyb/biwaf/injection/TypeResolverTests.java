package io.github.ensyb.biwaf.injection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

import io.github.ensyb.biwaf.application.injection.scan.TypeResolver;

public class TypeResolverTests {
	@Test
	public void testTryParseValueOfByte() {
		String value = "15";
		byte expectedValue = 15;
		byte recreatedValue = TypeResolver.tryRecreateType(value, Byte.class);
		assertEquals(expectedValue, recreatedValue);
	}

	@Test
	public void testTryParseValueOfInteger() {
		String value = "11122";
		int expectedValue = 11122;
		int recreatedValue = TypeResolver.tryRecreateType(value, Integer.class);
		assertEquals(expectedValue, recreatedValue);
	}

	@Test
	public void testTryParseValueOfLong() {
		String value = "15365";
		long expectedValue = 15365L;
		long recreatedValue = TypeResolver.tryRecreateType(value, Long.class);
		assertEquals(expectedValue, recreatedValue);
	}

	@Test
	public void testTryParseValueOfFloat() {
		String value = "15.12";
		float expectedValue = 15.12f;
		double tacnost = 0.001;
		float recteatedValue = TypeResolver.tryRecreateType(value, Float.class);
		assertEquals(expectedValue, recteatedValue, tacnost);
	}

	@Test
	public void testTryParseValueOfDouble() {
		String value = "15.12";
		double expectedValue = 15.12;
		double tacnost = 0.001;
		double recteatedValue = TypeResolver.tryRecreateType(value, double.class);
		assertEquals(expectedValue, recteatedValue, tacnost);
	}

	@Test
	public void testTryParseValueOfBoolean() {
		String value = "false";
		boolean recteatedValue = TypeResolver.tryRecreateType(value, boolean.class);
		assertFalse(recteatedValue);
	}

	@Test
	public void testTryParseValueOfString() {
		String value = "String";
		String recreatedValue = TypeResolver.tryRecreateType(value, String.class);
		assertEquals(value, recreatedValue);
	}

}
