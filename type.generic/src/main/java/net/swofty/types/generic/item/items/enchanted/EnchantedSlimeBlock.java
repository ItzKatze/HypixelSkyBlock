package net.swofty.types.generic.item.items.enchanted;

import net.swofty.types.generic.item.ItemTypeLinker;
import net.swofty.types.generic.item.impl.DefaultCraftable;
import net.swofty.types.generic.item.impl.Enchanted;
import net.swofty.types.generic.item.impl.Sellable;
import net.swofty.types.generic.item.impl.SkyBlockRecipe;

public class EnchantedSlimeBlock implements Enchanted, Sellable, DefaultCraftable {
    @Override
    public double getSellValue() {
        return 128000;
    }

    @Override
    public SkyBlockRecipe<?> getRecipe() {
        return getStandardEnchantedRecipe(SkyBlockRecipe.RecipeType.COMBAT, ItemTypeLinker.ENCHANTED_SLIME_BALL);
    }
}