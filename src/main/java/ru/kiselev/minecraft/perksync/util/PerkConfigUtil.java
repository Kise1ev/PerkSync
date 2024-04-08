package ru.kiselev.minecraft.perksync.util;

import lombok.experimental.UtilityClass;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import ru.kiselev.minecraft.perksync.Plugin;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

@UtilityClass
public class PerkConfigUtil {

    public static HashMap<String, ItemStack> loadCustomMap(final String path) {
        final Plugin plugin = Plugin.getInstance();
        try {
            final FileConfiguration dataConfiguration = plugin.getDataConfiguration();
            final HashMap<String, ItemStack> hashMap = new HashMap<>();
            for (final String key : dataConfiguration.getConfigurationSection(path).getKeys(false)) {
                if (dataConfiguration.getString(path + "." + key + ".type") == null) {
                    continue;
                }

                final ItemStack item = new ItemStack(Material.matchMaterial(dataConfiguration.getString(path + "." + key + ".type")), 1, (byte) dataConfiguration.getInt(path + "." + key + ".id"));
                final ItemMeta meta = item.getItemMeta();
                meta.setDisplayName(dataConfiguration.getString(path + "." + key + ".name"));

                final ArrayList<String> lore = new ArrayList<>();
                lore.add(dataConfiguration.getString(path + "." + key + ".lore"));
                meta.setLore(lore);
                item.setItemMeta(meta);

                hashMap.put(key, item);
            }
            return hashMap;
        } catch (Exception exception) {
            plugin.getLogger().severe("[" + exception.getClass().getName() + "] " + exception.getMessage());
            return null;
        }
    }

    public static void saveCustomMap(final HashMap<String, ItemStack> hashMap, final String path) {
        final Plugin plugin = Plugin.getInstance();
        try {
            for (final String key : hashMap.keySet()) {
                plugin.getDataConfiguration().set(path + "." + key + ".name", hashMap.get(key).getItemMeta().getDisplayName());
                plugin.getDataConfiguration().set(path + "." + key + ".lore", hashMap.get(key).getItemMeta().getLore().get(0));
                plugin.getDataConfiguration().set(path + "." + key + ".type", hashMap.get(key).getType().toString());
                plugin.getDataConfiguration().set(path + "." + key + ".id", hashMap.get(key).getDurability());
            }
            saveCustomYml(plugin.getDataConfiguration(), plugin.getDataFile());
        } catch (Exception exception) {
            plugin.getLogger().severe("[" + exception.getClass().getName() + "] " + exception.getMessage());
        }
    }

    public static void saveCustomYml(final FileConfiguration ymlConfig, final File ymlFile) {
        try {
            ymlConfig.save(ymlFile);
        }
        catch (Exception exception) {
            Plugin.getInstance().getLogger().severe("[" + exception.getClass().getName() + "] " + exception.getMessage());
        }
    }

    public static ArrayList<ItemStack> getListItems(final String perk) {
        final Plugin plugin = Plugin.getInstance();
        final FileConfiguration configFile = plugin.getConfig();
        final ArrayList<ItemStack> items = new ArrayList<>();
        for (int i = 0; i < configFile.getList("command.perksync." + perk + ".items").size(); ++i) {
            final String item = configFile.getList("command.perksync." + perk + ".items").get(i).toString();
            final String[] values = item.split(",");
            final String name = values.length > 1 ? values[2] : null;
            final String lore = values.length > 2 ? values[3] : null;
            final ItemStack type = values[1] != "0" ? new ItemStack(Material.getMaterial(Integer.parseInt(values[0])), 1, Byte.parseByte(values[1]))
                    : new ItemStack(Material.getMaterial(Integer.parseInt(values[0])));

            if (name != null) {
                final ItemMeta meta = type.getItemMeta();
                meta.setDisplayName(ChatColorUtil.translateCodes(name));
                type.setItemMeta(meta);
            }

            if (lore != null) {
                final ItemMeta meta = type.getItemMeta();
                final ArrayList<String> loreList = new ArrayList<>();
                loreList.add(ChatColorUtil.translateCodes(lore));
                meta.setLore(loreList);
                type.setItemMeta(meta);
            }

            items.add(type);
        }
        return items;
    }

    public static HashMap<ItemStack, String> getHashMapItems(final String perk) {
        final Plugin plugin = Plugin.getInstance();
        final FileConfiguration configFile = plugin.getConfig();
        final HashMap<ItemStack, String> items = new HashMap<>();
        for (int i = 0; i < configFile.getList("command.perksync." + perk + ".items").size(); ++i) {
            final String item = configFile.getList("command.perksync." + perk + ".items").get(i).toString();
            final String[] values = item.split(",");
            final String name = values.length > 1 ? values[2] : null;
            final String lore = values.length > 2 ? values[3] : null;
            final ItemStack type = values[1] != "0" ? new ItemStack(Material.getMaterial(Integer.parseInt(values[0])), 1, Byte.parseByte(values[1]))
                    : new ItemStack(Material.getMaterial(Integer.parseInt(values[0])));

            if (name != null) {
                final ItemMeta meta = type.getItemMeta();
                meta.setDisplayName(ChatColorUtil.translateCodes(name));
                type.setItemMeta(meta);
            }

            if (lore != null) {
                final ItemMeta meta = type.getItemMeta();
                final ArrayList<String> loreList = new ArrayList<>();
                loreList.add(ChatColorUtil.translateCodes(lore));
                meta.setLore(loreList);
                type.setItemMeta(meta);
            }

            for (final Enchantment enchantment : type.getEnchantments().keySet()) {
                type.removeEnchantment(enchantment);
            }

            items.put(type, values[4]);
        }
        return items;
    }
}