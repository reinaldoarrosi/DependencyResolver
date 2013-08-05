package com.reinaldoarrosi.dependencyresolver;

import java.util.ArrayList;
import java.util.List;

public abstract class DependencyModule {
	final ArrayList<Bind<?,?>> registry = new ArrayList<DependencyModule.Bind<?,?>>();
	
	public <TSrc, TDest extends TSrc> Bind<TSrc, TDest> bind(Class<TSrc> source, Class<TDest> destination) {
		Bind<TSrc, TDest> bind = new Bind<TSrc, TDest>(source, destination);
		registry.add(bind);
		
		return bind;
	}
	
	public abstract void load();
	
	public static class Bind<TSrc, TDest extends TSrc> {
		private Class<TSrc> source; 
		private Class<TDest> destination;
		private String name;
		private ArrayList<ConstructorArgument> constructorParameters;
		
		private Bind(Class<TSrc> source, Class<TDest> destination) {
			this.source = source;
			this.destination = destination;
			this.name = null;
			this.constructorParameters = new ArrayList<ConstructorArgument>();
		}
		
		public Bind<TSrc, TDest> named(String name) {
			this.name = name;
			return this;
		}
		
		public Bind<TSrc, TDest> withConstructorArguments(Object... argumentValues) {
			for (int i = 0; i < argumentValues.length; i++)
				this.constructorParameters.add(new ConstructorArgument(argumentValues[i]));
			
			return this;
		}

		public Class<TSrc> getSource() {
			return source;
		}

		public Class<TDest> getDestination() {
			return destination;
		}

		public String getName() {
			return name;
		}
		
		public List<ConstructorArgument> getConstructorParameters() {
			return constructorParameters;
		}
		
		public String getKey() {
			String key = source.getName();
			
			if(this.name != null && this.name.length() > 0)
				key += "!" + this.name;
			
			return key;
		}
	}
}
