package online.umbcraft.balls;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class LuckySnows {


    public static void drawItem(Player p, Block b) {


        int result = (int) (Math.random() * 4);
        switch (result) {
            case (0):
                drawCoal(p);
                break;
            case (1):
                drawBerry(p);
                break;
            case (2):
                drawGun(p);
                break;
            case (3):
                drawIcicle(p);
                 break;
            //     case (4):
            //         break;
            // }
        }
    }

    public static void drawCoal(Player p) {
        String item_name = ChatColor.DARK_GRAY+"["+ChatColor.GRAY + "Gritty Coal"+ChatColor.DARK_GRAY+"]";
        giftItem(p, Material.COAL, item_name);
    }

    public static void drawBerry(Player p) {
        String item_name = ChatColor.DARK_GRAY+"["+ChatColor.DARK_PURPLE + "Holiday Berries"+ChatColor.DARK_GRAY+"]";
        giftItem(p, Material.SWEET_BERRIES, item_name);

    }

    public static void drawGun(Player p) {
        String item_name = ChatColor.DARK_GRAY+"["+ChatColor.BLUE + "Snow Blower"+ChatColor.DARK_GRAY+"]";
        giftItem(p, Material.IRON_HOE, item_name);
    }

    public static void drawIcicle(Player p) {
        String item_name = ChatColor.DARK_GRAY+"["+ChatColor.AQUA + "Ice Shard"+ChatColor.DARK_GRAY+"]";
        giftItem(p, Material.PRISMARINE_SHARD, item_name);
    }

    public static void giftItem(Player p, Material type, int amount, String name) {
        p.sendMessage(ChatColor.RED+"[!] "+ChatColor.GREEN+"You found "+name);

        ItemStack gift = new ItemStack(type, amount);
        ItemMeta gift_meta = gift.getItemMeta();
        gift_meta.setDisplayName(name);
        gift.setItemMeta(gift_meta);
        p.getInventory().addItem(gift);
    }

    public static void giftItem(Player p, Material type, String name) {
        giftItem(p, type,1,name);
    }
}
