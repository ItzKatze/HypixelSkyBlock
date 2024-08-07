package net.swofty.types.generic.item.items.accessories.dungeon.treasure;

import net.swofty.types.generic.item.ItemTypeLinker;
import net.swofty.types.generic.item.ItemQuantifiable;
import net.swofty.types.generic.item.SkyBlockItem;
import net.swofty.types.generic.item.impl.*;
import net.swofty.types.generic.item.impl.recipes.ShapedRecipe;
import net.swofty.types.generic.user.SkyBlockPlayer;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TreasureRing implements TieredTalisman, NotFinishedYet, DefaultCraftable, SkullHead {
    @Override
    public List<String> getTalismanDisplay() {
        return List.of("§7Grants §a+2% §7extra loot to end",
                "§7of dungeon chests.");
    }

    @Override
    public String getSkullTexture(@Nullable SkyBlockPlayer player, SkyBlockItem item) {
        return "6a1cc5525a217a399b5b86c32f0f22dd91378874b5f44d5a383e18bc0f3bc301";
    }

    @Override
    public SkyBlockRecipe<?> getRecipe() {
        Map<Character, ItemQuantifiable> ingredientMap = new HashMap<>();
        ingredientMap.put('A', new ItemQuantifiable(ItemTypeLinker.TREASURE_TALISMAN, 1));
        List<String> pattern = List.of(
                "AAA",
                "A A",
                "AAA"
        );
        return new ShapedRecipe(SkyBlockRecipe.RecipeType.NONE, new SkyBlockItem(ItemTypeLinker.TREASURE_RING), ingredientMap, pattern);
    }

    @Override
    public ItemTypeLinker getBaseTalismanTier() {
        return ItemTypeLinker.TREASURE_TALISMAN;
    }

    @Override
    public Integer getTier() {
        return 2;
    }
}
