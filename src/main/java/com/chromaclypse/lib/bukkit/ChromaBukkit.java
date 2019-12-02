package com.chromaclypse.lib.bukkit;

import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import com.chromaclypse.api.Chroma;
import com.chromaclypse.lib.FactoryImpl;

@SuppressWarnings("deprecation")
public class ChromaBukkit extends Chroma.Impl {
	private FactoryImpl factory = new FactoryImpl();
	private Plugin handle;
	
	public void setPlugin(Plugin plugin) {
		handle = plugin;
	}
	
	@Override
	public FactoryImpl factory() {
		return factory;
	}

	@Override
	public Plugin plugin() {
		return handle;
	}

	@Override
	public Logger log() {
		return Bukkit.getLogger();
	}
}
