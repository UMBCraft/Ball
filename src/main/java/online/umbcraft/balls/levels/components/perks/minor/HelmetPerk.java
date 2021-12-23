package online.umbcraft.balls.levels.components.perks.minor;

import online.umbcraft.balls.JingleBall;
import online.umbcraft.balls.levels.components.perks.PerkImplementation;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

public class HelmetPerk extends PerkImplementation {

    @Override
    public void apply(Player p, JingleBall plugin) {
        ItemStack helm = new ItemStack(Material.IRON_HELMET);
        ItemMeta meta = helm.getItemMeta();
        meta.setDisplayName(ChatColor.GRAY + "Rusty Helm");
        helm.setItemMeta(meta);
        p.getInventory().setHelmet(helm);

        String message = "You find a rusty helmet in the dirt. (+armor)";
        p.sendMessage(ChatColor.GREEN + message);
    }

    @Override
    public void revoke(Player p, JingleBall plugin) {
        ItemStack helm = new ItemStack(Material.AIR);
        p.getInventory().setHelmet(helm);
    }
}
