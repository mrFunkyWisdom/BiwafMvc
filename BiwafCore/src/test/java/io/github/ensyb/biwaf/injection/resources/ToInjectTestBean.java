package io.github.ensyb.biwaf.injection.resources;

import io.github.ensyb.biwaf.application.injection.meta.Injectable;
import io.github.ensyb.biwaf.application.injection.meta.Inject;
import io.github.ensyb.biwaf.application.injection.meta.Injectable.Scope;

@Injectable(scope = Scope.SINGLETON, id = "forInjection")
public class ToInjectTestBean {

	@Inject(value = "this is from to inject")
	private String toInjectProperty;

	public String getToInjectProperty() {
		return toInjectProperty;
	}

	public void setToInjectProperty(String toInjectProperty) {
		this.toInjectProperty = toInjectProperty;
	}
}
