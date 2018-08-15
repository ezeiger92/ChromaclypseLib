package com.chromaclypse.lib;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import com.chromaclypse.api.Defaults;
import com.chromaclypse.api.config.ConfigAction;
import com.chromaclypse.api.config.ConfigObject;
import com.chromaclypse.api.config.Walker;
import com.chromaclypse.api.config.visitor.ConfigVisitor;

public class WalkerImpl implements Walker {
	
	@Override
	@SuppressWarnings("unchecked")
	public <T extends ConfigObject> Map<String, Object> serialize(T config) {
		return (Map<String, Object>) mapFields(config);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public <T extends ConfigObject> T deserialize(Class<T> clazz, Map<String, Object> serialData) {
		try {
			return (T) unmapFields(clazz.newInstance(), serialData);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	private static boolean isEmptyList(Object o) {
		return List.class.isAssignableFrom(o.getClass()) && ((List<?>)o).isEmpty();
	}
	
	private static Class<?> parentConfigClass(Class<?> type) {
		while(type != null && !ConfigObject.class.isAssignableFrom(type))
			type = type.getEnclosingClass();
		return type;
	}
	
	@SuppressWarnings("unchecked")
	private static List<Object> unmapListHelper(Object data, Field f) throws InstantiationException, IllegalAccessException {
		List<Object> result = Defaults.emptyList();
		
		if(data == null)
			return result;
		
		ParameterizedType pt = (ParameterizedType) f.getGenericType();
		Type[] args = pt.getActualTypeArguments();
		for(int i = 0; i < args.length; ++i)
			if(args[i] instanceof ParameterizedType)
				args[i] = ((ParameterizedType)args[i]).getRawType();

		Class<?> elemClass = (Class<?>) args[0];
		Class<?> parentConfigElem = parentConfigClass(elemClass);
		
		for(Object leafData : (List<Object>) data) {
			if(parentConfigElem != null)
				leafData = unmapFields(elemClass.newInstance(), mapOf(leafData));
			result.add(leafData);
		}
		
		return result;
	}
	
	@SuppressWarnings("unchecked")
	private static Map<Object, Object> unmapMapHelper(Object data, Field f) throws InstantiationException, IllegalAccessException {
		Map<Object, Object> result = Defaults.emptyMap();

		if(data == null)
			return result;
		
		ParameterizedType pt = (ParameterizedType) f.getGenericType();
		Type[] args = pt.getActualTypeArguments();
		for(int i = 0; i < args.length; ++i)
			if(args[i] instanceof ParameterizedType)
				args[i] = ((ParameterizedType)args[i]).getRawType();

		Class<?> keyClass = (Class<?>) args[0];
		Class<?> valClass = (Class<?>) args[1];
		Class<?> parentConfigKey = parentConfigClass(keyClass);
		Class<?> parentConfigVal = parentConfigClass(valClass);
		
		for(Map.Entry<Object, Object> entry : ((Map<Object, Object>) data).entrySet()) {
			Object leafDataKey = entry.getKey();
			Object leafDataVal = entry.getValue();
			
			if(parentConfigKey != null)
				leafDataKey = unmapFields(keyClass.newInstance(), mapOf(leafDataKey));
			if(parentConfigVal != null)
				leafDataVal = unmapFields(valClass.newInstance(), mapOf(leafDataVal));
			result.put(leafDataKey, leafDataVal);
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	private static Map<String, Object> mapOf(Object in) {
		if(in instanceof ConfigurationSection) {
			return ((ConfigurationSection)in).getValues(false);
		}
		
		if(in == null)
			return Defaults.emptyMap();
		
		return (Map<String,Object>)in;
	}
	
	private static Object unmapFields(Object object, Map<?, ?> serialData) {
		try {
			for(Field f : object.getClass().getFields()) {
				Object subData = serialData.get(f.getName());
				
				if(List.class.isAssignableFrom(f.getType()))
					subData = unmapListHelper(subData, f);
				else if(Map.class.isAssignableFrom(f.getType()))
					subData = unmapMapHelper(mapOf(subData), f);
				else if(parentConfigClass(f.getType()) != null)
					subData = unmapFields(f.getType().newInstance(), mapOf(subData));
				
				if(!f.getType().isPrimitive() || subData != null)
					f.set(object, subData);
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		return object;
	}
	
	@SuppressWarnings("unchecked")
	private static Object mapFields(Object object) {
		if(object instanceof List) {
			List<Object> result = Defaults.emptyList();
			for(Object entry : (List<Object>) object)
				result.add( mapFields(entry) );
			
			return result;
		}
		else if(object instanceof Map) {
			Map<Object, Object> result = Defaults.emptyMap();
			for(Map.Entry<Object, Object> entry : ((Map<Object, Object>) object).entrySet())
				result.put( mapFields(entry.getKey()), mapFields(entry.getValue()) );
			
			return result;
		}
		else if(parentConfigClass(object.getClass()) == null)
			return object;
		
		Map<Object, Object> result = Defaults.emptyMap();
		
		for(Field f : object.getClass().getFields())
			try {
				result.put(f.getName(), mapFields(f.get(object)) );
			} catch (Exception e) {}
		
		return result;
	}
	
	private static class Holder {
		public Map<String, Object> map;
		public ConfigurationSection file;
		public int depth;
		
		public Holder(Map<String, Object> map, ConfigurationSection file, int depth) {
			this.map = map;
			this.file = (file != null) ? file : new YamlConfiguration();
			this.depth = depth;
		}
	}
	
	private Object getFromCS(ConfigurationSection config, String key) {
		Object value = config.get(key);
		if(value instanceof ConfigurationSection)
			value = ((ConfigurationSection)value).getValues(true);
		return value;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public void walk(ConfigurationSection configFile, Object configInst, ConfigVisitor visitor) {
		Map<String, Object> dataInst = (Map<String,Object>)mapFields(configInst);
		List<Holder> toParse = Defaults.list(new Holder(dataInst, configFile, 0));
		boolean update = false;
		
		for(int i = 0; i < toParse.size(); ++i) {
			Holder holder = toParse.get(i);
			Map<String, Object> fromInst = holder.map;
			ConfigurationSection fromFile = holder.file;
			Set<String> keysFile = fromFile.getKeys(false);
			
			for(Map.Entry<String, Object> entry : fromInst.entrySet()) {
				String key = entry.getKey();
				keysFile.remove(key);
				Object valueInst = entry.getValue();
				Object valueFile = getFromCS(fromFile, key);
				Class<?> classInst = valueInst.getClass();
				
				if(Map.class.isAssignableFrom(classInst)) {
					visitor.branch(key, valueInst, valueFile, holder.depth);
					ConfigurationSection next = fromFile.getConfigurationSection(key);
					if(next == null && visitor.getAction().modifiesFile())
						next = fromFile.createSection(key);
					
					toParse.add(new Holder((Map<String,Object>)valueInst, next, holder.depth + 1));
				}
				else {
					visitor.leaf(key, valueInst, valueFile, holder.depth);
					ConfigAction action = visitor.getAction();
					if((action == ConfigAction.MERGE && (valueFile == null || isEmptyList(valueFile))) || action == ConfigAction.SAVE)
						fromFile.set(key, valueInst);
					
					if(action.modifiesInstance() && valueFile != null) {
						entry.setValue(valueFile);
						update = true;
					}
				}
			}
			
			// Write missing info
			for(String keyw : keysFile) {
				Object valueFile = getFromCS(fromFile, keyw);
				visitor.leaf(keyw, null, valueFile, holder.depth);
				if(visitor.getAction().modifiesInstance()) {
					fromInst.put(keyw, valueFile);
					update = true;
				}
			}
		}
		
		if(update)
			unmapFields(configInst, dataInst);
	}
}
