package tntrun.commands.setup.arena;

import org.bukkit.entity.Player;

import tntrun.TNTRun;
import tntrun.arena.Arena;
import tntrun.commands.setup.CommandHandlerInterface;
import tntrun.selectionget.PlayerCuboidSelection;
import tntrun.selectionget.PlayerSelection;

public class SetArena implements CommandHandlerInterface {

	private TNTRun plugin;
	private PlayerSelection selection;
	public SetArena(TNTRun plugin, PlayerSelection selection) {
		this.plugin = plugin;
		this.selection = selection;
	}

	@Override
	public boolean handleCommand(Player player, String[] args) {
		Arena arena = plugin.amanager.getArenaByName(args[0]);
		if (arena != null) {
			if (arena.getStatusManager().isArenaEnabled()) {
				player.sendMessage("Disable arena first");
				return true;
			}
			PlayerCuboidSelection sel = selection.getPlayerSelection(player, false);
			if (sel != null) {
				arena.getStructureManager().setArenaPoints(sel.getMinimumLocation(), sel.getMaximumLocation());
				player.sendMessage("Arena bounds set");
			} else {
				player.sendMessage("Locations are wrong or not defined");
			}
		} else {
			player.sendMessage("Arena does not exist");
		}
		return true;
	}

}