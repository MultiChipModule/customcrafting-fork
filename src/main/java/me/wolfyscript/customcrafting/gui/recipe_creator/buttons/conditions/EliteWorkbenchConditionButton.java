package me.wolfyscript.customcrafting.gui.recipe_creator.buttons.conditions;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.configs.custom_data.EliteWorkbenchData;
import me.wolfyscript.customcrafting.data.TestCache;
import me.wolfyscript.customcrafting.recipes.Conditions;
import me.wolfyscript.customcrafting.recipes.conditions.EliteWorkbenchCondition;
import me.wolfyscript.customcrafting.recipes.types.ICustomRecipe;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItems;
import me.wolfyscript.utilities.api.inventory.custom_items.api_references.WolfyUtilitiesRef;
import me.wolfyscript.utilities.api.inventory.gui.GuiWindow;
import me.wolfyscript.utilities.api.inventory.gui.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ActionButton;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.Material;

public class EliteWorkbenchConditionButton extends ActionButton<TestCache> {

    public EliteWorkbenchConditionButton() {
        super("conditions.elite_workbench", new ButtonState<>("elite_workbench", Material.CRAFTING_TABLE, (guiHandler, player, inventory, slot, event) -> {
            GuiWindow<TestCache> window = guiHandler.getCurrentInv();
            ICustomRecipe recipeConfig = guiHandler.getCustomCache().getRecipe();
            Conditions conditions = recipeConfig.getConditions();
            if (event.getClick().isRightClick()) {
                //Change Mode
                conditions.getByID("elite_workbench").toggleOption();
                recipeConfig.setConditions(conditions);
            } else if (!event.isShiftClick()) {
                //CONFIGURE ELITE WORKBENCHES
                window.openChat("elite_workbench", guiHandler, (guiHandler1, player1, s, args) -> {
                    if (args.length > 1) {
                        CustomItem customItem = CustomItems.getCustomItem(new NamespacedKey(args[0], args[1]));
                        if (customItem == null || !(customItem.getApiReference() instanceof WolfyUtilitiesRef)) {
                            window.sendMessage(player1, "error");
                            return true;
                        }
                        NamespacedKey namespacedKey = ((WolfyUtilitiesRef) customItem.getApiReference()).getNamespacedKey();
                        EliteWorkbenchData data = (EliteWorkbenchData) customItem.getCustomData("elite_workbench");
                        if (!data.isEnabled()) {
                            window.sendMessage(player1, "not_elite_workbench");
                            return true;
                        }
                        EliteWorkbenchCondition condition = (EliteWorkbenchCondition) conditions.getByID("elite_workbench");
                        if (condition.getEliteWorkbenches().contains(namespacedKey.toString())) {
                            window.sendMessage(player1, "already_existing");
                            return true;
                        }
                        ((EliteWorkbenchCondition) conditions.getByID("elite_workbench")).addEliteWorkbenches(namespacedKey.toString());
                        recipeConfig.setConditions(conditions);
                        return false;
                    }
                    window.sendMessage(player1, "no_name");
                    return true;
                });
            } else {
                if (((EliteWorkbenchCondition) conditions.getByID("elite_workbench")).getEliteWorkbenches().size() > 0) {
                    ((EliteWorkbenchCondition) conditions.getByID("elite_workbench")).getEliteWorkbenches().remove(((EliteWorkbenchCondition) conditions.getByID("elite_workbench")).getEliteWorkbenches().size() - 1);
                    recipeConfig.setConditions(conditions);
                }
                return false;
            }
            return true;
        }, (hashMap, guiHandler, player, itemStack, slot, b) -> {
            EliteWorkbenchCondition condition = (EliteWorkbenchCondition) guiHandler.getCustomCache().getRecipe().getConditions().getByID("elite_workbench");
            hashMap.put("%MODE%", condition.getOption().getDisplayString(CustomCrafting.getApi()));
            for (int i = 0; i < 4; i++) {
                if (i < condition.getEliteWorkbenches().size()) {
                    hashMap.put("%var" + i + "%", condition.getEliteWorkbenches().get(i));
                } else {
                    hashMap.put("%var" + i + "%", "...");
                }
            }
            return itemStack;
        }));
    }
}
