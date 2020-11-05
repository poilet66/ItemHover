package me.poilet66.itemhover;

import com.palmergames.bukkit.TownyChat.Chat;
import com.palmergames.bukkit.TownyChat.events.AsyncChatHookEvent;
import net.md_5.bungee.api.chat.*;
import net.minecraft.server.v1_15_R1.NBTTagCompound;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import javax.xml.soap.Text;

public class ChatListener implements Listener {

    public ChatListener(Plugin main) {

    }


    @EventHandler
    public void onTownyChat(AsyncChatHookEvent event) {

        Player player = event.getPlayer();

        if(event.getMessage().contains("{item}")) {
            //player.sendMessage(event.getFormat();
            event.setCancelled(true);
            for(Player recipplayer : event.getRecipients()) {
                recipplayer.spigot().sendMessage(formatItemMessage(event, player));
            }

        }
    }

    public static void hover(Player p, String name, String hover) {
        p.spigot()
                .sendMessage(new ComponentBuilder(name)
                        .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(hover).create())).create());
    }

    public String convertItemStackToJson(ItemStack itemStack) {
        // First we convert the item stack into an NMS itemstack
        net.minecraft.server.v1_15_R1.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
        net.minecraft.server.v1_15_R1.NBTTagCompound compound = new NBTTagCompound();
        compound = nmsItemStack.save(compound);

        return compound.toString();
    }

    public TextComponent sendItemTooltipMessage(String message, ItemStack item) {
        String itemJson = convertItemStackToJson(item);

        // Prepare a BaseComponent array with the itemJson as a text component
        BaseComponent[] hoverEventComponents = new BaseComponent[]{
                new TextComponent(itemJson) // The only element of the hover events basecomponents is the item json
        };

        // Create the hover event
        HoverEvent event = new HoverEvent(HoverEvent.Action.SHOW_ITEM, hoverEventComponents);

        /* And now we create the text component (this is the actual text that the player sees)
         * and set it's hover event to the item event */
        TextComponent component = new TextComponent(TextComponent.fromLegacyText(message));
        component.setHoverEvent(event);

        // Finally, send the message to the player
        return component;
    }

    public TextComponent formatItemMessage(AsyncChatHookEvent event, Player player) {

        ItemStack item = player.getInventory().getItemInMainHand();
        String cColor = ChatColor.getLastColors(event.getFormat());
        TextComponent builtMessage = new TextComponent();
        TextComponent hoverMessage;
        String[] parts;
        parts = event.getMessage().split(" ");

        if(item.getType() != Material.AIR) { //if holding something in hand``
            String name = getItemName(item);
            hoverMessage = this.sendItemTooltipMessage(name, item);
        }
        else { //not holding item in either hand
            hoverMessage = new TextComponent("{item}");
            hoverMessage.setColor(net.md_5.bungee.api.ChatColor.DARK_GRAY);
        }

        String prefix = event.getFormat().replaceAll("%2\\$s","")
                .replaceAll("%1\\$s",event.getPlayer().getDisplayName());

        TextComponent reset = new TextComponent("");
        reset.setColor(net.md_5.bungee.api.ChatColor.RESET);

        builtMessage.addExtra(prefix);
        builtMessage.addExtra(reset);

        for(String part : parts) {
            if(part.equals("{item}")) {
                builtMessage.addExtra(hoverMessage);
                builtMessage.addExtra(" ");
            }
            else {
                TextComponent textPart = new TextComponent(TextComponent.fromLegacyText(cColor + part + " "));
                builtMessage.addExtra(textPart);
            }
        }

        return builtMessage;
    }

    public String getItemName(ItemStack item) {

        String name;

        if(item.getItemMeta().hasDisplayName()) {
            name = item.getItemMeta().getDisplayName();
        }
        else {
            name = CraftItemStack.asNMSCopy(item).getName().getString();
        }
        name = ChatColor.getLastColors(name) + "[" + name + ChatColor.getLastColors(name) + "]";

        return name;
    }
}
