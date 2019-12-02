package com.chromaclypse.lib;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import com.chromaclypse.api.Factory;
import com.chromaclypse.api.reflect.Inject;
import com.chromaclypse.api.reflect.Singleton;

public final class FactoryImpl implements Factory {
	private final Map<Class<?>, Supplier<?>> constructionMap = new HashMap<>();
	
	@Override
	public <T> T construct(Class<T> clazz) {
		return newInstance(clazz);
	}
	
	@Override
	@Deprecated
	public <T> T instance(Class<T> clazz) {
		return construct(clazz);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public <T> void register(Class<T> abstractClass, Class<? extends T> implementation) {
		Supplier<? extends T> supplier = () -> construct(implementation);

		registerSupplier(abstractClass, supplier);
		registerSupplier((Class<T>)implementation, supplier);
	}
	
	@Override
	public <T> void registerSingleton(Class<T> abstractClass, Supplier<? extends T> func) {
		constructionMap.put(abstractClass, () -> {
			T val = func.get();
			constructionMap.put(abstractClass, () -> val);
			
			return val;
		});
	}
	
	@Override
	public <T> void registerSupplier(Class<T> clazz, Supplier<? extends T> func) {
		constructionMap.put(clazz, func);
	}
	
	private static Constructor<?> findConstructor(Class<?> clazz) {
		Constructor<?> found = null;
		
		for(Constructor<?> ctor : clazz.getDeclaredConstructors()) {
			if(ctor.getAnnotation(Inject.class) != null) {
				return ctor;
			}
			
			if(ctor.getParameterCount() == 0) {
				found = ctor;
			}
		}
		
		if(found != null) {
			return found;
		}
		
		throw new IllegalArgumentException("No injectable constructor found on class");
	}

	@SuppressWarnings("unchecked")
	private <T> void registerSingletons(Class<?> clazz, T object) {
		Supplier<T> supplier = () -> object;
		
		if(clazz.getAnnotation(Singleton.class) != null) {
			registerSupplier((Class<T>)clazz, supplier);
		}
		
		for(Class<?> iface : clazz.getInterfaces()) {
			if(iface.getAnnotation(Singleton.class) != null) {
				registerSupplier((Class<T>)iface, supplier);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private <T> T buildObject(Constructor<?> ctor, Object... parameters) {
		Class<?> clazz = ctor.getDeclaringClass();
		T result;
		try {
			result = (T)ctor.newInstance(parameters);
		} catch (Exception e) {
			throw new IllegalArgumentException("Could not invoke constructor", e);
		}
		
		registerSingletons(clazz, result);
		
		return result;
	}
	
	@SuppressWarnings("unchecked")
	private <T> T newInstance(Class<T> clazz) {
		Supplier<?> supplier = constructionMap.get(clazz);
		
		if(supplier != null) {
			return (T)supplier.get();
		}
		
		Constructor<?> ctor = findConstructor(clazz);
		Class<?>[] types = ctor.getParameterTypes();
		Object[] parameters = new Object[types.length];
		
		for(int i = 0; i < types.length; ++i) {
			parameters[i] = newInstance(types[i]);
		}
		
		return buildObject(ctor, parameters);
	}
}
