package me.wolfyscript.customcrafting.utils;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.Registry;
import me.wolfyscript.customcrafting.data.CCPlayerData;
import me.wolfyscript.customcrafting.handlers.DataHandler;
import me.wolfyscript.customcrafting.listeners.customevents.CustomPreCraftEvent;
import me.wolfyscript.customcrafting.recipes.CraftingRecipe;
import me.wolfyscript.customcrafting.recipes.conditions.Conditions;
import me.wolfyscript.customcrafting.recipes.conditions.CraftDelayCondition;
import me.wolfyscript.customcrafting.recipes.data.CraftingData;
import me.wolfyscript.customcrafting.utils.recipe_item.Result;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.util.RandomCollection;
import me.wolfyscript.utilities.util.inventory.InventoryUtils;
import me.wolfyscript.utilities.util.inventory.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class CraftManager {

    private final RecipeUtils recipeUtils;

    private final Map<UUID, CraftingData> preCraftedRecipes = new HashMap<>();
    private final CustomCrafting customCrafting;
    private final DataHandler dataHandler;

    public CraftManager(CustomCrafting customCrafting) {
        this.customCrafting = customCrafting;
        this.dataHandler = customCrafting.getDataHandler();
        this.recipeUtils = new RecipeUtils(this);
    }

    /**
     * Checks for a possible {@link CraftingRecipe} and returns the result ItemStack of the {@link CraftingRecipe} that is valid.
     *
     * @param matrix    The matrix of the crafting grid.
     * @param player    The player that executed the craft.
     * @param inventory The inventory this craft was called from.
     * @param elite     If the workstation is an Elite Crafting Table.
     * @param advanced  If the workstation is an Advanced Crafting Table.
     * @return The result ItemStack of the valid {@link CraftingRecipe}.
     */
    public ItemStack preCheckRecipe(ItemStack[] matrix, Player player, Inventory inventory, boolean elite, boolean advanced) {
        remove(player.getUniqueId());
        if (customCrafting.getConfigHandler().getConfig().isLockedDown()) {
            return null;
        }
        var matrixData = getIngredients(matrix);
        var targetBlock = inventory.getLocation() != null ? inventory.getLocation().getBlock() : player.getTargetBlockExact(5);
        return Registry.RECIPES.getSimilarCraftingRecipes(matrixData, elite, advanced).map(recipe -> checkRecipe(recipe, matrixData, player, targetBlock, inventory)).filter(Objects::nonNull).findFirst().orElse(null);
    }

    /**
     * Checks one single {@link CraftingRecipe} and returns the {@link CustomItem} if it's valid.
     *
     * @param recipe      The {@link CraftingRecipe} to check.
     * @param ingredients The ingredients of the matrix without surrounding empty columns/rows (via {@link DataHandler#getIngredients(ItemStack[])}).
     * @param player      The player that crafts it.
     * @param block       The block of the workstation or players inventory.
     * @param inventory   The inventory of the workstation or player.
     * @return The result {@link CustomItem} if the {@link CraftingRecipe} is valid. Else null.
     */
    @Nullable
    public ItemStack checkRecipe(CraftingRecipe<?> recipe, MatrixData flatMatrix, Player player, Block block, Inventory inventory) {
        if (!recipe.isDisabled() && recipe.checkConditions(new Conditions.Data(player, block, player.getOpenInventory()))) {
            var craftingData = recipe.check(flatMatrix);
            if (craftingData != null) {
                var customPreCraftEvent = new CustomPreCraftEvent(recipe, inventory, flatMatrix);
                Bukkit.getPluginManager().callEvent(customPreCraftEvent);
                if (!customPreCraftEvent.isCancelled()) {
                    Result result = customPreCraftEvent.getResult();
                    craftingData.setResult(result);
                    put(player.getUniqueId(), craftingData);
                    return result.getItem(craftingData, player, block);
                }
            }
        }
        return null; //No longer call Event if recipe is disabled or invalid!
    }

    /**
     * Consumes the active Recipe from the matrix and sets the correct item to the cursor.
     *
     * @param result The result {@link ItemStack} from the inventory.
     * @param matrix The matrix of the crafting grid. <strong>The {@link ItemStack}s of the matrix will be edited directly! It will not add new instances!</strong>
     * @param event  The {@link InventoryClickEvent} that caused this click.
     * @deprecated This method no longer uses the passed in matrix! Instead it will use the cached {@link CraftingData} created when {@link #preCheckRecipe(ItemStack[], Player, Inventory, boolean, boolean)} is called. Use {@link #consumeRecipe(ItemStack, InventoryClickEvent)} instead!
     */
    @Deprecated
    public void consumeRecipe(ItemStack result, ItemStack[] matrix, InventoryClickEvent event) {
        consumeRecipe(result, event);
    }

    /**
     * Consumes the active Recipe from the matrix and sets the correct item to the cursor.
     *
     * @param result The result {@link ItemStack} from the inventory.
     * @param event  The {@link InventoryClickEvent} that caused this click.
     */
    public void consumeRecipe(ItemStack result, InventoryClickEvent event) {
        var inventory = event.getClickedInventory();
        var player = (Player) event.getWhoClicked();
        if (inventory != null && !ItemUtils.isAirOrNull(result) && has(player.getUniqueId())) {
            var craftingData = preCraftedRecipes.get(player.getUniqueId());
            CraftingRecipe<?> recipe = craftingData.getRecipe();
            if (recipe != null && !ItemUtils.isAirOrNull(result)) {
                Result recipeResult = craftingData.getResult();
                editStatistics(player, inventory, recipe);
                setPlayerCraftTime(player, recipe);
                recipeResult.executeExtensions(inventory.getLocation() == null ? event.getWhoClicked().getLocation() : inventory.getLocation(), inventory.getLocation() != null, (Player) event.getWhoClicked());
                calculateClick(player, event, craftingData, recipe, recipeResult, result);
            }
            remove(event.getWhoClicked().getUniqueId());
        }
    }

    private void editStatistics(Player player, Inventory inventory, CraftingRecipe<?> recipe) {
        CCPlayerData playerStore = PlayerUtil.getStore(player);
        playerStore.increaseRecipeCrafts(recipe.getNamespacedKey(), 1);
        playerStore.increaseTotalCrafts(1);
        var customItem = NamespacedKeyUtils.getCustomItem(inventory.getLocation());
        if (customItem != null && customItem.getNamespacedKey().equals(CustomCrafting.ADVANCED_CRAFTING_TABLE)) {
            playerStore.increaseAdvancedCrafts(1);
        } else {
            playerStore.increaseNormalCrafts(1);
        }
    }

    private void setPlayerCraftTime(Player player, CraftingRecipe<?> recipe) {
        CraftDelayCondition condition = recipe.getConditions().getByType(CraftDelayCondition.class);
        if (condition != null && condition.getOption().equals(Conditions.Option.EXACT)) {
            condition.setPlayerCraftTime(player);
        }
    }

    private void calculateClick(Player player, InventoryClickEvent event, CraftingData craftingData, CraftingRecipe<?> recipe, Result recipeResult, ItemStack result) {
        int possible = event.isShiftClick() ? Math.min(InventoryUtils.getInventorySpace(player.getInventory(), result) / result.getAmount(), recipe.getAmountCraftable(craftingData)) : 1;
        recipe.removeMatrix(event.getClickedInventory(), possible, craftingData);
        if (event.isShiftClick()) {
            if (possible > 0) {
                RandomCollection<CustomItem> results = recipeResult.getRandomChoices(player);
                for (int i = 0; i < possible; i++) {
                    var customItem = results.next();
                    if (customItem != null) {
                        player.getInventory().addItem(recipeResult.getItem(craftingData, customItem, player, null));
                    }
                }
            }
            return;
        }
        ItemStack cursor = event.getCursor();
        if (ItemUtils.isAirOrNull(cursor) || (result.isSimilar(cursor) && cursor.getAmount() + result.getAmount() <= cursor.getMaxStackSize())) {
            if (ItemUtils.isAirOrNull(cursor)) {
                event.setCursor(result);
            } else {
                cursor.setAmount(cursor.getAmount() + result.getAmount());
            }
            recipeResult.removeCachedItem(player);
        }
    }

    /**
     * Sets the current active {@link CraftingData} for the player.
     *
     * @param uuid         The {@link UUID} of the player.
     * @param craftingData The {@link CraftingData} of the latest check.
     */
    public void put(UUID uuid, CraftingData craftingData) {
        preCraftedRecipes.put(uuid, craftingData);
    }

    /**
     * Removes the active CustomRecipe of the specified player.
     *
     * @param uuid The UUID of the player.
     */
    public void remove(UUID uuid) {
        preCraftedRecipes.remove(uuid);
    }

    /**
     * @param uuid The uuid of the player.
     * @return If the player has an active CustomRecipe.
     */
    public boolean has(UUID uuid) {
        return preCraftedRecipes.containsKey(uuid);
    }

    /**
     * @return The old deprecated RecipeUtils!
     */
    @Deprecated
    public RecipeUtils getRecipeUtils() {
        return recipeUtils;
    }

    private int gridSize(ItemStack[] ingredients) {
        return switch (ingredients.length) {
            case 9 -> 3;
            case 16 -> 4;
            case 25 -> 5;
            case 36 -> 6;
            default -> (int) Math.sqrt(ingredients.length);
        };
    }

    public MatrixData getIngredients(ItemStack[] ingredients) {
        List<List<ItemStack>> items = new ArrayList<>();
        int gridSize = gridSize(ingredients);
        for (int y = 0; y < gridSize; y++) {
            items.add(new ArrayList<>(Arrays.asList(ingredients).subList(y * gridSize, gridSize + y * gridSize)));
        }
        ListIterator<List<ItemStack>> iterator = items.listIterator();
        while (iterator.hasNext()) {
            if (!iterator.next().parallelStream().allMatch(Objects::isNull)) break;
            iterator.remove();
        }
        iterator = items.listIterator(items.size());
        while (iterator.hasPrevious()) {
            if (!iterator.previous().parallelStream().allMatch(Objects::isNull)) break;
            iterator.remove();
        }
        var leftPos = gridSize;
        var rightPos = 0;
        for (List<ItemStack> itemsY : items) {
            var size = itemsY.size();
            for (int i = 0; i < size; i++) {
                if (itemsY.get(i) != null) {
                    leftPos = Math.min(leftPos, i);
                    break;
                }
            }
            if (leftPos == 0) break;
        }
        for (List<ItemStack> itemsY : items) {
            var size = itemsY.size();
            for (int i = size - 1; i > 0; i--) {
                if (itemsY.get(i) != null) {
                    rightPos = Math.max(rightPos, i);
                    break;
                }
            }
            if (rightPos == gridSize) break;
        }
        var finalLeftPos = leftPos;
        var finalRightPos = rightPos + 1;
        return new MatrixData(items.stream().flatMap(itemStacks -> itemStacks.subList(finalLeftPos, finalRightPos).stream()).toArray(ItemStack[]::new), items.size(), finalRightPos - finalLeftPos);
    }

    public static class MatrixData {

        private final ItemStack[] matrix;
        private final int height;
        private final int width;
        private final long strippedSize;

        public MatrixData(ItemStack[] matrix, int height, int width) {
            this.matrix = matrix;
            this.height = height;
            this.width = width;
            this.strippedSize = Arrays.stream(matrix).filter(itemStack -> !ItemUtils.isAirOrNull(itemStack)).count();
        }

        public int getHeight() {
            return height;
        }

        public int getWidth() {
            return width;
        }

        public long getStrippedSize() {
            return strippedSize;
        }

        public ItemStack[] getMatrix() {
            return matrix;
        }

        @Override
        public String toString() {
            return "MatrixData{" +
                    "matrix=" + Arrays.toString(matrix) +
                    ", height=" + height +
                    ", width=" + width +
                    '}';
        }
    }

}