package com.chromaclypse.lib;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Color;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import com.chromaclypse.api.Defaults;
import com.chromaclypse.api.config.EmptySection;

public class EmptySectionImpl implements EmptySection {

	@Override
	public Set<String> getKeys(boolean deep) {
		return Defaults.EmptySet();
	}

	@Override
	public Map<String, Object> getValues(boolean deep) {
		return Defaults.EmptyMap();
	}

	@Override
	public boolean contains(String path) {
		return false;
	}

	@Override
	public boolean contains(String path, boolean ignoreDefault) {
		return false;
	}

	@Override
	public boolean isSet(String path) {
		return false;
	}

	@Override
	public String getCurrentPath() {
		return "";
	}

	@Override
	public String getName() {
		return "";
	}

	@Override
	public Configuration getRoot() {
		return null;
	}

	@Override
	public ConfigurationSection getParent() {
		return EmptySection.get();
	}

	@Override
	public Object get(String path) {
		return null;
	}

	@Override
	public Object get(String path, Object def) {
		return null;
	}

	@Override
	public void set(String path, Object value) {
	}

	@Override
	public ConfigurationSection createSection(String path) {
		return EmptySection.get();
	}

	@Override
	public ConfigurationSection createSection(String path, Map<?, ?> map) {
		return EmptySection.get();
	}

	@Override
	public String getString(String path) {
		return "";
	}

	@Override
	public String getString(String path, String def) {
		return "";
	}

	@Override
	public boolean isString(String path) {
		return false;
	}

	@Override
	public int getInt(String path) {
		return 0;
	}

	@Override
	public int getInt(String path, int def) {
		return 0;
	}

	@Override
	public boolean isInt(String path) {
		return false;
	}

	@Override
	public boolean getBoolean(String path) {
		return false;
	}

	@Override
	public boolean getBoolean(String path, boolean def) {
		return false;
	}

	@Override
	public boolean isBoolean(String path) {
		return false;
	}

	@Override
	public double getDouble(String path) {
		return 0;
	}

	@Override
	public double getDouble(String path, double def) {
		return 0;
	}

	@Override
	public boolean isDouble(String path) {
		return false;
	}

	@Override
	public long getLong(String path) {
		return 0;
	}

	@Override
	public long getLong(String path, long def) {
		return 0;
	}

	@Override
	public boolean isLong(String path) {
		return false;
	}

	@Override
	public List<?> getList(String path) {
		return Defaults.EmptyList();
	}

	@Override
	public List<?> getList(String path, List<?> def) {
		return Defaults.EmptyList();
	}

	@Override
	public boolean isList(String path) {
		return false;
	}

	@Override
	public List<String> getStringList(String path) {
		return Defaults.EmptyList();
	}

	@Override
	public List<Integer> getIntegerList(String path) {
		return Defaults.EmptyList();
	}

	@Override
	public List<Boolean> getBooleanList(String path) {
		return Defaults.EmptyList();
	}

	@Override
	public List<Double> getDoubleList(String path) {
		return Defaults.EmptyList();
	}

	@Override
	public List<Float> getFloatList(String path) {
		return Defaults.EmptyList();
	}

	@Override
	public List<Long> getLongList(String path) {
		return Defaults.EmptyList();
	}

	@Override
	public List<Byte> getByteList(String path) {
		return Defaults.EmptyList();
	}

	@Override
	public List<Character> getCharacterList(String path) {
		return Defaults.EmptyList();
	}

	@Override
	public List<Short> getShortList(String path) {
		return Defaults.EmptyList();
	}

	@Override
	public List<Map<?, ?>> getMapList(String path) {
		return Defaults.EmptyList();
	}

	@Override
	public Vector getVector(String path) {
		return null;
	}

	@Override
	public Vector getVector(String path, Vector def) {
		return null;
	}

	@Override
	public boolean isVector(String path) {
		return false;
	}

	@Override
	public OfflinePlayer getOfflinePlayer(String path) {
		return null;
	}

	@Override
	public OfflinePlayer getOfflinePlayer(String path, OfflinePlayer def) {
		return null;
	}

	@Override
	public boolean isOfflinePlayer(String path) {
		return false;
	}

	@Override
	public ItemStack getItemStack(String path) {
		return null;
	}

	@Override
	public ItemStack getItemStack(String path, ItemStack def) {
		return null;
	}

	@Override
	public boolean isItemStack(String path) {
		return false;
	}

	@Override
	public Color getColor(String path) {
		return null;
	}

	@Override
	public Color getColor(String path, Color def) {
		return null;
	}

	@Override
	public boolean isColor(String path) {
		return false;
	}

	@Override
	public ConfigurationSection getConfigurationSection(String path) {
		return EmptySection.get();
	}

	@Override
	public boolean isConfigurationSection(String path) {
		return false;
	}

	@Override
	public ConfigurationSection getDefaultSection() {
		return EmptySection.get();
	}

	@Override
	public void addDefault(String path, Object value) {
	}

	@Override
	public <T extends ConfigurationSerializable> T getSerializable(String arg0, Class<T> arg1) {
		return null;
	}

	@Override
	public <T extends ConfigurationSerializable> T getSerializable(String arg0, Class<T> arg1, T arg2) {
		return null;
	}
}
