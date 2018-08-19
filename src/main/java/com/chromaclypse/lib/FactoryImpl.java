package com.chromaclypse.lib;

import java.util.HashMap;
import java.util.Map;

import com.chromaclypse.api.Factory;

public final class FactoryImpl implements Factory {
	private final Map<Class<?>, Class<?>> classMap = new HashMap<>();
	private final Map<Class<?>, Object> instanceMap = new HashMap<>();
	
	@Override
	public <T> T construct(Class<T> clazz) {
		try {
			@SuppressWarnings("unchecked")
			T inst = (T) classMap.get(clazz).getDeclaredConstructor().newInstance();
			return inst;
		} catch (Exception e) {
			return null;
		}
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public <T> T instance(Class<T> clazz) {
		return (T) instanceMap.get(clazz);
	}
	
	public <T> void register(Class<T> abstractClass, Class<? extends T> concreteClass) {
		classMap.put(abstractClass, concreteClass);
	}

	public <T> void register(Class<T> abstractClass, T instance) {
		instanceMap.put(abstractClass, instance);
	}
}
