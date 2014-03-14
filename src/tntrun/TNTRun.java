/**
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
 */

package tntrun;

import java.io.File;
import java.util.logging.Logger;

import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import tntrun.arena.Arena;
import tntrun.bars.Bars;
import tntrun.commands.ConsoleCommands;
import tntrun.commands.GameCommands;
import tntrun.commands.SetupCommands;
import tntrun.datahandler.ArenasManager;
import tntrun.datahandler.PlayerDataStore;
import tntrun.eventhandler.PlayerLeaveArenaChecker;
import tntrun.eventhandler.PlayerStatusHandler;
import tntrun.eventhandler.RestrictionHandler;
import tntrun.eventhandler.WorldUnloadHandler;
import tntrun.lobby.GlobalLobby;
import tntrun.messages.Messages;
import tntrun.signs.SignHandler;
import tntrun.signs.editor.SignEditor;

public class TNTRun extends JavaPlugin {

	private Logger log;

	public PlayerDataStore pdata;
	public ArenasManager amanager;
	public SetupCommands scommands;
	public GameCommands gcommands;
	public ConsoleCommands ccommands;
	public PlayerStatusHandler pshandler;
	public RestrictionHandler rhandler;
	public PlayerLeaveArenaChecker plachecker;
	public WorldUnloadHandler wuhandler;
	public SignHandler signs;
	public GlobalLobby globallobby;
	public SignEditor signEditor;

	@Override
	public void onEnable() {
		log = getLogger();
		signEditor = new SignEditor(this);
		globallobby = new GlobalLobby(this);
		Messages.loadMessages(this);
		Bars.loadBars(this);
		pdata = new PlayerDataStore();
		amanager = new ArenasManager();
		scommands = new SetupCommands(this);
		getCommand("tntrunsetup").setExecutor(scommands);
		gcommands = new GameCommands(this);
		getCommand("tntrun").setExecutor(gcommands);
		ccommands = new ConsoleCommands(this);
		getCommand("tntrunconsole").setExecutor(ccommands);
		pshandler = new PlayerStatusHandler(this);
		getServer().getPluginManager().registerEvents(pshandler, this);
		rhandler = new RestrictionHandler(this);
		getServer().getPluginManager().registerEvents(rhandler, this);
		plachecker = new PlayerLeaveArenaChecker(this);
		getServer().getPluginManager().registerEvents(plachecker, this);
		wuhandler = new WorldUnloadHandler(this);
		getServer().getPluginManager().registerEvents(wuhandler, this);
		signs = new SignHandler(this);
		getServer().getPluginManager().registerEvents(signs, this);
		// load arenas
		final File arenasfolder = new File(this.getDataFolder() + File.separator + "arenas");
		arenasfolder.mkdirs();
		final TNTRun instance = this;
		this.getServer().getScheduler().scheduleSyncDelayedTask(
			this,
			new Runnable() {
				@Override
				public void run() {
					// load globallobyy
					globallobby.loadFromConfig();
					// load arenas
					for (String file : arenasfolder.list()) {
						Arena arena = new Arena(file.substring(0, file.length() - 4), instance);
						arena.getStructureManager().loadFromConfig();
						arena.getStatusManager().enableArena();
						amanager.registerArena(arena);
					}
					// load signs
					signEditor.loadConfiguration();
				}
			},
			20
		);
	}

	@Override
	public void onDisable() {
		// save arenas
		for (Arena arena : amanager.getArenas()) {
			arena.getStatusManager().disableArena();
			arena.getStructureManager().saveToConfig();
		}
		// save lobby
		globallobby.saveToConfig();
		globallobby = null;
		// save signs
		signEditor.saveConfiguration();
		signEditor = null;
		// unload other things
		HandlerList.unregisterAll(this);
		scommands = null;
		gcommands = null;
		pshandler = null;
		rhandler = null;
		plachecker = null;
		signs = null;
		pdata = null;
		amanager = null;
		log = null;
	}

	public void logSevere(String message) {
		log.severe(message);
	}

}
