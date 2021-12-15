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

package me.wolfyscript.customcrafting.registry;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.gui.item_creator.tabs.ItemCreatorTab;
import me.wolfyscript.customcrafting.recipes.items.extension.ResultExtension;
import me.wolfyscript.customcrafting.recipes.items.target.MergeAdapter;
import me.wolfyscript.utilities.api.WolfyUtilCore;
import me.wolfyscript.utilities.registry.Registry;
import me.wolfyscript.utilities.registry.TypeRegistry;
import me.wolfyscript.utilities.registry.TypeRegistrySimple;
import me.wolfyscript.utilities.util.NamespacedKey;

public class CCRegistries {

    private final RegistryRecipes recipes;
    private final Registry<ItemCreatorTab> itemCreatorTabs;
    private final ClassRegistryRecipeConditions recipeConditions;
    private final TypeRegistry<MergeAdapter> recipeMergeAdapters;
    private final TypeRegistry<ResultExtension> recipeResultExtensions;

    public CCRegistries(CustomCrafting customCrafting, WolfyUtilCore core) {
        var registries = core.getRegistries();
        this.recipes = new RegistryRecipes(customCrafting, registries);
        this.itemCreatorTabs = new RegistryItemCreatorTabs(customCrafting, registries);
        this.recipeConditions = new ClassRegistryRecipeConditions(customCrafting, registries);
        this.recipeMergeAdapters = new TypeRegistrySimple<>(new NamespacedKey(customCrafting, "recipe/merge_adapters"), registries);
        this.recipeResultExtensions = new TypeRegistrySimple<>(new NamespacedKey(customCrafting, "recipe/result_extensions"), registries);
    }

    public ClassRegistryRecipeConditions getRecipeConditions() {
        return recipeConditions;
    }

    public Registry<ItemCreatorTab> getItemCreatorTabs() {
        return itemCreatorTabs;
    }

    public TypeRegistry<MergeAdapter> getRecipeMergeAdapters() {
        return recipeMergeAdapters;
    }

    public TypeRegistry<ResultExtension> getRecipeResultExtensions() {
        return recipeResultExtensions;
    }

    public RegistryRecipes getRecipes() {
        return recipes;
    }
}
