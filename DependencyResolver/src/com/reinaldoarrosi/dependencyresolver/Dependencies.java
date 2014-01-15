package com.reinaldoarrosi.dependencyresolver;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.reinaldoarrosi.dependencyresolver.DependencyModule.Bind;

public class Dependencies {
	private static final HashMap<String, Class<?>> registry = new HashMap<String, Class<?>>();
	private static final HashMap<String, List<ConstructorArgument>> constructorsArguments = new HashMap<String, List<ConstructorArgument>>();
	
	public static void addModule(DependencyModule module) {
		module.load();
		
		for (Bind<?, ?> record : module.registry) {
			registry.put(record.getKey(), record.getDestination());
			constructorsArguments.put(record.getKey(), record.getConstructorParameters());
		}
	}
	
	public static <T> Criteria<T> forClass(Class<T> clazz) {
		return new Criteria<T>(clazz);
	}
	
	public static class Criteria<T> {
		private Class<T> source;
		private String name;
		private ArrayList<ConstructorArgument> myConstructorArguments;
		
		private Criteria(Class<T> clazz) {
			this.source = clazz;
			this.name = null;
			this.myConstructorArguments = new ArrayList<ConstructorArgument>();
		}
		
		public Criteria<T> named(String name) {
			this.name = name;
			return this;
		}
		
		public Criteria<T> withConstructorArguments(Object... argumentValues) {
			for (int i = 0; i < argumentValues.length; i++) {
				if(argumentValues[i] instanceof ConstructorArgument)
					this.myConstructorArguments.add((ConstructorArgument)argumentValues[i]);
				else
					this.myConstructorArguments.add(new ConstructorArgument(argumentValues[i]));
			}
			
			return this;
		}
		
		@SuppressWarnings("unchecked")
		public T resolve() {
			String key = getKey(source, name);
			Class<?> createClass = registry.get(key);
			
			if(createClass == null)
				return null;
			
			List<ConstructorArgument> arguments = constructorsArguments.get(key);
			
			try {
				if(myConstructorArguments.size() > 0) {
					return (T) createUsingArguments(createClass, myConstructorArguments);
				} else if (arguments.size() > 0){
					return (T) createUsingArguments(createClass, arguments);
				} else {
					return (T) createClass.newInstance();
				}
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		
		@SuppressWarnings("unchecked")
		private T createUsingArguments(Class<?> createClass, List<ConstructorArgument> arguments) {
			Class<?>[] argumentTypes = new Class<?>[arguments.size()];
			Object[] argumentValues = new Object[arguments.size()];
			
			for (int i = 0; i < arguments.size(); i++) {
				argumentTypes[i] = arguments.get(i).getType();
				argumentValues[i] = arguments.get(i).getValue();
			}
			
			try {
				Constructor<?> constructor = findBestMatchConstructor(createClass, argumentTypes);
				return (T) constructor.newInstance(argumentValues);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		
		private Constructor<?> findBestMatchConstructor(Class<?> createClass, Class<?>[] argumentTypes) {
			Constructor<?>[] constructors = createClass.getConstructors();
			
			for (int i = 0; i < constructors.length; i++) {
				boolean valid = true;
				Constructor<?> constructor = constructors[i];
				Class<?>[] types = constructor.getParameterTypes();
				
				if(types.length != argumentTypes.length)
					continue;
				
				for (int j = 0; j < types.length; j++) {
					if(types[j].equals(boolean.class)) {
						valid = valid && (argumentTypes[j].equals(boolean.class) || argumentTypes[j].equals(Boolean.class));
					} else if(types[j].equals(byte.class)) {
						valid = valid && (argumentTypes[j].equals(byte.class) || argumentTypes[j].equals(Byte.class));
					} else if(types[j].equals(short.class)) {
						valid = valid && (argumentTypes[j].equals(short.class) || argumentTypes[j].equals(Short.class));
					} else if(types[j].equals(int.class)) {
						valid = valid && (argumentTypes[j].equals(int.class) || argumentTypes[j].equals(Integer.class));
					} else if(types[j].equals(long.class)) {
						valid = valid && (argumentTypes[j].equals(long.class) || argumentTypes[j].equals(Long.class));
					} else if(types[j].equals(float.class)) {
						valid = valid && (argumentTypes[j].equals(float.class) || argumentTypes[j].equals(Float.class));
					} else if(types[j].equals(double.class)) {
						valid = valid && (argumentTypes[j].equals(double.class) || argumentTypes[j].equals(Double.class));
					} else if(types[j].equals(char.class)) {
						valid = valid && (argumentTypes[j].equals(char.class) || argumentTypes[j].equals(Character.class));
					} else {
						valid = valid && types[j].isAssignableFrom(argumentTypes[j]);
					}
					
					if(!valid)
						break;
				}
				
				if(valid)
					return constructor;
			}
			
			return null;
		}

		private String getKey(Class<T> clazz, String name) {
			String key = clazz.getName();
			
			if(name != null && name.length() > 0)
				key += "!" + name;
			
			return key;
		}
	}
}
