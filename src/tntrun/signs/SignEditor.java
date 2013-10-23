package tntrun.signs;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Hashtable;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import tntrun.TNTRun;

public class SignEditor {
	
	protected TNTRun plugin;
	protected YamlConfiguration file;
	protected Hashtable<String, HashSet<Block>> signs = new Hashtable<String, HashSet<Block>>();
	
	public SignEditor(TNTRun plugin) {
		this.plugin = plugin;
	}
	
	public void onEnable() {
		file = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder().getAbsolutePath() + "/signs.yml"));

		for(String arena : file.getKeys(false)) {
			ConfigurationSection section = file.getConfigurationSection(arena);
			for(String block : section.getKeys(false)) {
				ConfigurationSection blockSection = section.getConfigurationSection(block);
				Location l = new Location(Bukkit.getWorld(blockSection.getString("world")), (double)blockSection.getInt("x"), (double)blockSection.getInt("y"), (double)blockSection.getInt("z"));
				Block sign = l.getBlock();
				addSign(sign, arena);
			}
			modifySigns(arena, SignMode.ENABLED, 0, plugin.pdata.getArenaByName(arena).getMaxPlayers());
		}
	}
	
	public void onDisable() {
		for(String arena : signs.keySet()) {
			ConfigurationSection section = file.createSection(arena);
			int i = 0;
			for(Block b : signs.get(arena)) {
				ConfigurationSection blockSection = section.createSection(Integer.toString(i++));
				blockSection.set("x", b.getX());
				blockSection.set("y", b.getY());
				blockSection.set("z", b.getZ());
				blockSection.set("world", b.getWorld().getName());
			}
		}
		
		try {
			file.save(new File(plugin.getDataFolder().getAbsolutePath() + "/signs.yml"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public SignEditor addArena(String arena) {
		if(!signs.containsKey(arena)) {
			signs.put(arena, new HashSet<Block>());
		}
		return this;
	}
	
	public SignEditor addSign(Block sign, String arena) {
		addArena(arena).signs.get(arena).add(sign);
		return this;
	}
	
	public SignEditor removeSign(Block sign, String arena) {
		addArena(arena).signs.get(arena).remove(sign);
		return this;
	}
	
	protected HashSet<Block> getSigns(String arena) {
		return addArena(arena).signs.get(arena);
	}
	
	public SignEditor modifySigns(String arena, int players, int maxPlayers) {
		return modifySigns(arena, SignMode.ENABLED, players, maxPlayers);
	}
	
	public SignEditor modifySigns(String arena, SignMode mode) {
		return modifySigns(arena, mode, 0, 100);
	}
	
	public SignEditor modifySigns(String arena, SignMode mode, int players, int maxPlayers) {
		String text;
		if(mode == SignMode.GAME_IN_PROGRESS) {
			text = ChatColor.RED.toString() + ChatColor.BOLD.toString() + "Game in progress";
		} else if(mode == SignMode.DISABLED) {
			text = ChatColor.RED.toString() + ChatColor.BOLD.toString() + "Disabled";
		} else if(players == maxPlayers) {
			text = ChatColor.RED.toString() + ChatColor.BOLD.toString() + Integer.toString(players) + "/" + Integer.toString(maxPlayers);
		} else {
			text = ChatColor.GREEN.toString() + ChatColor.BOLD.toString() + Integer.toString(players) + "/" + Integer.toString(maxPlayers);
		}
		
		for(Block b : getSigns(arena)) {
			Sign sign = (Sign) b.getState();
			sign.setLine(3, text);
			sign.update();
			
			org.bukkit.material.Sign s = (org.bukkit.material.Sign) sign.getData();
			Block lamp = b.getRelative(s.getAttachedFace()).getRelative(s.getAttachedFace()).getLocation().add(0, 1, 0).getBlock();
			if(mode == SignMode.DISABLED || mode == SignMode.GAME_IN_PROGRESS || players == maxPlayers) {
				lamp.setType(Material.REDSTONE_BLOCK);
			} else {
				lamp.setType(Material.STONE);
			}
		}
		return this;
	}
}