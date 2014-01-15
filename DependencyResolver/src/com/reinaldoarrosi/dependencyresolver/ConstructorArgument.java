package com.reinaldoarrosi.dependencyresolver;

public class ConstructorArgument {
	private Object value;
	private Class<?> type;
	
	public ConstructorArgument(Object value, Class<?> type) {
		this.value = value;
		this.type = (type != null ? type : Object.class);
	}
	
	public ConstructorArgument(Object value) {
		this.value = value;
		this.type = (value != null ? value.getClass() : Object.class);
	}

	public Object getValue() {
		return value;
	}

	public Class<?> getType() {
		return type;
	}
	
}
