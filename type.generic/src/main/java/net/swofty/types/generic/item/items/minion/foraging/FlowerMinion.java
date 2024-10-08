package net.swofty.types.generic.item.items.minion.foraging;

import net.swofty.types.generic.item.ItemTypeLinker;
import net.swofty.types.generic.item.impl.CustomSkyBlockItem;
import net.swofty.types.generic.item.impl.Minion;
import net.swofty.types.generic.minion.MinionIngredient;
import net.swofty.types.generic.minion.MinionRegistry;

import java.util.List;

public class FlowerMinion implements CustomSkyBlockItem, Minion {
    @Override
    public MinionRegistry getMinionRegistry() {
        return MinionRegistry.FLOWER;
    }

    @Override
    public ItemTypeLinker getFirstBaseItem() {
        return ItemTypeLinker.WOODEN_HOE;
    }

    @Override
    public boolean isByDefaultCraftable() {
        return false;
    }

    @Override
    public List<MinionIngredient> getMinionCraftingIngredients() {
        return List.of(
                new MinionIngredient(ItemTypeLinker.DANDELION, 20),
                new MinionIngredient(ItemTypeLinker.DANDELION, 40),
                new MinionIngredient(ItemTypeLinker.DANDELION, 64),
                new MinionIngredient(ItemTypeLinker.ENCHANTED_DANDELION, 1),
                new MinionIngredient(ItemTypeLinker.ENCHANTED_DANDELION, 3),
                new MinionIngredient(ItemTypeLinker.ENCHANTED_DANDELION, 8),
                new MinionIngredient(ItemTypeLinker.ENCHANTED_DANDELION, 16),
                new MinionIngredient(ItemTypeLinker.ENCHANTED_DANDELION, 32),
                new MinionIngredient(ItemTypeLinker.ENCHANTED_DANDELION, 64),
                new MinionIngredient(ItemTypeLinker.ENCHANTED_POPPY, 1),
                new MinionIngredient(ItemTypeLinker.ENCHANTED_POPPY, 2)
        );
    }
}
