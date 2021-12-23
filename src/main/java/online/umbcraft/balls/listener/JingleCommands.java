package online.umbcraft.balls.listener;

import online.umbcraft.balls.JingleBall;
import online.umbcraft.balls.enums.JinglePerm;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.StringUtil;

import java.util.*;

public class JingleCommands implements CommandExecutor, TabCompleter {

    final private JingleBall plugin;

    public JingleCommands(JingleBall plugin) {
        this.plugin = plugin;;
    }



    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(args.length == 0) {
            return onEmptyCommand(sender);
        }
        if(args[0].equalsIgnoreCase("wand")) {
            return onWandCommand(sender);
        }
        if(args[0].equalsIgnoreCase("spectate")) {
            return onSpecateCommand(sender);
        }
        return false;
    }

    public boolean onSpecateCommand(CommandSender sender) {
        if(!sender.hasPermission(JinglePerm.SPECTATOR.path)) {
            sender.sendMessage(ChatColor.RED + "You're not magical enough to spectate! (need "+JinglePerm.SPECTATOR+")");
            return false;
        }
        if(sender.getName().contentEquals("CONSOLE")) {
            sender.sendMessage(ChatColor.RED + "Console cannot run this command!");
            return false;
        }
        Player playerSender = (Player) sender;
        UUID uuid = playerSender.getUniqueId();
        boolean isSpectator = plugin.isSpectator(uuid);
        if(isSpectator) {
            if(plugin.getAttemptedSpectators().contains(uuid)) {
                sender.sendMessage(ChatColor.RED+"You may not be a spectator yet. Wait 20 seconds.");
            }
            else {
                plugin.getSpectators().remove(uuid);
                playerSender.setGameMode(GameMode.SURVIVAL);
                playerSender.setHealth(0);
                plugin.getScores().addPlayer(playerSender);
                sender.sendMessage(ChatColor.GREEN + "You are no longer a spectator! Good luck!");
            }
        }
        else {
            if(!plugin.getAttemptedSpectators().contains(uuid)) {
                sender.sendMessage(ChatColor.RED+"Are you sure you want to be a spectator? ");
                sender.sendMessage(ChatColor.RED+"You will lose all points you have gained thus far.");
                sender.sendMessage(ChatColor.RED+"You may start over if you exit spectator mode.");
                sender.sendMessage(ChatColor.RED+"To become a spectator, run '/jingle spectate' again within 20 seconds.");
                plugin.getAttemptedSpectators().add(uuid);

                // remove uuid from attempted specs list in 20 seconds
                plugin.getServer().getScheduler().scheduleSyncDelayedTask(
                        plugin,
                        () -> plugin.getAttemptedSpectators().remove(uuid),
                        400
                );
            }
            else {
                sender.sendMessage(ChatColor.GREEN+"You are now a spectator. Enjoy the show!");
                plugin.getSpectators().add(uuid);
                playerSender.setGameMode(GameMode.SPECTATOR);
                plugin.getScores().removePlayer(uuid);
                plugin.getScores().showScoreboard(playerSender);
            }
        }

        return true;
    }

    public boolean onWandCommand(CommandSender sender) {
        if(!sender.hasPermission(JinglePerm.WAND.path)) {
            sender.sendMessage(ChatColor.RED + "You're not magical enough to get a wand! (need "+JinglePerm.WAND+")");
            return false;
        }
        if(sender.getName().contentEquals("CONSOLE")) {
            sender.sendMessage(ChatColor.RED + "Console cannot run this command!");
            return false;
        }
        ItemStack wand = new ItemStack(Material.BAMBOO,1);
        ItemMeta meta = wand.getItemMeta();
        meta.setDisplayName(ChatColor.LIGHT_PURPLE+"Jingle Wand");
        wand.setItemMeta(meta);
        ((Player) sender).getInventory().addItem(wand);
        return true;
    }

    public boolean onEmptyCommand(CommandSender sender) {
        sender.sendMessage(ChatColor.GREEN + "Jingle Balls!");
        sender.sendMessage(ChatColor.GREEN + "Punch snow to get snowballs");
        sender.sendMessage(ChatColor.GREEN + "Throw those snowballs at other players");
        sender.sendMessage(ChatColor.GREEN + "Try not to die!");
        sender.sendMessage(ChatColor.GREEN + "to spectate: /jingle spectate (you will lose all your progress) ");

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        List<String> commands = new ArrayList<>();

        if (args.length == 1) {
            if (sender.hasPermission(JinglePerm.WAND.path)) {
                commands.add("wand");
            }
            if (sender.hasPermission(JinglePerm.WAND.path)) {
                commands.add("spectate");
            }
            StringUtil.copyPartialMatches(args[0], commands, completions);
        }
        Collections.sort(completions);
        return completions;
    }
}
