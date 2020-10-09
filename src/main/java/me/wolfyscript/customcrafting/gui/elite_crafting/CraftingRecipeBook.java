package me.wolfyscript.customcrafting.gui.elite_crafting;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.configs.recipebook.Category;
import me.wolfyscript.customcrafting.data.PlayerStatistics;
import me.wolfyscript.customcrafting.data.TestCache;
import me.wolfyscript.customcrafting.data.cache.EliteWorkbench;
import me.wolfyscript.customcrafting.data.cache.KnowledgeBook;
import me.wolfyscript.customcrafting.gui.ExtendedGuiWindow;
import me.wolfyscript.customcrafting.gui.recipebook.buttons.IngredientContainerButton;
import me.wolfyscript.customcrafting.gui.recipebook.buttons.ItemCategoryButton;
import me.wolfyscript.customcrafting.gui.recipebook.buttons.RecipeBookContainerButton;
import me.wolfyscript.customcrafting.recipes.Conditions;
import me.wolfyscript.customcrafting.recipes.conditions.EliteWorkbenchCondition;
import me.wolfyscript.customcrafting.recipes.types.ICustomRecipe;
import me.wolfyscript.customcrafting.recipes.types.RecipeType;
import me.wolfyscript.customcrafting.recipes.types.elite_workbench.EliteCraftingRecipe;
import me.wolfyscript.customcrafting.recipes.types.elite_workbench.ShapedEliteCraftRecipe;
import me.wolfyscript.utilities.api.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.GuiHandler;
import me.wolfyscript.utilities.api.inventory.GuiUpdate;
import me.wolfyscript.utilities.api.inventory.InventoryAPI;
import me.wolfyscript.utilities.api.inventory.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.button.buttons.ActionButton;
import me.wolfyscript.utilities.api.inventory.button.buttons.DummyButton;
import me.wolfyscript.utilities.api.utils.inventory.PlayerHeadUtils;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class CraftingRecipeBook extends ExtendedGuiWindow {

    public CraftingRecipeBook(InventoryAPI inventoryAPI, CustomCrafting customCrafting) {
        super("recipe_book", inventoryAPI, 54, customCrafting);
    }

    @Override
    public void onInit() {
        registerButton(new ActionButton("back", new ButtonState("none", "back", PlayerHeadUtils.getViaURL("864f779a8e3ffa231143fa69b96b14ee35c16d669e19c75fd1a7da4bf306c"), (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            TestCache cache = (TestCache) guiHandler.getCustomCache();
            KnowledgeBook book = cache.getKnowledgeBook();
            book.stopTimerTask();
            IngredientContainerButton.resetButtons(guiHandler);
            book.setRecipeItems(new ArrayList<>());
            if (book.getSubFolder() == 0) {
                guiHandler.openPreviousInv();
            } else {
                book.stopTimerTask();
                book.getResearchItems().remove(book.getSubFolder() - 1);
                book.setSubFolder(book.getSubFolder() - 1);
                if (book.getSubFolder() > 0) {
                    CustomItem item = book.getResearchItem();
                    book.setSubFolderRecipes(customCrafting.getRecipeHandler().getRecipes(item));
                    book.applyRecipeToButtons(guiHandler, book.getSubFolderRecipes().get(0));
                    return true;
                } else {
                    book.setSubFolderRecipes(new ArrayList<>());
                }
            }
            return true;
        })));
        for (int i = 0; i < 45; i++) {
            registerButton(new RecipeBookContainerButton(i, customCrafting));
        }
        registerButton(new DummyButton("workbench.shapeless_on", PlayerHeadUtils.getViaURL("f21d93da43863cb3759afefa9f7cc5c81f34d920ca97b7283b462f8b197f813")));
        registerButton(new DummyButton("workbench.shapeless_off", PlayerHeadUtils.getViaURL("1aae7e8222ddbee19d184b97e79067814b6ba3142a3bdcce8b93099a312")));
    }

    @Override
    public void onUpdateAsync(GuiUpdate event) {
        super.onUpdateAsync(event);
        event.setButton(0, "back");
        GuiHandler<TestCache> guiHandler = event.getGuiHandler(TestCache.class);
        Player player = event.getPlayer();
        TestCache cache = guiHandler.getCustomCache();
        PlayerStatistics playerStatistics = CustomCrafting.getPlayerStatistics(player);
        EliteWorkbench eliteWorkbenchData = cache.getEliteWorkbench();
        KnowledgeBook knowledgeBook = cache.getKnowledgeBook();

        Category category = ((ItemCategoryButton) api.getInventoryAPI().getGuiCluster("recipe_book").getButton("itemCategory")).getCategory(guiHandler);
        if (knowledgeBook.getSubFolder() == 0) {
            event.setButton(0, "back");
            event.setButton(2, "recipe_book", "previous_page");
            event.setButton(4, "recipe_book", "itemCategory");
            event.setButton(6, "recipe_book", "next_page");
            if (knowledgeBook.getRecipeItems().isEmpty()) {

                List<ICustomRecipe<?>> recipes = new ArrayList<>(customCrafting.getRecipeHandler().getAvailableRecipes(RecipeType.ELITE_WORKBENCH, player));

                Iterator<ICustomRecipe<?>> iterator = recipes.iterator();
                while (iterator.hasNext()) {
                    EliteCraftingRecipe recipe = (EliteCraftingRecipe) iterator.next();
                    if (!recipe.getConditions().getByID("elite_workbench").getOption().equals(Conditions.Option.IGNORE)) {
                        if (!((EliteWorkbenchCondition) recipe.getConditions().getByID("elite_workbench")).getEliteWorkbenches().contains(eliteWorkbenchData.getEliteWorkbenchData().getId())) {
                            iterator.remove();
                            continue;
                        }
                    }
                    if (recipe.isShapeless()) {
                        if (recipe.getIngredients().size() > eliteWorkbenchData.getCurrentGridSize() * eliteWorkbenchData.getCurrentGridSize()) {
                            iterator.remove();
                        }
                    } else {
                        ShapedEliteCraftRecipe recipe1 = (ShapedEliteCraftRecipe) recipe;
                        if (recipe1.getShape().length > eliteWorkbenchData.getCurrentGridSize() || recipe1.getShape()[0].length() > eliteWorkbenchData.getCurrentGridSize()) {
                            iterator.remove();
                        }
                    }
                }
                EliteWorkbench eliteWorkbench = cache.getEliteWorkbench();
                if (eliteWorkbench.getEliteWorkbenchData().isAdvancedRecipes()) {
                    recipes.addAll(customCrafting.getRecipeHandler().getAvailableRecipes(RecipeType.WORKBENCH, player));
                }
                if (category != null) {
                    Iterator<ICustomRecipe<?>> recipeIterator = recipes.iterator();
                    while (recipeIterator.hasNext()) {
                        ICustomRecipe<?> recipe = recipeIterator.next();
                        List<CustomItem> customItems = recipe.getResults();
                        if (!category.isValid(recipe) && customItems.stream().noneMatch(customItem -> category.isValid(customItem.getItemStack().getType()))) {
                            recipeIterator.remove();
                        }
                    }
                }
                List<CustomItem> recipeItems = new ArrayList<>();
                recipes.stream().map(ICustomRecipe::getResults).forEach(items -> recipeItems.addAll(items.stream().filter(item -> !recipeItems.contains(item)).collect(Collectors.toList())));
                knowledgeBook.setRecipeItems(recipeItems);
            }

            List<CustomItem> recipeItems = knowledgeBook.getRecipeItems();
            int maxPages = recipeItems.size() / 45 + (recipeItems.size() % 45 > 0 ? 1 : 0);
            if (knowledgeBook.getPage() >= maxPages) {
                knowledgeBook.setPage(0);
            }
            if (knowledgeBook.getPage() != 0) {
                event.setButton(2, "recipe_book", "previous_page");
            }
            if (knowledgeBook.getPage() + 1 < maxPages) {
                event.setButton(6, "recipe_book", "next_page");
            }
            int item = 0;
            for (int i = 45 * knowledgeBook.getPage(); item < 45 && i < recipeItems.size(); i++) {
                RecipeBookContainerButton button = (RecipeBookContainerButton) getButton("recipe_book.container_" + item);
                button.setRecipeItem(event.getGuiHandler(), recipeItems.get(i));
                event.setButton(9 + item, button);
                item++;
            }
        } else {
            List<ICustomRecipe<?>> recipes = knowledgeBook.getSubFolderRecipes();
            for (int i = 1; i < 9; i++) {
                event.setButton(i, "none", playerStatistics.getDarkMode() ? "glass_gray" : "glass_white");
            }
            for (int i = 36; i < 45; i++) {
                event.setButton(i, "none", playerStatistics.getDarkMode() ? "glass_gray" : "glass_white");
            }
            event.setButton(0, "back");
            int maxPages = recipes.size();
            if (knowledgeBook.getSubFolderPage() >= maxPages) {
                knowledgeBook.setSubFolderPage(0);
            }

            ICustomRecipe customRecipe = recipes.get(knowledgeBook.getSubFolderPage());
            if (customRecipe instanceof EliteCraftingRecipe) {
                if (knowledgeBook.getSubFolderPage() > 0) {
                    event.setButton(51, "previous_recipe");
                }
                event.setButton(52, "recipe_book", "back_to_list");
                if (knowledgeBook.getSubFolderPage() + 1 < recipes.size()) {
                    event.setButton(53, "next_recipe");
                }
            } else {
                if (knowledgeBook.getSubFolderPage() > 0) {
                    event.setButton(48, "previous_recipe");
                }
                event.setButton(49, "recipe_book", "back_to_list");
                if (knowledgeBook.getSubFolderPage() + 1 < recipes.size()) {
                    event.setButton(50, "next_recipe");
                }
            }
            customRecipe.renderMenu(this, event);
        }
    }
}
