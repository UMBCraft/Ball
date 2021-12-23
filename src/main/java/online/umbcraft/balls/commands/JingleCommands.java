package online.umbcraft.balls.commands;

import online.umbcraft.balls.JingleBall;
import online.umbcraft.balls.levels.components.PlayerPerks;
import online.umbcraft.balls.levels.components.perks.Perk;
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
        if(args[0].equalsIgnoreCase("perk")) {
            return onPerkCommand(sender, args);
        }
        if(args[0].equalsIgnoreCase("tournament")) {
            if(args.length != 2) {
                return onEmptyCommand(sender);
            }
            return onTournamentCommand(sender, args[1]);
        }
        if(args[0].equalsIgnoreCase("exp")) {
            if(args.length != 3) {
                return onEmptyCommand(sender);
            }
            return onExpCommand(sender, args[1], Integer.parseInt(args[2]));
        }
        return false;
    }


    public boolean onPerkCommand(CommandSender sender, String[] args) {
        if(!sender.hasPermission(JinglePerm.TOURNAMENT.path)) {
            sender.sendMessage(ChatColor.RED + "You're not magical enough to give perks! (need "+JinglePerm.PERK+")");
            return false;
        }
        if(args.length == 4) {
            Player p = plugin.getServer().getPlayer(args[2]);
            if(args[1].equalsIgnoreCase("add"))
                plugin.getLevelingManager().addPerk(p, Perk.valueOf(args[3]));
            else if(args[1].equalsIgnoreCase("remove"))
                plugin.getLevelingManager().removePerk(p, Perk.valueOf(args[3]));

        }
        return false;
    }
    public boolean onTournamentCommand(CommandSender sender, String option) {
        if(!sender.hasPermission(JinglePerm.TOURNAMENT.path)) {
            sender.sendMessage(ChatColor.RED + "You're not magical enough to start a tournament! (need "+JinglePerm.TOURNAMENT+")");
            return false;
        }

        if(option.equalsIgnoreCase("start")) {
            sender.sendMessage(ChatColor.GOLD + "Started tournament!");
            plugin.getTournamentManager().startTournament();
        }
        else if (option.equalsIgnoreCase("clear")) {
            sender.sendMessage(ChatColor.GOLD + "Clearing tournament!");
            plugin.getTournamentManager().clearTournament();
        }

        return true;
    }

    public boolean onExpCommand(CommandSender sender, String player, int amount) {
        if(!sender.hasPermission(JinglePerm.EXP.path)) {
            sender.sendMessage(ChatColor.RED + "You're not magical enough to give exp! (need "+JinglePerm.EXP+")");
            return false;
        }
        Player toAdd = plugin.getServer().getPlayer(player);
        if(toAdd == null || plugin.isSpectator(toAdd.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "Invalid player "+player);
        }
        plugin.getLevelingManager().addExp(toAdd, amount);
        return true;
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
                plugin.getScoreManager().addPlayer(playerSender);
                playerSender.setHealth(0);
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
                plugin.getLevelingManager().resetExp(playerSender);
                plugin.getScoreManager().removePlayer(uuid);
                plugin.getScoreManager().showScoreboard(playerSender);
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
            if (sender.hasPermission(JinglePerm.SPECTATOR.path)) {
                commands.add("spectate");
            }
            if (sender.hasPermission(JinglePerm.EXP.path)) {
                commands.add("exp");
            }
            if (sender.hasPermission(JinglePerm.PERK.path)) {
                commands.add("perk");
            }
            if (sender.hasPermission(JinglePerm.TOURNAMENT.path)) {
                commands.add("tournament");
            }
            StringUtil.copyPartialMatches(args[0], commands, completions);
        }
        if(args.length == 2 && args[0].equals("exp")) {
            if (sender.hasPermission(JinglePerm.EXP.path)) {
                for(UUID uuid: plugin.getScoreManager().getPlayingPlayers()) {
                    commands.add(plugin.getServer().getPlayer(uuid).getName());
                }
            }
            StringUtil.copyPartialMatches(args[1], commands, completions);
        }
        if(args.length == 2 && args[0].equals("tournament")) {
            if (sender.hasPermission(JinglePerm.TOURNAMENT.path)) {
                commands.add("start");
                commands.add("clear");
            }
            StringUtil.copyPartialMatches(args[1], commands, completions);
        }
        if(args.length == 2 && args[0].equals("perk")) {
            if (sender.hasPermission(JinglePerm.PERK.path)) {
                commands.add("add");
                commands.add("remove");
            }
            StringUtil.copyPartialMatches(args[1], commands, completions);
        }
        if(args.length == 3 && args[0].equals("exp")) {
            if (sender.hasPermission(JinglePerm.EXP.path)) {
                commands.add("<amount>");
            }
            completions.addAll(commands);
        }
        if(args.length == 3 && args[0].equals("perk")) {
            if (sender.hasPermission(JinglePerm.PERK.path)) {
                for(UUID uuid: plugin.getScoreManager().getPlayingPlayers()) {
                    commands.add(plugin.getServer().getPlayer(uuid).getName());
                }
            }
            StringUtil.copyPartialMatches(args[2], commands, completions);
        }

        if(args.length == 4 && args[0].equals("perk")) {
            if (sender.hasPermission(JinglePerm.PERK.path)) {
                for(Perk perk: PlayerPerks.PERKS) {
                    commands.add(perk.toString());
                }
            }
            StringUtil.copyPartialMatches(args[3], commands, completions);
        }
        Collections.sort(completions);
        return completions;
    }
}
