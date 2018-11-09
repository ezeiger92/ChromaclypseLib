package com.chromaclypse.lib;

import java.util.function.Consumer;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.plugin.java.JavaPlugin;

import com.chromaclypse.api.Chroma;
import com.chromaclypse.api.Reflect;
import com.chromaclypse.api.config.Walker;
import com.chromaclypse.api.menu.Menu;
import com.chromaclypse.api.messages.Formatter;
import com.chromaclypse.api.messages.Messager;
import com.chromaclypse.lib.bukkit.ChromaBukkit;
import com.chromaclypse.lib.bukkit.FormatterBukkit;

public class ChromaLib extends JavaPlugin implements Listener {

	static {
		ChromaBukkit chroma = new ChromaBukkit();
		
		chroma.factory().register(Logger.class, Bukkit.getLogger());
		chroma.factory().register(Walker.class, WalkerImpl.class);
		chroma.factory().register(Formatter.class, FormatterBukkit.class);
	}
	
	public ChromaLib() {
		((ChromaBukkit) Chroma.get()).setPlugin(this);
	}
	
	@Override
	public void onLoad() {
	}
	
	@Override
	public void onEnable() {
		getServer().getPluginManager().registerEvents(this, this);
		
		try {
			Reflect.serverAddChannel(this, Messager.BOOK_CHANNEL);
		}
		catch(Exception e) {
		}
	}
	
	@Override
	public void onDisable() {
		try {
			Reflect.serverRemoveChannel(this, Messager.BOOK_CHANNEL);
		}
		catch(Exception e) {
		}
	}
	
	@EventHandler(ignoreCancelled=true)
	public void onInventoryClick(InventoryClickEvent event) {
		InventoryHolder holder = event.getInventory().getHolder();
			
		if(holder instanceof Menu) {
			if(event.getRawSlot() < event.getInventory().getSize()) {
				Consumer<InventoryClickEvent> consumer = ((Menu)holder).getHandler(event.getSlot());
					
				if(consumer != null)
					try {
						consumer.accept(event);
					}
					catch(Throwable t) {
						t.printStackTrace();
					}
					
				event.setCancelled(true);
			}
			else if(event.isShiftClick()) {
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler(ignoreCancelled=true)
	public void onInventoryDrag(InventoryDragEvent event) {
		if(event.getInventory().getHolder() instanceof Menu) {
			int lastChestSlot = event.getInventory().getSize();
			
			for(int slot : event.getNewItems().keySet())
				if(slot < lastChestSlot) {
					event.setCancelled(true);
					break;
				}
		}
	}
}
