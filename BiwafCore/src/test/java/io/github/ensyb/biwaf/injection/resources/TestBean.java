package io.github.ensyb.biwaf.injection.resources;

import io.github.ensyb.biwaf.application.injection.meta.Injectable;
import io.github.ensyb.biwaf.application.injection.meta.InitBean;
import io.github.ensyb.biwaf.application.injection.meta.Inject;
import io.github.ensyb.biwaf.application.injection.meta.Injectable.Scope;
import io.github.ensyb.biwaf.injection.resources.ToInjectTestBean;

@Injectable(scope = Scope.PROTOTYPE, id = "test")
public class TestBean implements InitBean {

	public static final String BEAN_INITIALIZED_MESSAGE = "Bean initialized";

	private String testMessage;

	@Inject(value = "this is from test bean")
	private String testBeanProperty;

	@Inject(reference = "forInjection")
	private ToInjectTestBean testToInject;

	@Inject(value ="injection on field")
	private String testiInjectField;
	
	
	public String getValueFromInjectionField(){
		return this.testiInjectField;
	}
	
	public String getTestBeanProperty() {
		return testBeanProperty;
	}

	public void setTestBeanProperty(String testBeanProperty) {
		this.testBeanProperty = testBeanProperty;
	}

	public ToInjectTestBean getTestToInject() {
		return testToInject;
	}

	public void setTestToInject(ToInjectTestBean testToInject) {
		this.testToInject = testToInject;
	}

	@Override
	public void init() {
		this.testMessage = BEAN_INITIALIZED_MESSAGE;
	}

	public String getTestMessage() {
		return testMessage;
	}

}
