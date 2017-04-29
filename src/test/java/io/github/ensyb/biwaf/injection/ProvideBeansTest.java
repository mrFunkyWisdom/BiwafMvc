package io.github.ensyb.biwaf.injection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import io.github.ensyb.biwaf.application.injection.LoadBindings;
import io.github.ensyb.biwaf.application.injection.ProvideBeans;
import io.github.ensyb.biwaf.injection.resources.TestBean;
import io.github.ensyb.biwaf.injection.resources.ToInjectTestBean;

public class ProvideBeansTest {

	private static final String BASE_PACKAGE = "io.github.ensyb.biwaf.injection";

	private ProvideBeans provider;

	private final String PROPERTY_VALUE_FROM_INJECTED = "this is from to inject";

	@Before
	public void setup() {
		LoadBindings beans = new LoadBindings.AnnotationBinded();
		provider = new ProvideBeans.StandardProvider(beans.load(BASE_PACKAGE));
	}

	@Test
	public void testIfFileldHaveInjectedValue() {
		ToInjectTestBean beanTest = (ToInjectTestBean) provider.getBean("forInjection");
		assertEquals(PROPERTY_VALUE_FROM_INJECTED, beanTest.getToInjectProperty());
	}

	@Test
	public void testIfInitMethodOfBeanIsRunned() {
		TestBean testBean = (TestBean) provider.getBean("test");
		assertEquals(TestBean.BEAN_INITIALIZED_MESSAGE, testBean.getTestMessage());
	}

	@Test
	public void testTwoBeansWithSingletonScopeHaveSameReference() {
		ToInjectTestBean beanTest = (ToInjectTestBean) provider.getBean("forInjection");
		ToInjectTestBean beanTest2 = (ToInjectTestBean) provider.getBean("forInjection");

		String firstBeanHex = Integer.toHexString(System.identityHashCode(beanTest));
		String secondBeanHex = Integer.toHexString(System.identityHashCode(beanTest2));

		assertEquals(firstBeanHex, secondBeanHex);
	}

	@Test 
	public void testFieldInjectionWithoutSetter(){
		TestBean bean = provider.getBean("test", TestBean.class);
		assertFalse(bean.getValueFromInjectionField().isEmpty());
	}
	
	@Test
	public void testTwoBeansWithPrototypeScopeDoesNotHaveSameReference() {
		TestBean beanTest = (TestBean) provider.getBean("test");
		TestBean beanTest2 = (TestBean) provider.getBean("test");

		String firstBeanHex = Integer.toHexString(System.identityHashCode(beanTest));
		String secondBeanHex = Integer.toHexString(System.identityHashCode(beanTest2));

		assertNotEquals(firstBeanHex, secondBeanHex);
	}

	@Test
	public void testBeanHaveInjectedOtherBean() {
		ToInjectTestBean beanTest = (ToInjectTestBean) provider.getBean("forInjection");
		TestBean testBean = (TestBean) provider.getBean("test");

		assertTrue(equalBean(beanTest, testBean.getTestToInject()));
	}

	private boolean equalBean(ToInjectTestBean bean1, ToInjectTestBean bean2) {
		return (bean1.getToInjectProperty().equals(bean2.getToInjectProperty()));
	}

	@Test
	public void testGetBeanWithClassProvided() {
		ToInjectTestBean beanTest = provider.getBean("forInjection", ToInjectTestBean.class);
		TestBean testBean = provider.getBean("test", TestBean.class);

		assertTrue(equalBean(beanTest, testBean.getTestToInject()));
	}
}

