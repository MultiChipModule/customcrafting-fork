/*
 *       ____ _  _ ____ ___ ____ _  _ ____ ____ ____ ____ ___ _ _  _ ____
 *       |    |  | [__   |  |  | |\/| |    |__/ |__| |___  |  | |\ | | __
 *       |___ |__| ___]  |  |__| |  | |___ |  \ |  | |     |  | | \| |__]
 *
 *       CustomCrafting Recipe creation and management tool for Minecraft
 *                      Copyright (C) 2021  WolfyScript
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package me.wolfyscript.customcrafting.utils.cooking;

import com.wolfyscript.utilities.bukkit.BukkitNamespacedKey;
import com.wolfyscript.utilities.bukkit.world.items.CustomItem;
import com.wolfyscript.utilities.tuple.Pair;
import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.utils.NamespacedKeyUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.FurnaceStartSmeltEvent;

/**
 * Uses the new {@link FurnaceStartSmeltEvent} to more efficiently handle custom cooking recipes.
 */
public class FurnaceListener1_17Adapter implements Listener {

    private final CustomCrafting customCrafting;
    private final CookingManager manager;

    public FurnaceListener1_17Adapter(CustomCrafting customCrafting, CookingManager manager) {
        this.customCrafting = customCrafting;
        this.manager = manager;
    }

    @EventHandler
    public void onStartSmelt(FurnaceStartSmeltEvent event) {
        var recipe = event.getRecipe();
        if (recipe.getKey().getNamespace().equals(NamespacedKeyUtils.NAMESPACE)) {
            var data = manager.getAdapter().processRecipe(event.getSource(), BukkitNamespacedKey.fromBukkit(recipe.getKey()), event.getBlock());
            //Update the cache to the new Custom Recipe.
            manager.cacheRecipeData(event.getBlock(), data);
        } else {
            //Update the cache with the vanilla recipe
            manager.cacheRecipeData(event.getBlock(), new Pair<>(null, false));
            //Check if the CustomItem is allowed in Vanilla recipes
            CustomItem customItem = CustomItem.getByItemStack(event.getSource());
            if (customItem != null && customItem.isBlockVanillaRecipes()) {
                event.setTotalCookTime(0); //"Cancel" the process if it is.
            }
        }
    }
}
