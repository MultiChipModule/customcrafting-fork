package me.wolfyscript.customcrafting.commands.recipes;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.commands.AbstractSubCommand;
import me.wolfyscript.customcrafting.data.TestCache;
import me.wolfyscript.customcrafting.gui.Setting;
import me.wolfyscript.customcrafting.recipes.types.ICustomRecipe;
import me.wolfyscript.customcrafting.utils.ChatUtils;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.utils.NamespacedKey;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class EditSubCommand extends AbstractSubCommand {

    public EditSubCommand(CustomCrafting customCrafting) {
        super("edit", new ArrayList<>(), customCrafting);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull String var3, @NotNull String[] args) {
        if (sender instanceof Player) {
            WolfyUtilities api = CustomCrafting.getApi();
            Player player = (Player) sender;
            if (ChatUtils.checkPerm(sender, "customcrafting.cmd.recipes.edit")) {
                if (args.length > 0) {
                    ICustomRecipe<?> customRecipe = customCrafting.getRecipeHandler().getRecipe(new me.wolfyscript.utilities.api.utils.NamespacedKey(args[0].split(":")[0], args[0].split(":")[1]));
                    if (customRecipe != null) {
                        ((TestCache) api.getInventoryAPI().getGuiHandler(player).getCustomCache()).setSetting(Setting.valueOf(customRecipe.getRecipeType().toString().toUpperCase(Locale.ROOT)));
                        if (customCrafting.getRecipeHandler().loadRecipeIntoCache(customRecipe, api.getInventoryAPI().getGuiHandler(player))) {
                            Bukkit.getScheduler().runTaskLater(customCrafting, () -> api.getInventoryAPI().openGui(player, "none", "recipe_creator"), 1);
                        }
                    } else {
                        api.sendPlayerMessage((Player) sender, "$msg.gui.recipe_editor.not_existing$", new String[]{"%RECIPE%", args[0] + ":" + args[1]});
                    }
                }
            }
        }
        return true;
    }

    @Override
    protected @Nullable
    List<String> onTabComplete(@NotNull CommandSender var1, @NotNull String var3, @NotNull String[] args) {
        List<String> results = new ArrayList<>();
        List<String> recipes = customCrafting.getRecipeHandler().getRecipes().keySet().stream().map(NamespacedKey::toString).collect(Collectors.toList());
        StringUtil.copyPartialMatches(args[args.length - 1], recipes, results);
        return results;
    }
}