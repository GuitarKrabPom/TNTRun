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

package tntrun.commands;

import java.io.File;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import tntrun.TNTRun;
import tntrun.arena.Arena;
import tntrun.bars.Bars;
import tntrun.messages.Messages;
import tntrun.selectionget.PlayerCuboidSelection;
import tntrun.selectionget.PlayerSelection;

public class SetupCommands implements CommandExecutor {

	private TNTRun plugin;
	private PlayerSelection plselection = new PlayerSelection();

	public SetupCommands(TNTRun plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("Player is expected");
			return true;
		}
		Player player = (Player) sender;
		// check permissions
		if (!player.hasPermission("tntrun.setup")) {
			Messages.sendMessage(player, Messages.nopermission);
			return true;
		}
		// handle commands
		// locations
		if (args.length == 1 && args[0].equalsIgnoreCase("setp1")) {
			plselection.setSelectionPoint1(player);
			sender.sendMessage("p1 saved");
			return true;
		} else if (args.length == 1 && args[0].equalsIgnoreCase("setp2")) {
			plselection.setSelectionPoint2(player);
			sender.sendMessage("p2 saved");
			return true;
		} else if (args.length == 1 && args[0].equalsIgnoreCase("clearp")) {
			plselection.clearSelectionPoints(player);
			sender.sendMessage("points cleared");
			return true;
		}
		// set lobby
		else if (args.length == 2 && args[0].equalsIgnoreCase("setlobby")) {
			plugin.globallobby.setLobbyLocation(player.getLocation());
			sender.sendMessage("Lobby set");
			return true;
		}
		// create arena
		else if (args.length == 2 && args[1].equalsIgnoreCase("create")) {
			Arena arenac = plugin.pdata.getArenaByName(args[0]);
			if (arenac != null) {
				sender.sendMessage("Arena already exists");
				return true;
			}
			plugin.pdata.registerArena(new Arena(args[0], plugin));
			sender.sendMessage("Arena created");
			return true;
		}
		// delete arena
		else if (args.length == 2 && args[1].equalsIgnoreCase("delete")) {
			Arena arena = plugin.pdata.getArenaByName(args[0]);
			if (arena == null) {
				sender.sendMessage("Arena does not exist");
				return true;
			}
			if (arena.isArenaEnabled()) {
				sender.sendMessage("Disable arena first");
				return true;
			}
			new File(plugin.getDataFolder() + File.separator + "arenas" + File.separator + arena.getArenaName() + ".yml").delete();
			plugin.signEditor.removeArena(arena.getArenaName());
			plugin.pdata.unregisterArena(arena);
			sender.sendMessage("Arena deleted");
			return true;
		}
		// set arena bounds
		else if (args.length == 2 && args[1].equalsIgnoreCase("setarena")) {
			Arena arena = plugin.pdata.getArenaByName(args[0]);
			if (arena != null) {
				if (arena.isArenaEnabled()) {
					sender.sendMessage("Disable arena first");
					return true;
				}
				PlayerCuboidSelection selection = plselection.getPlayerSelection(player, false);
				if (selection != null) {
					arena.setArenaPoints(selection.getMinimumLocation(),
							selection.getMaximumLocation());
					sender.sendMessage("Arena bounds set");
				} else {
					sender.sendMessage("Locations are wrong or not defined");
				}
			} else {
				sender.sendMessage("Arena does not exist");
			}
			return true;
		}
		// set game level
		else if (args.length == 3 && args[1].equalsIgnoreCase("setgamelevel")) {
			Arena arena = plugin.pdata.getArenaByName(args[0]);
			if (arena != null) {
				if (arena.isArenaEnabled()) {
					sender.sendMessage("Disable arena first");
					return true;
				}
				PlayerCuboidSelection selection = plselection
						.getPlayerSelection(player, true);
				if (selection != null) {
					if (arena.setGameLevel(args[2], selection.getMinimumLocation(), selection.getMaximumLocation())) {
						sender.sendMessage("GameLevel set");
					} else {
						sender.sendMessage("GameLevel should be in arena bounds");
					}
				} else {
					sender.sendMessage("Locations are wrong or not defined");
				}
			} else {
				sender.sendMessage("Arena does not exist");
			}
			return true;
		}
		// set gamelevel destroy delay
		else if (args.length == 3
				&& args[1].equalsIgnoreCase("setgameleveldestroydelay")) {
			Arena arena = plugin.pdata.getArenaByName(args[0]);
			if (arena != null) {
				if (arena.isArenaEnabled()) {
					sender.sendMessage("Disable arena first");
					return true;
				}
				arena.setGameLevelDestroyDelay(Integer.valueOf(args[2]));
				sender.sendMessage("GameLevel blocks destroy delay set");
			} else {
				sender.sendMessage("Arena does not exist");
			}
			return true;
		}
		// set looselevel
		else if (args.length == 2 && args[1].equalsIgnoreCase("setloselevel")) {
			Arena arena = plugin.pdata.getArenaByName(args[0]);
			if (arena != null) {
				if (arena.isArenaEnabled()) {
					sender.sendMessage("Disable arena first");
					return true;
				}
				PlayerCuboidSelection selection = plselection.getPlayerSelection(player, true);
				if (selection != null) {
					if (arena.setLooseLevel(selection.getMinimumLocation(),
							selection.getMaximumLocation())) {
						sender.sendMessage("LoseLevel set");
					} else {
						sender.sendMessage("LoseLevel should be in arena bounds");
					}
				} else {
					sender.sendMessage("Locations are wrong or not defined");
				}
			} else {
				sender.sendMessage("Arena does not exist");
			}
			return true;
		}
		// set spawnpoint
		else if (args.length == 2 && args[1].equalsIgnoreCase("setspawn")) {
			Arena arena = plugin.pdata.getArenaByName(args[0]);
			if (arena != null) {
				if (arena.isArenaEnabled()) {
					sender.sendMessage("Disable arena first");
					return true;
				}
				if (arena.setSpawnPoint(player.getLocation())) {
					sender.sendMessage("Spawnpoint set");
				} else {
					sender.sendMessage("Spawnpoint should be in arena bounds");
				}
			} else {
				sender.sendMessage("Arena does not exist");
			}
			return true;
		}
		// set maxPlayers
		else if (args.length == 3 && args[1].equalsIgnoreCase("setmaxplayers")) {
			Arena arena = plugin.pdata.getArenaByName(args[0]);
			if (arena != null) {
				if (arena.isArenaEnabled()) {
					sender.sendMessage("Disable arena first");
					return true;
				}
				arena.setMaxPlayers(Integer.valueOf(args[2]));
				sender.sendMessage("Max Players set");
			} else {
				sender.sendMessage("Arena does not exist");
			}
			return true;
		}
		// set min players
		else if (args.length == 3 && args[1].equalsIgnoreCase("setminplayers")) {
			Arena arena = plugin.pdata.getArenaByName(args[0]);
			if (arena != null) {
				if (arena.isArenaEnabled()) {
					sender.sendMessage("Disable arena first");
					return true;
				}
				arena.setMinPlayers(Integer.valueOf(args[2]));
				sender.sendMessage("Min Players set");
			} else {
				sender.sendMessage("Arena does not exist");
			}
			return true;
		}
		// set vote percent
		else if (args.length == 3 && args[1].equalsIgnoreCase("setvotepercent")) {
			Arena arena = plugin.pdata.getArenaByName(args[0]);
			if (arena != null) {
				if (arena.isArenaEnabled()) {
					sender.sendMessage("Disable arena first");
					return true;
				}
				arena.setVotePercent(Double.valueOf(args[2]));
				sender.sendMessage("Vote percent set");
			} else {
				sender.sendMessage("Arena does not exist");
			}
			return true;
		}
		// set countdown
		else if (args.length == 3 && args[1].equalsIgnoreCase("setcountdown")) {
			Arena arena = plugin.pdata.getArenaByName(args[0]);
			if (arena != null) {
				if (arena.isArenaEnabled()) {
					sender.sendMessage("Disable arena first");
					return true;
				}
				arena.setCountdown(Integer.valueOf(args[2]));
			} else {
				sender.sendMessage("Arena does not exist");
			}
			return true;
		}
		// set items rewards
		else if (args.length == 2 && args[1].equalsIgnoreCase("setitemsrewards")) {
			Arena arena = plugin.pdata.getArenaByName(args[0]);
			if (arena != null) {
				if (arena.isArenaEnabled()) {
					sender.sendMessage("Disable arena first");
					return true;
				}
				arena.setRewards(player.getInventory().getContents());
				sender.sendMessage("Items Rewards set");
			} else {
				sender.sendMessage("Arena does not exist");
			}
			return true;
		}
		// set money rewards
		else if (args.length == 3 && args[1].equalsIgnoreCase("setmoneyrewards")) {
			Arena arena = plugin.pdata.getArenaByName(args[0]);
			if (arena != null) {
				if (arena.isArenaEnabled()) {
					sender.sendMessage("Disable arena first");
					return true;
				}
				arena.setRewards(Integer.valueOf(args[2]));
				sender.sendMessage("Money Rewards set");
			} else {
				sender.sendMessage("Arena does not exist");
			}
			return true;
		}
		// set time limit
		else if (args.length == 3 && args[1].equalsIgnoreCase("settimelimit")) {
			Arena arena = plugin.pdata.getArenaByName(args[0]);
			if (arena != null) {
				if (arena.isArenaEnabled()) {
					sender.sendMessage("Disable arena first");
					return true;
				}
				arena.setTimeLimit(Integer.valueOf(args[2]));
				sender.sendMessage("Time limit set");
			} else {
				sender.sendMessage("Arena does not exist");
			}
			return true;
		}
		// set countdown
		else if (args.length == 3 && args[1].equalsIgnoreCase("setcountdown")) {
			Arena arena = plugin.pdata.getArenaByName(args[0]);
			if (arena != null) {
				if (arena.isArenaEnabled()) {
					sender.sendMessage("Disable arena first");
					return true;
				}
				arena.setCountdown(Integer.valueOf(args[2]));
				sender.sendMessage("Countdown set");
			} else {
				sender.sendMessage("Arena does not exist");
			}
			return true;
		}
		// finish arena creation
		else if (args.length == 2 && args[1].equalsIgnoreCase("finish")) {
			Arena arena = plugin.pdata.getArenaByName(args[0]);
			if (arena != null) {
				if (!arena.isArenaEnabled()) {
					if (arena.isArenaConfigured().equalsIgnoreCase("yes")) {
						arena.saveToConfig();
						plugin.pdata.registerArena(arena);
						arena.enableArena();
						sender.sendMessage("Arena saved and enabled");
					} else {
						sender.sendMessage("Arena is not configured. Reason: " + arena.isArenaConfigured());
					}
				} else {
					sender.sendMessage("Disable arena first");
				}
			} else {
				sender.sendMessage("Arena does not exist");
			}
			return true;
		}
		// disable arena
		else if (args.length == 2 && args[1].equalsIgnoreCase("disable")) {
			Arena arena = plugin.pdata.getArenaByName(args[0]);
			if (arena != null) {
				arena.disableArena();
				sender.sendMessage("Arena disabled");
			} else {
				sender.sendMessage("Arena does not exist");
			}
			return true;
		}
		// enable arena
		else if (args.length == 2 && args[1].equalsIgnoreCase("enable")) {
			Arena arena = plugin.pdata.getArenaByName(args[0]);
			if (arena != null) {
				if (arena.isArenaEnabled()) {
					sender.sendMessage("Arena already enabled.");
				} else {
					if (arena.enableArena()) {
						sender.sendMessage("Arena enabled");
					} else {
						sender.sendMessage("Arena is not configured. Reason: "
								+ arena.isArenaConfigured());
					}
				}
			} else {
				sender.sendMessage("Arena does not exist");
			}
			return true;
		}
		// reload messages
		else if (args.length == 1 && args[0].equalsIgnoreCase("reloadmsg")) {
			Messages.loadMessages(plugin);
			sender.sendMessage("Messages reloaded");
			return true;
		}
		// reload bars
		else if (args.length == 1 && args[0].equalsIgnoreCase("reloadbars")) {
			Bars.loadBars(plugin);
			sender.sendMessage("Bars reloaded");
			return true;
		}
		return false;
	}

}
