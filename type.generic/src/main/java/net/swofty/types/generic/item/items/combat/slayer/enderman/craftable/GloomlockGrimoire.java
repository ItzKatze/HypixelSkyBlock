package net.swofty.types.generic.item.items.combat.slayer.enderman.craftable;

import net.swofty.types.generic.item.ItemTypeLinker;
import net.swofty.types.generic.item.ItemQuantifiable;
import net.swofty.types.generic.item.SkyBlockItem;
import net.swofty.types.generic.item.impl.*;
import net.swofty.types.generic.item.impl.recipes.ShapedRecipe;
import net.swofty.types.generic.user.SkyBlockPlayer;
import net.swofty.commons.statistics.ItemStatistics;

import java.util.*;
import net.swofty.commons.item.ItemType;

public class GloomlockGrimoire implements CustomSkyBlockItem, DefaultCraftable, Enchanted, NotFinishedYet {
    @Override
    public SkyBlockRecipe<?> getRecipe() {
        Map<Character, ItemQuantifiable> ingredientMap = new HashMap<>();
        ingredientMap.put('A', new ItemQuantifiable(ItemTypeLinker.NULL_OVOID, 4));
        ingredientMap.put('B', new ItemQuantifiable(ItemTypeLinker.ENCHANTED_GUNPOWDER, 64));
        ingredientMap.put('C', new ItemQuantifiable(ItemTypeLinker.ENCHANTED_BONE_BLOCK, 4));
        ingredientMap.put('D', new ItemQuantifiable(ItemType.BOOK, 1));
        List<String> pattern = List.of(
                "ABA",
                "CDC",
                "ABA");

        return new ShapedRecipe(SkyBlockRecipe.RecipeType.VOIDGLOOM_SERAPH, new SkyBlockItem(ItemTypeLinker.GLOOMLOCK_GRIMOIRE), ingredientMap, pattern);
    }

    @Override
    public ItemStatistics getStatistics(SkyBlockItem instance) {
        return ItemStatistics.empty();
    }

    @Override
    public ArrayList<String> getLore(SkyBlockPlayer player, SkyBlockItem item) {
        return new ArrayList<>(Arrays.asList(
                "§6Ability: Life Tap §e§lLEFT CLICK",
                "§7Convert §3§315⸎ Soulflow §7to gain up to",
                "§7§3600ʬ Overflow§7, but lose §c25% ❤§7.",
                "§8Cooldown: §a1s",
                "",
                "§6Ability: Extreme Measures §e§lRIGHT CLICK",
                "§7Costs §3300ʬ Overflow§7. Heal for",
                "§7§c40% ❤ §7but deal §c-8% §7damage for",
                "§740 seconds."));
    }
}
