package io.github.ensyb.biwaf.application.injection;

import java.util.List;

import io.github.ensyb.biwaf.application.injection.meta.Injectable;

/** data class */
final class BeanRepresenation {
	public final String ClassName;
	public final Injectable.Scope Scope;
	public final String Id;
	public final List<PropertyRepresentation> Properties;
	public final Object BeanInstance;
	
	public static final class PropertyRepresentation {
		public final String NameOfProperty;
		public final String ValueOfProperty;
		public final String BeanReference;

		public PropertyRepresentation(String propertyName, String beanReference, String propertyValue) {
			this.NameOfProperty = propertyName;
			this.BeanReference = beanReference;
			this.ValueOfProperty = propertyValue;
		}
	}

	private BeanRepresenation(Construct construct) {
		this.ClassName = construct.className;
		this.Scope = construct.scope;
		this.Id = construct.id;
		this.BeanInstance = construct.beanInstance;
		this.Properties = construct.properties;
	}
	
	private BeanRepresenation(String className, Injectable.Scope scope, String id,
			List<PropertyRepresentation> properties, Object beanInstance) {
		this.ClassName = className;
		this.Scope = scope;
		this.Id = id;
		this.Properties = properties;
		this.BeanInstance = beanInstance;
	}
	
	public final static class Construct{
		private String className;
		private Injectable.Scope scope;
		private String id;
		private Object beanInstance;
		private List<PropertyRepresentation> properties;
		
		public Construct className(String className){
			this.className = className;
			return this;
		}
		
		public Construct scope(Injectable.Scope scope){
			this.scope = scope;
			return this;
		}
		
		public Construct id(String id){
			this.id = id;
			return this;
		}
		
		public Construct beanInstance(Object instance){
			this.beanInstance = instance;
			return this;
		}
		
		public Construct properies(List<PropertyRepresentation> properties){
			this.properties = properties;
			return this;
		}
		
		public BeanRepresenation build(){
			return new BeanRepresenation(this);
		}
		
	}
	
	public BeanRepresenation changeBeanInstance(Object beanInstance){
		return new BeanRepresenation(this.ClassName,this.Scope, this.Id, this.Properties, beanInstance);
	}


}
