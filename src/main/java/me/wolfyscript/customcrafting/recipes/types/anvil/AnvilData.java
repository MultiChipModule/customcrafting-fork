package me.wolfyscript.customcrafting.recipes.types.anvil;


import me.wolfyscript.customcrafting.utils.recipe_item.Result;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class AnvilData {

    private final CustomAnvilRecipe recipe;
    private final CustomItem inputLeft;
    private final CustomItem inputRight;
    private final Optional<Result<?>> result;

    public AnvilData(CustomAnvilRecipe recipe, @Nullable Result<?> result, CustomItem inputLeft, CustomItem inputRight) {
        this.recipe = recipe;
        this.inputLeft = inputLeft;
        this.inputRight = inputRight;
        this.result = Optional.ofNullable(result);
    }

    public CustomAnvilRecipe getRecipe() {
        return recipe;
    }

    public CustomItem getInputLeft() {
        return inputLeft;
    }

    public CustomItem getInputRight() {
        return inputRight;
    }

    public Optional<Result<?>> getResult() {
        return result;
    }
}
