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

package me.wolfyscript.customcrafting.recipes;

import me.wolfyscript.lib.com.fasterxml.jackson.annotation.JsonIgnore;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.inventory.Recipe;

public interface ICustomVanillaRecipe<T extends Recipe> {

    String PLACEHOLDER_PREFIX = "cc_placeholder.";
    String DISPLAY_PREFIX = "cc_display.";

    @JsonIgnore
    T getVanillaRecipe();

    @JsonIgnore
    boolean isVisibleVanillaBook();

    @JsonIgnore
    void setVisibleVanillaBook(boolean vanillaBook);

    @JsonIgnore
    boolean isAutoDiscover();

    @JsonIgnore
    void setAutoDiscover(boolean autoDiscover);

    static NamespacedKey toPlaceholder(NamespacedKey recipeID) {
        return new NamespacedKey(recipeID.getNamespace(), PLACEHOLDER_PREFIX + recipeID.getKey());
    }

    static NamespacedKey toDisplayKey(NamespacedKey recipeID) {
        return new NamespacedKey(recipeID.getNamespace(), DISPLAY_PREFIX + recipeID.getKey());
    }

    static boolean isPlaceholderRecipe(org.bukkit.NamespacedKey bukkitKey) {
        return bukkitKey.getKey().startsWith(PLACEHOLDER_PREFIX);
    }

    static boolean isDisplayRecipe(org.bukkit.NamespacedKey bukkitKey) {
        return bukkitKey.getKey().startsWith(DISPLAY_PREFIX);
    }

    static boolean isPlaceholderOrDisplayRecipe(org.bukkit.NamespacedKey bukkitKey) {
        return isPlaceholderRecipe(bukkitKey) || isDisplayRecipe(bukkitKey);
    }

    static NamespacedKey toOriginalKey(org.bukkit.NamespacedKey bukkitKey) {
        return new NamespacedKey(bukkitKey.getNamespace(), bukkitKey.getKey().replace(PLACEHOLDER_PREFIX, "").replace(DISPLAY_PREFIX, ""));
    }

}
