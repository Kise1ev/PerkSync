package ru.kiselev.minecraft.perksync.listener;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import ru.kiselev.minecraft.perksync.Plugin;
import ru.kiselev.minecraft.perksync.util.ChatColorUtil;
import ru.kiselev.minecraft.perksync.util.PerkConfigUtil;

import java.util.HashMap;

public class GameListener implements Listener {

    @EventHandler
    public void onPlayerTeleport(final PlayerTeleportEvent event) {
        if (event.getTo().getWorld().getName().startsWith("mlgrush")) {
            final Plugin plugin = Plugin.getInstance();
            new BukkitRunnable() {
                public void run() {
                    final Player player = event.getPlayer();
                    final Inventory inventory = player.getInventory();
                    for (int i = 0; i < inventory.getSize(); ++i) {
                        if (inventory.getItem(i) != null) {
                            final HashMap<String, ItemStack> playerSticks = plugin.getPlayerSticks();
                            if (inventory.getItem(i).containsEnchantment(Enchantment.KNOCKBACK) && playerSticks.get(player.getName()) != null) {
                                final ItemStack item = playerSticks.get(player.getName());
                                final ItemStack normalItem = playerSticks.get(player.getName());
                                item.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);
                                inventory.setItem(i, item);
                                plugin.getPlayerSticks().put(player.getName(), normalItem);
                            }

                            final HashMap<String, ItemStack> playerBlocks = plugin.getPlayerBlocks();
                            if (inventory.getItem(i).getType().isBlock() && !inventory.getItem(i).containsEnchantment(Enchantment.KNOCKBACK) && (playerBlocks.containsValue(inventory.getItem(i)) || inventory.getItem(i).getType().equals(Material.SANDSTONE)) && playerBlocks.containsKey(player.getName())) {
                                final ItemStack item = playerBlocks.get(player.getName());
                                final ItemStack normalItem = playerBlocks.get(player.getName());
                                item.setAmount(64);
                                inventory.setItem(i, item);
                                plugin.getPlayerBlocks().put(player.getName(), normalItem);
                            }

                            final HashMap<String, ItemStack> playerPickaxes = plugin.getPlayerPickaxes();
                            if (playerPickaxes.get(player.getName()) != null && (playerBlocks.containsValue(inventory.getItem(i)))) {
                                final ItemStack item = playerPickaxes.get(player.getName());
                                final ItemStack normalItem = playerPickaxes.get(player.getName());
                                inventory.setItem(i, item);
                                plugin.getPlayerPickaxes().put(player.getName(), normalItem);
                            }
                        }
                    }
                }
            }.runTaskLater(plugin, 3L);
        }
    }

    @EventHandler
    public void onBlockPlace(final BlockPlaceEvent event) {
        final Block block = event.getBlock();
        if (block.getWorld().getName().startsWith("mlgrush")) {
            final Plugin plugin = Plugin.getInstance();
            final Player player = event.getPlayer();

            final HashMap<String, ItemStack> playerSticks = plugin.getPlayerSticks();
            if (playerSticks.get(player.getName()) != null && playerSticks.get(player.getName()).equals(player.getInventory().getItemInHand())) {
                block.setType(Material.AIR);
                new BukkitRunnable() {
                    public void run() {
                        final Inventory inventory = player.getInventory();
                        if (!inventory.contains(playerSticks.get(player.getName()))) {
                            final ItemStack item = playerSticks.get(player.getName());
                            item.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);
                            inventory.addItem(item);
                        }
                    }
                }.runTaskLater(plugin, 1L);
            }

            final HashMap<String, ItemStack> playerBlocks = plugin.getPlayerBlocks();
            if (playerBlocks.get(player.getName()) != null && playerBlocks.get(player.getName()).equals(player.getInventory().getItemInHand())) {
                player.getInventory().setItemInHand(playerBlocks.get(player.getName()));
            }

            final HashMap<String, ItemStack> playerPickaxes = plugin.getPlayerPickaxes();
            if (playerPickaxes.get(player.getName()) != null && playerPickaxes.get(player.getName()).equals(player.getInventory().getItemInHand())) {
                player.getInventory().setItemInHand(playerPickaxes.get(player.getName()));
                new BukkitRunnable() {
                    public void run() {
                        final Inventory inventory = player.getInventory();
                        if (!inventory.contains(playerPickaxes.get(player.getName()))) {
                            final ItemStack item = playerPickaxes.get(player.getName());
                            item.addUnsafeEnchantment(Enchantment.DIG_SPEED, 5);

                            ItemMeta itemMeta = item.getItemMeta();
                            itemMeta.spigot().setUnbreakable(true);
                            item.setItemMeta(itemMeta);

                            inventory.addItem(item);
                        }
                    }
                }.runTaskLater(plugin, 1L);
            }
        }
    }

    @EventHandler
    public void onInventoryClick(final InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;

        final Plugin plugin = Plugin.getInstance();
        final FileConfiguration configFile = plugin.getConfig();
        final String messagePrefix = ChatColorUtil.translateCodes(configFile.getString("chat.prefix"));

        final Player player = (Player) event.getWhoClicked();
        final Inventory inventory = event.getInventory();
        final Inventory clickedInventory = event.getClickedInventory();
        final ItemStack currentItem = event.getCurrentItem();

        if (inventory.getName().equals(ChatColorUtil.translateCodes(configFile.getString("command.perksync.sticks.name"))) && clickedInventory != null) {
            event.setCancelled(true);

            if (clickedInventory.getType().equals(InventoryType.CHEST) && currentItem.getType() != Material.AIR) {
                if (currentItem.getType().equals(Material.STICK)) return;

                if (!player.hasPermission(PerkConfigUtil.getHashMapItems("sticks").get(currentItem))) {
                    player.sendMessage(messagePrefix + ChatColorUtil.translateCodes(configFile.getString("commands.perksync.sticks.no_permission")));
                    player.closeInventory();
                    return;
                }

                final HashMap<String, ItemStack> playerSticks = plugin.getPlayerSticks();
                if (currentItem.equals(playerSticks.get(player.getName()))) {
                    plugin.getPlayerSticks().remove(player.getName());
                    plugin.getDataConfiguration().set("sticks." + player.getName() + ".name", null);
                    plugin.getDataConfiguration().set("sticks." + player.getName() + ".lore", null);
                    plugin.getDataConfiguration().set("sticks." + player.getName() + ".type", null);
                    plugin.getDataConfiguration().set("sticks." + player.getName() + ".id", null);

                    final String stick = currentItem.getItemMeta().getDisplayName();
                    player.sendMessage(messagePrefix + ChatColorUtil.translateCodes(configFile.getString("command.perksync.sticks.on_remove").replace("%stick%", stick)));
                    player.closeInventory();
                    return;
                }

                plugin.getPlayerSticks().put(player.getName(), currentItem);
                final String stick = currentItem.getItemMeta().getDisplayName();
                player.sendMessage(messagePrefix + ChatColorUtil.translateCodes(configFile.getString("command.perksync.sticks.on_choose").replace("%stick%", stick)));
                player.closeInventory();

                return;
            }
        }

        if (inventory.getName().equals(ChatColorUtil.translateCodes(configFile.getString("command.perksync.blocks.name"))) && clickedInventory != null) {
            event.setCancelled(true);

            if (clickedInventory.getType().equals(InventoryType.CHEST) && currentItem.getType() != Material.AIR) {
                if (currentItem.getType().equals(Material.SANDSTONE)) return;

                if (!player.hasPermission(PerkConfigUtil.getHashMapItems("blocks").get(currentItem))) {
                    player.sendMessage(messagePrefix + ChatColorUtil.translateCodes(configFile.getString("commands.perksync.blocks.no_permission")));
                    player.closeInventory();
                    return;
                }

                final HashMap<String, ItemStack> playerBlocks = plugin.getPlayerBlocks();
                if (currentItem.equals(playerBlocks.get(player.getName()))) {
                    plugin.getPlayerBlocks().remove(player.getName());
                    plugin.getDataConfiguration().set("blocks." + player.getName() + ".name", null);
                    plugin.getDataConfiguration().set("blocks." + player.getName() + ".lore", null);
                    plugin.getDataConfiguration().set("blocks." + player.getName() + ".type", null);
                    plugin.getDataConfiguration().set("blocks." + player.getName() + ".id", null);

                    final String block = currentItem.getItemMeta().getDisplayName();
                    player.sendMessage(messagePrefix + ChatColorUtil.translateCodes(configFile.getString("command.perksync.blocks.on_remove").replace("%block%", block)));
                    player.closeInventory();
                    return;
                }

                plugin.getPlayerBlocks().put(player.getName(), currentItem);
                final String block = currentItem.getItemMeta().getDisplayName();
                player.sendMessage(messagePrefix + ChatColorUtil.translateCodes(configFile.getString("command.perksync.blocks.on_choose").replace("%block%", block)));
                player.closeInventory();

                return;
            }
        }

        if (inventory.getName().equals(ChatColorUtil.translateCodes(configFile.getString("command.perksync.pickaxes.name"))) && clickedInventory != null) {
            event.setCancelled(true);

            if (clickedInventory.getType().equals(InventoryType.CHEST) && currentItem.getType() != Material.AIR) {
                if (currentItem.getType().equals(Material.IRON_PICKAXE)) return;

                if (!player.hasPermission(PerkConfigUtil.getHashMapItems("pickaxes").get(currentItem))) {
                    player.sendMessage(messagePrefix + ChatColorUtil.translateCodes(configFile.getString("commands.perksync.pickaxes.no_permission")));
                    player.closeInventory();
                    return;
                }

                final HashMap<String, ItemStack> playerPickaxes = plugin.getPlayerPickaxes();
                if (currentItem.equals(playerPickaxes.get(player.getName()))) {
                    plugin.getPlayerPickaxes().remove(player.getName());
                    plugin.getDataConfiguration().set("pickaxes." + player.getName() + ".name", null);
                    plugin.getDataConfiguration().set("pickaxes." + player.getName() + ".lore", null);
                    plugin.getDataConfiguration().set("pickaxes." + player.getName() + ".type", null);
                    plugin.getDataConfiguration().set("pickaxes." + player.getName() + ".id", null);

                    final String pickaxe = currentItem.getItemMeta().getDisplayName();
                    player.sendMessage(messagePrefix + ChatColorUtil.translateCodes(configFile.getString("command.perksync.pickaxes.on_remove").replace("%pickaxe%", pickaxe)));
                    player.closeInventory();
                    return;
                }

                plugin.getPlayerPickaxes().put(player.getName(), currentItem);
                final String pickaxe = currentItem.getItemMeta().getDisplayName();
                player.sendMessage(messagePrefix + ChatColorUtil.translateCodes(configFile.getString("command.perksync.pickaxes.on_choose").replace("%pickaxe%", pickaxe)));
                player.closeInventory();
            }
        }
    }
}
