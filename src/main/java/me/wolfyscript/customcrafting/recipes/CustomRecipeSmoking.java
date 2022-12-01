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

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.wolfyscript.utilities.NamespacedKey;
import com.wolfyscript.utilities.bukkit.nms.item.crafting.FunctionalRecipeBuilderSmoking;
import me.wolfyscript.customcrafting.CustomCrafting;
import org.bukkit.Material;
import org.bukkit.inventory.SmokingRecipe;

public class CustomRecipeSmoking extends CustomRecipeCooking<CustomRecipeSmoking, SmokingRecipe> {

    public CustomRecipeSmoking(NamespacedKey namespacedKey, JsonNode node) {
        super(namespacedKey, node);
    }

    @JsonCreator
    public CustomRecipeSmoking(@JsonProperty("key") @JacksonInject("key") NamespacedKey key, @JacksonInject("customcrafting") CustomCrafting customCrafting) {
        super(key, customCrafting);
    }

    @Deprecated
    public CustomRecipeSmoking(NamespacedKey key) {
        this(key, CustomCrafting.inst());
    }

    private CustomRecipeSmoking(CustomRecipeSmoking customRecipeSmoking) {
        super(customRecipeSmoking);
    }

    @Override
    public CustomRecipeSmoking clone() {
        return new CustomRecipeSmoking(this);
    }

    @Override
    public boolean validType(Material material) {
        return material.equals(Material.SMOKER);
    }

    @Override
    public SmokingRecipe getVanillaRecipe() {
        if (!getSource().isEmpty()) {
            registerRecipeIntoMinecraft(new FunctionalRecipeBuilderSmoking(getNamespacedKey(), getResult().getItemStack(), getRecipeChoice()));
        }
        return null;
    }
}
