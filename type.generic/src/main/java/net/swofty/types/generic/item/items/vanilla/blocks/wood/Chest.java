package net.swofty.types.generic.item.items.vanilla.blocks.wood;

import net.swofty.types.generic.block.BlockType;
import net.swofty.types.generic.item.ItemTypeLinker;
import net.swofty.types.generic.item.ItemQuantifiable;
import net.swofty.types.generic.item.SkyBlockItem;
import net.swofty.types.generic.item.impl.*;
import net.swofty.types.generic.item.impl.recipes.ShapedRecipe;
import net.swofty.commons.statistics.ItemStatistics;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.swofty.commons.item.ItemType;

public class Chest implements PlaceableCustomSkyBlockItem, DefaultCraftable, Sellable {
    @Override
    public ItemStatistics getStatistics(SkyBlockItem instance) {
        return ItemStatistics.empty();
    }

    @Override
    public @Nullable BlockType getAssociatedBlockType() {
        return BlockType.CHEST;
    }

    @Override
    public SkyBlockRecipe<?> getRecipe() {
        Map<Character, ItemQuantifiable> ingredientMap = new HashMap<>();
        ingredientMap.put('A', new ItemQuantifiable(ItemTypeLinker.OAK_PLANKS, 1));
        ingredientMap.put(' ', new ItemQuantifiable(ItemType.AIR, 1));
        List<String> pattern = List.of(
                "AAA",
                "A A",
                "AAA");
        return new ShapedRecipe(SkyBlockRecipe.RecipeType.NONE, new SkyBlockItem(ItemTypeLinker.CHEST), ingredientMap, pattern);
    }

    @Override
    public double getSellValue() {
        return 2;
    }
}
