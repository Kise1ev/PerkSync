package ru.kiselev.minecraft.perksync.command;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import ru.kiselev.minecraft.perksync.Plugin;
import ru.kiselev.minecraft.perksync.util.ChatColorUtil;
import ru.kiselev.minecraft.perksync.util.PerkConfigUtil;

public class PerkSyncCommand implements CommandExecutor {

    @Override
    public boolean onCommand(final CommandSender commandSender, final Command command, final String str, final String[] args) {
        if (!(commandSender instanceof Player)) return true;

        final Player player = (Player) commandSender;

        final Plugin plugin = Plugin.getInstance();
        final FileConfiguration configFile = plugin.getConfig();
        final String messagePrefix = ChatColorUtil.translateCodes(configFile.getString("chat.prefix"));

        if (args.length == 1) {
            switch (args[0]) {
                case "reload":
                    if (!player.hasPermission("perksync")) {
                        player.sendMessage(messagePrefix + ChatColorUtil.translateCodes("&7PerkSync plugin is created by &eRoman Kiselev&7."));
                        player.sendMessage(messagePrefix + ChatColorUtil.translateCodes("&7Version: &e1.0.0"));
                        return true;
                    }

                    plugin.reloadConfig();
                    player.sendMessage(messagePrefix + ChatColorUtil.translateCodes(configFile.getString("command.perksync.reload.message")));
                    break;
                case "sticks":
                    if (player.getWorld().getName().startsWith("mlgrush")) {
                        player.sendMessage(messagePrefix + ChatColorUtil.translateCodes(configFile.getString("command.perksync.in_game")));
                        return true;
                    }

                    final Inventory sticksInventory = Bukkit.createInventory(null, configFile.getInt("command.perksync.sticks.size"), ChatColorUtil.translateCodes(configFile.getString("commands.perksync.sticks.name")));
                    for (final ItemStack item : PerkConfigUtil.getListItems("sticks")) {
                        sticksInventory.addItem(item);
                    }

                    final ItemStack playerStickItem = plugin.getPlayerSticks().get(player.getName());
                    if (playerStickItem == null) {
                        sticksInventory.setItem(sticksInventory.getSize() - 1, new ItemStack(Material.STICK));
                    } else {
                        for (final Enchantment enchantment : playerStickItem.getEnchantments().keySet()) {
                            playerStickItem.removeEnchantment(enchantment );
                        }
                        sticksInventory.setItem(sticksInventory.getSize() - 1, playerStickItem);
                    }

                    player.openInventory(sticksInventory);
                    break;
                case "blocks":
                    if (player.getWorld().getName().startsWith("mlgrush")) {
                        player.sendMessage(messagePrefix + ChatColorUtil.translateCodes(configFile.getString("command.perksync.in_game")));
                        return true;
                    }

                    final Inventory blocksInventory = Bukkit.createInventory(null, configFile.getInt("command.perksync.blocks.size"), ChatColorUtil.translateCodes(configFile.getString("commands.perksync.blocks.name")));
                    for (final ItemStack item : PerkConfigUtil.getListItems("blocks")) {
                        blocksInventory.addItem(item);
                    }

                    final ItemStack playerBlockItem = plugin.getPlayerBlocks().get(player.getName());
                    if (playerBlockItem == null) {
                        blocksInventory.setItem(blocksInventory.getSize() - 1, new ItemStack(Material.SANDSTONE));
                    } else {
                        for (final Enchantment enchantment : playerBlockItem.getEnchantments().keySet()) {
                            playerBlockItem.removeEnchantment(enchantment );
                        }
                        blocksInventory.setItem(blocksInventory.getSize() - 1, playerBlockItem);
                    }

                    player.openInventory(blocksInventory);
                    break;
                case "pickaxes":
                    if (player.getWorld().getName().startsWith("mlgrush")) {
                        player.sendMessage(messagePrefix + ChatColorUtil.translateCodes(configFile.getString("command.perksync.in_game")));
                        return true;
                    }

                    final Inventory pickaxesInventory = Bukkit.createInventory(null, configFile.getInt("command.perksync.pickaxes.size"), ChatColorUtil.translateCodes(configFile.getString("commands.perksync.pickaxes.name")));
                    for (final ItemStack item : PerkConfigUtil.getListItems("pickaxes")) {
                        pickaxesInventory.addItem(item);
                    }

                    final ItemStack playerPickaxeItem = plugin.getPlayerBlocks().get(player.getName());
                    if (playerPickaxeItem == null) {
                        pickaxesInventory.setItem(pickaxesInventory.getSize() - 1, new ItemStack(Material.IRON_PICKAXE));
                    } else {
                        for (final Enchantment enchantment : playerPickaxeItem.getEnchantments().keySet()) {
                            playerPickaxeItem.removeEnchantment(enchantment );
                        }
                        pickaxesInventory.setItem(pickaxesInventory.getSize() - 1, playerPickaxeItem);
                    }

                    player.openInventory(pickaxesInventory);
                    break;
                default:
                    player.sendMessage(messagePrefix + ChatColorUtil.translateCodes(configFile.getString("chat.command_not_found")));
                    break;
            }
            return true;
        }

        player.sendMessage(messagePrefix + ChatColorUtil.translateCodes("&7PerkSync plugin is created by &eRoman Kiselev&7."));
        player.sendMessage(messagePrefix + ChatColorUtil.translateCodes("&7Version: &e1.0.0"));

        return true;
    }
}
