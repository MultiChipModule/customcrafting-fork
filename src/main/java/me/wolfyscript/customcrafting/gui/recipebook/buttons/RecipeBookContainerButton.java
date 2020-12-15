package me.wolfyscript.customcrafting.gui.recipebook.buttons;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.TestCache;
import me.wolfyscript.customcrafting.data.cache.KnowledgeBook;
import me.wolfyscript.customcrafting.handlers.RecipeHandler;
import me.wolfyscript.customcrafting.recipes.types.ICustomRecipe;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.gui.GuiHandler;
import me.wolfyscript.utilities.api.inventory.gui.GuiWindow;
import me.wolfyscript.utilities.api.inventory.gui.button.Button;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class RecipeBookContainerButton extends Button<TestCache> {

    private final HashMap<GuiHandler<?>, CustomItem> recipes = new HashMap<>();
    private final CustomCrafting customCrafting;

    public RecipeBookContainerButton(int slot, CustomCrafting customCrafting) {
        super("recipe_book.container_" + slot, null);
        this.customCrafting = customCrafting;
    }


    @Override
    public void init(GuiWindow guiWindow) {
    }

    @Override
    public void init(String s, WolfyUtilities wolfyUtilities) {
    }

    @Override
    public void postExecute(GuiHandler<TestCache> guiHandler, Player player, Inventory inventory, ItemStack itemStack, int i, InventoryInteractEvent inventoryInteractEvent) throws IOException {

    }

    @Override
    public void prepareRender(GuiHandler<TestCache> guiHandler, Player player, Inventory inventory, ItemStack itemStack, int i, boolean b) {

    }

    @Override
    public boolean execute(GuiHandler<TestCache> guiHandler, Player player, Inventory inventory, int slot, InventoryClickEvent event) {
        TestCache cache = guiHandler.getCustomCache();
        RecipeHandler recipeHandler = customCrafting.getRecipeHandler();
        KnowledgeBook book = cache.getKnowledgeBook();
        CustomItem customItem = getRecipeItem(guiHandler);
        List<ICustomRecipe<?>> recipes = recipeHandler.getAvailableRecipesBySimilarResult(customItem.create(), player);
        recipes.remove(book.getCurrentRecipe());
        if (!recipes.isEmpty()) {
            book.setSubFolder(1);
            book.setSubFolderPage(0);
            book.getResearchItems().add(customItem);
            book.setSubFolderRecipes(recipes);
            book.applyRecipeToButtons(guiHandler, recipes.get(0));
        }
        return true;
    }

    @Override
    public void render(GuiHandler<TestCache> guiHandler, Player player, Inventory inventory, int slot, boolean help) {
        inventory.setItem(slot, getRecipeItem(guiHandler).create(1));
    }

    public CustomItem getRecipeItem(GuiHandler<TestCache> guiHandler) {
        return recipes.getOrDefault(guiHandler, null);
    }

    public void setRecipeItem(GuiHandler<TestCache> guiHandler, CustomItem item) {
        recipes.put(guiHandler, item);
    }
}
