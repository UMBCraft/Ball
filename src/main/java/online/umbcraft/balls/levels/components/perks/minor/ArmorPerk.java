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

public class ArmorPerk extends PerkImplementation {

    @Override
    public void apply(Player p, JingleBall plugin) {
        ItemStack chestplate = new ItemStack(Material.CHAINMAIL_CHESTPLATE);
        ItemMeta meta = chestplate.getItemMeta();
        meta.setDisplayName(ChatColor.GRAY + "Scaled Chestplate");
        chestplate.setItemMeta(meta);
        p.getInventory().setChestplate(chestplate);

        String message = "A piece of scale mail finds its way to you. (+armor)";
        p.sendMessage(ChatColor.GREEN + message);
    }

    @Override
    public void revoke(Player p, JingleBall plugin) {
        ItemStack chest = new ItemStack(Material.LEATHER_CHESTPLATE);
        LeatherArmorMeta meta = (LeatherArmorMeta) chest.getItemMeta();
        meta.setDisplayName(ChatColor.GRAY + "Snow Suit");
        meta.setColor(Color.WHITE);
        chest.setItemMeta(meta);
        p.getInventory().setChestplate(chest);
    }
}
