package me.wolfyscript.customcrafting.recipes.types.smoker;

import me.wolfyscript.customcrafting.recipes.types.CustomCookingRecipe;
import me.wolfyscript.customcrafting.recipes.types.RecipeType;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.JsonNode;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.SmokingRecipe;

import java.util.stream.Collectors;

public class CustomSmokerRecipe extends CustomCookingRecipe<CustomSmokerRecipe, SmokingRecipe> {

    public CustomSmokerRecipe(NamespacedKey namespacedKey, JsonNode node) {
        super(namespacedKey, node);
    }

    public CustomSmokerRecipe() {
        super();
    }

    public CustomSmokerRecipe(CustomSmokerRecipe customSmokerRecipe) {
        super(customSmokerRecipe);
    }

    @Override
    public RecipeType<CustomSmokerRecipe> getRecipeType() {
        return RecipeType.SMOKER;
    }

    @Override
    public CustomSmokerRecipe clone() {
        return new CustomSmokerRecipe(this);
    }

    @Override
    public SmokingRecipe getVanillaRecipe() {
        RecipeChoice choice = isExactMeta() ? new RecipeChoice.ExactChoice(getSource().stream().map(CustomItem::create).collect(Collectors.toList())) : new RecipeChoice.MaterialChoice(getSource().stream().map(i -> i.create().getType()).collect(Collectors.toList()));
        return new SmokingRecipe(new org.bukkit.NamespacedKey(getNamespacedKey().getNamespace(), getNamespacedKey().getKey()), getResult().create(), choice, getExp(), getCookingTime());
    }
}
