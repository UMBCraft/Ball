package online.umbcraft.balls;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class LuckySnows {


    public static void drawItem(Player p, Block b) {


        int result = (int) (Math.random() * 2);
        switch (result) {
            case (0):
                drawCoal(p);
                break;
            case (1):
                drawBerry(p);
                break;
            //     case (2):
            //        break;
            //    case (3):
            //         break;
            //     case (4):
            //         break;
            // }
        }
    }

    public static void drawCoal(Player p) {
        ItemStack coal = new ItemStack(Material.COAL);
        ItemMeta coal_meta = coal.getItemMeta();
        coal_meta.setDisplayName(ChatColor.GRAY+"Gritty Coal");
        coal.setItemMeta(coal_meta);
        p.getInventory().addItem(coal);
    }

    public static void drawBerry(Player p) {
        ItemStack berry = new ItemStack(Material.SWEET_BERRIES);
        ItemMeta berry_meta = berry.getItemMeta();
        berry_meta.setDisplayName(ChatColor.DARK_PURPLE+"Holiday Berries");
        berry.setItemMeta(berry_meta);
        p.getInventory().addItem(berry);

    }
}
