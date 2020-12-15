package me.wolfyscript.customcrafting.utils;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.TestCache;
import me.wolfyscript.customcrafting.data.cache.items.Items;
import me.wolfyscript.customcrafting.recipes.types.ICustomRecipe;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.chat.ClickData;
import me.wolfyscript.utilities.api.chat.ClickEvent;
import me.wolfyscript.utilities.api.chat.HoverEvent;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.Pair;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

public class ChatUtils {

    private static final WolfyUtilities api = CustomCrafting.getApi();
    private final CustomCrafting customCrafting;


    public ChatUtils(CustomCrafting customCrafting) {
        this.customCrafting = customCrafting;
    }

    public static boolean checkPerm(CommandSender sender, String perm) {
        return checkPerm(sender, perm, true);
    }

    public static boolean checkPerm(CommandSender sender, String perm, boolean sendMessage) {
        if (api.getPermissions().hasPermission(sender, perm)) {
            return true;
        }
        if (sendMessage) {
            if (sender instanceof Player) {
                api.getChat().sendPlayerMessage((Player) sender, "$msg.denied_perm$", new Pair<>("%PERM%", perm));
            } else {
                sender.sendMessage(api.getChat().getCONSOLE_PREFIX() + api.getLanguageAPI().replaceKeys("$msg.denied_perm$").replace("%PERM%", perm).replace("&", "§"));
            }
        }
        return false;
    }

    public static NamespacedKey getNamespacedKey(Player player, String s, String[] args) {
        NamespacedKey namespacedKey = null;
        try {
            if (s.length() > 1) {
                namespacedKey = new NamespacedKey(args[0], args[1]);
            } else if (s.contains(":")) {
                namespacedKey = NamespacedKey.getByString(s);
            }
        } catch (IllegalArgumentException e) {
            api.getLanguageAPI().replaceKey("msg.player.invalid_namespacedkey").forEach(s1 -> api.getChat().sendPlayerMessage(player, s1));
        }
        return namespacedKey;
    }

    public static void sendCategoryDescription(Player player) {
        List<String> description = ((TestCache) api.getInventoryAPI().getGuiHandler(player).getCustomCache()).getRecipeBookEditor().getCategory().getDescription();
        for (int i = 0; i < 15; i++) {
            player.sendMessage("");
        }
        api.getChat().sendPlayerMessage(player, "------------------[&cEdit Description&7]-----------------");
        api.getChat().sendPlayerMessage(player, "");
        if (!description.isEmpty()) {
            int i = 0;
            for (String line : description) {
                int finalI = i;
                api.getChat().sendActionMessage(player, new ClickData("§7[§4-§7] ", (wolfyUtilities, player1) -> {
                    description.remove(finalI);
                    sendCategoryDescription(player1);
                }, true), new ClickData(line, null));
                i++;
            }
        } else {
            api.getChat().sendPlayerMessage(player, "&l&cNo Description set yet!");
        }
        api.getChat().sendPlayerMessage(player, "");
        api.getChat().sendPlayerMessage(player, "-------------------------------------------------");
        api.getChat().sendActionMessage(player, new ClickData("                    §7[§3Back to Recipe Book Editor§7]", (wolfyUtilities, player1) -> api.getInventoryAPI().getGuiHandler(player1).openCluster(), true));
    }

    public static void sendLoreManager(Player player) {
        ItemMeta itemMeta = ((TestCache) api.getInventoryAPI().getGuiHandler(player).getCustomCache()).getItems().getItem().getItemMeta();
        for (int i = 0; i < 15; i++) {
            player.sendMessage("");
        }
        api.getChat().sendPlayerMessage(player, "-------------------[&cRemove Lore&7]------------------");
        api.getChat().sendPlayerMessage(player, "");
        List<String> lore;
        if (itemMeta != null && itemMeta.hasLore()) {
            lore = itemMeta.getLore() == null ? new ArrayList<>() : itemMeta.getLore();

            int i = 0;
            for (String line : lore) {
                int finalI = i;
                api.getChat().sendActionMessage(player, new ClickData("§7[§4-§7] ", (wolfyUtilities, player1) -> {
                    lore.remove(finalI);
                    itemMeta.setLore(lore);
                    ((TestCache) api.getInventoryAPI().getGuiHandler(player).getCustomCache()).getItems().getItem().setItemMeta(itemMeta);
                    sendLoreManager(player1);
                }, true), new ClickData(line, null));
                i++;
            }
        } else {
            api.getChat().sendPlayerMessage(player, "&l&cNo Lore set yet!");
        }
        api.getChat().sendPlayerMessage(player, "");
        api.getChat().sendPlayerMessage(player, "-------------------------------------------------");
        api.getChat().sendActionMessage(player, new ClickData("                        §7[§3Back to ItemCreator§7]", (wolfyUtilities, player1) -> api.getInventoryAPI().getGuiHandler(player1).openCluster(), true));
    }

    public static void sendAttributeModifierManager(Player player) {
        TestCache cache = ((TestCache) api.getInventoryAPI().getGuiHandler(player).getCustomCache());
        Items items = cache.getItems();
        ItemMeta itemMeta = items.getItem().getItemMeta();
        for (int i = 0; i < 15; i++) {
            player.sendMessage("");
        }
        api.getChat().sendPlayerMessage(player, "-----------------[&cRemove Modifier&7]-----------------");
        api.getChat().sendPlayerMessage(player, "");
        if (itemMeta.hasAttributeModifiers()) {
            Collection<AttributeModifier> modifiers = itemMeta.getAttributeModifiers(Attribute.valueOf(cache.getSubSetting().substring("attribute.".length()).toUpperCase(Locale.ROOT)));
            if (modifiers != null) {
                api.getChat().sendPlayerMessage(player, "        §e§oName   §b§oAmount  §6§oEquipment-Slot  §3§oMode  §7§oUUID");
                api.getChat().sendPlayerMessage(player, "");
                for (AttributeModifier modifier : modifiers) {
                    api.getChat().sendActionMessage(player,
                            new ClickData("§7[§c-§7] ", (wolfyUtilities, player1) -> {
                                TestCache cache1 = (TestCache) api.getInventoryAPI().getGuiHandler(player).getCustomCache();
                                itemMeta.removeAttributeModifier(Attribute.valueOf(cache1.getSubSetting().substring("attribute.".length()).toUpperCase(Locale.ROOT)), modifier);
                                cache1.getItems().getItem().setItemMeta(itemMeta);
                                sendAttributeModifierManager(player1);
                            }, new HoverEvent(net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_TEXT, "§c" + modifier.getName())),
                            new ClickData("§e" + modifier.getName() + "  §b" + modifier.getAmount() + "  §6" + (modifier.getSlot() == null ? "ANY" : modifier.getSlot()) + "  §3" + modifier.getOperation(), null),
                            new ClickData("  ", null),
                            new ClickData("§7[UUID]", null, new HoverEvent(net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_TEXT, "§7[§3Click to copy§7]\n" + modifier.getUniqueId()), new ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.SUGGEST_COMMAND, "" + modifier.getUniqueId()))
                    );
                }
            } else {
                api.getChat().sendPlayerMessage(player, "&cNo attributes set yet!");
            }
        }
        api.getChat().sendPlayerMessage(player, "");
        api.getChat().sendPlayerMessage(player, "-------------------------------------------------");
        api.getChat().sendActionMessage(player, new ClickData("                     §7[§3Back to ItemCreator§7]", (wolfyUtilities, player1) -> {
            for (int i = 0; i < 15; i++) {
                player.sendMessage("");
            }
            api.getInventoryAPI().getGuiHandler(player1).openCluster();
        }, true));
    }

    public static void sendRecipeItemLoadingError(String namespace, String key, String type, Exception ex) {
        api.getChat().sendConsoleMessage("-------------------------------------------------");
        api.getChat().sendConsoleMessage("Error loading Contents for: " + namespace + ":" + key);
        api.getChat().sendConsoleMessage("    Type: " + type);
        api.getChat().sendConsoleMessage("    Message: " + ex.getMessage());
        if (ex.getCause() != null) {
            api.getChat().sendConsoleMessage("    Cause: " + ex.getCause().getMessage());
        }
        api.getChat().sendConsoleMessage("You should check the config for empty settings ");
        api.getChat().sendConsoleMessage("e.g. No set Result or Source Item!");
        api.getChat().sendConsoleMessage("------------------[StackTrace]-------------------");
        ex.printStackTrace();
        if (ex.getCause() != null) {
            api.getChat().sendConsoleMessage("Caused StackTrace: ");
            ex.getCause().printStackTrace();
        }
        api.getChat().sendConsoleMessage("------------------[StackTrace]-------------------");
    }

    public void sendRecipeListExpanded(Player player) {
        sendRecipeList(player, customCrafting.getRecipeHandler().getRecipes(((TestCache) api.getInventoryAPI().getGuiHandler(player).getCustomCache()).getRecipeType()));
        api.getChat().sendPlayerMessage(player, "none", "recipe_editor", "input");
    }

    public void sendRecipeList(Player player, List<? extends ICustomRecipe<?>> customRecipes) {
        TestCache cache = (TestCache) api.getInventoryAPI().getGuiHandler(player).getCustomCache();
        for (int i = 0; i < 20; i++) {
            player.sendMessage(" ");
        }
        int currentPage = cache.getChatLists().getCurrentPageRecipes();
        int maxPages = ((customRecipes.size() % 15) > 0 ? 1 : 0) + customRecipes.size() / 15;

        api.getChat().sendActionMessage(player, new ClickData("[&3« Back&7]", (wolfyUtilities, player1) -> Bukkit.getScheduler().runTask(wolfyUtilities.getPlugin(), () -> wolfyUtilities.getInventoryAPI().getGuiHandler(player1).openCluster()), true),
                new ClickData("                   &7&lRecipes         ", null),
                new ClickData("&7[&e&l«&7]", (wolfyUtilities, p) -> {
                    if (currentPage > 1) {
                        cache.getChatLists().setCurrentPageRecipes(cache.getChatLists().getCurrentPageRecipes() - 1);
                        sendRecipeListExpanded(p);
                    }
                }),
                new ClickData(" &e" + currentPage + "§7/§6" + maxPages + "", null),
                new ClickData(" &7[&e&l»&7]", (wolfyUtilities, p) -> {
                    if (currentPage < maxPages) {
                        cache.getChatLists().setCurrentPageRecipes(cache.getChatLists().getCurrentPageRecipes() + 1);
                        sendRecipeListExpanded(p);
                    }
                }));
        api.getChat().sendPlayerMessage(player, "&8-------------------------------------------------");

        for (int i = (currentPage - 1) * 15; i < (currentPage - 1) * 15 + 15; i++) {
            if (i < customRecipes.size()) {
                ICustomRecipe<?> recipe = customRecipes.get(i);
                ClickEvent commandSuggest = new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, recipe.getNamespacedKey().getNamespace() + " " + recipe.getNamespacedKey().getKey());
                if (recipe.getResult() == null) {
                    api.getChat().sendActionMessage(player, new ClickData(" - §7[§c!§7] §c", null), new ClickData(recipe.getNamespacedKey().toString(), null, commandSuggest, new HoverEvent(HoverEvent.Action.SHOW_TEXT, "§cFailed to load result item!")));
                } else {
                    api.getChat().sendActionMessage(player, new ClickData(" - ", null), new ClickData(recipe.getNamespacedKey().toString(), null, commandSuggest, new HoverEvent(recipe.getResult().create())));
                }
            } else {
                api.getChat().sendPlayerMessage(player, "");
            }
        }
        api.getChat().sendPlayerMessage(player, "&8-------------------------------------------------");
    }
}
