package online.umbcraft.balls;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class LuckySnows {

    final static private List<BiConsumer<Player,Block>> deck;

    static {
        deck = new ArrayList<>();
        deck.add(LuckySnows::drawCoal);
        deck.add(LuckySnows::drawIcicle);
        deck.add(LuckySnows::drawBell);
        deck.add(LuckySnows::drawBerry);
        deck.add(LuckySnows::drawGun);
    }


    public static void drawRandomItem(Player p, Block b) {
        p.playSound(p.getLocation(), Sound.ENTITY_MULE_CHEST, 1, 1);
        deck.get((int)(Math.random() * deck.size())).accept(p,b);

    }

    public static void drawCoal(Player p, Block b) {
        String item_name = ChatColor.DARK_GRAY + "[" + ChatColor.GRAY + "Gritty Coal" + ChatColor.DARK_GRAY + "]";
        giftItem(p, b, Material.COAL, item_name);
    }

    public static void drawBerry(Player p, Block b) {
        String item_name = ChatColor.DARK_GRAY + "[" + ChatColor.DARK_PURPLE + "Holiday Berries" + ChatColor.DARK_GRAY + "]";
        giftItem(p, b, Material.SWEET_BERRIES, item_name);

    }

    public static void drawBell(Player p, Block b) {
        String item_name = ChatColor.DARK_GRAY + "[" + ChatColor.YELLOW + "Bell Bomb" + ChatColor.DARK_GRAY + "]";
        giftItem(p, b, Material.BELL, item_name);
    }

    public static void drawGun(Player p, Block b) {
        String item_name = ChatColor.DARK_GRAY + "[" + ChatColor.BLUE + "Snow Blower" + ChatColor.DARK_GRAY + "]";
        giftItem(p, b, Material.IRON_HOE, item_name);
    }

    public static void drawIcicle(Player p, Block b) {
        String item_name = ChatColor.DARK_GRAY + "[" + ChatColor.AQUA + "Ice Shard" + ChatColor.DARK_GRAY + "]";
        giftItem(p, b, Material.PRISMARINE_SHARD, item_name);
    }

    public static void giftItem(Player p, Block b, Material type, int amount, String name) {
        p.sendMessage(ChatColor.RED + "[!] " + ChatColor.GREEN + "You found " + name);

        ItemStack gift = new ItemStack(type, amount);

        ItemMeta gift_meta = gift.getItemMeta();
        gift_meta.setDisplayName(name);
        gift.setItemMeta(gift_meta);

        Location to_spawn = b.getLocation().clone().add(0,1,0);
        while(to_spawn.getBlock().getType() != Material.AIR)
            to_spawn = to_spawn.add(0,1,0);

        Item spawn = b.getLocation().getWorld().dropItem(to_spawn, gift);
        spawn.setVelocity(new Vector(
                        0.2 * (Math.random() - 0.5),
                        0.25,
                        0.2 * (Math.random() - 0.5)
                )
        );
        spawn.setGlowing(true);
        spawn.setCustomName(name);
        spawn.setCustomNameVisible(true);
        spawn.setPickupDelay(0);
    }

    public static void giftItem(Player p, Block b, Material type, String name) {
        giftItem(p, b, type, 1, name);
    }
}
