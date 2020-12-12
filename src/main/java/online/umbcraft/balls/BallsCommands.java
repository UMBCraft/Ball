package online.umbcraft.balls;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BallsCommands implements CommandExecutor, TabCompleter {

    public BallsCommands(Plugin p) {

    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(args.length == 0) {
            sender.sendMessage(ChatColor.GREEN + "Jingle Balls!");
            sender.sendMessage(ChatColor.RED + "Punch snow to get snowballs");
            sender.sendMessage(ChatColor.GREEN + "Throw those snowballs at other players");
            sender.sendMessage(ChatColor.RED + "Try not to die!");
            return true;
        }
        if(args[0].equalsIgnoreCase("wand")) {
            if(!sender.hasPermission("balls.wand")) {
                sender.sendMessage(ChatColor.RED + "You're not magical enough to get a wand!");
                return false;
            }
            if(sender.getName().contentEquals("CONSOLE")) {
                sender.sendMessage(ChatColor.RED + "Console cannot run this command!");
                return false;
            }
            ItemStack wand = new ItemStack(Material.BAMBOO,1);
            ItemMeta meta = wand.getItemMeta();
            meta.setDisplayName(ChatColor.LIGHT_PURPLE+"Magic Wand");
            wand.setItemMeta(meta);
            ((Player) sender).getInventory().addItem(wand);
            return true;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        List<String> commands = new ArrayList<>();

        if (args.length == 1) {
            if (sender.hasPermission("balls.wand")) {
                commands.add("wand");
            }
            StringUtil.copyPartialMatches(args[0], commands, completions);
        }
        Collections.sort(completions);
        return completions;
    }
}
