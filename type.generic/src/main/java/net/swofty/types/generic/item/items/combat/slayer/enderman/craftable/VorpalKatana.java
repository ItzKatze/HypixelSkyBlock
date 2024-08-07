package net.swofty.types.generic.item.items.combat.slayer.enderman.craftable;

import net.swofty.types.generic.gems.Gemstone;
import net.swofty.types.generic.item.ItemTypeLinker;
import net.swofty.types.generic.item.ItemQuantifiable;
import net.swofty.types.generic.item.SkyBlockItem;
import net.swofty.types.generic.item.impl.*;
import net.swofty.types.generic.item.impl.recipes.ShapedRecipe;
import net.swofty.types.generic.user.SkyBlockPlayer;
import net.swofty.commons.statistics.ItemStatistic;
import net.swofty.commons.statistics.ItemStatistics;

import java.util.*;
import net.swofty.commons.item.ItemType;

public class VorpalKatana implements CustomSkyBlockItem, DefaultCraftable, StandardItem, GemstoneItem, NotFinishedYet {
    @Override
    public SkyBlockRecipe<?> getRecipe() {
        Map<Character, ItemQuantifiable> ingredientMap = new HashMap<>();
        ingredientMap.put('A', new ItemQuantifiable(ItemTypeLinker.NULL_EDGE, 1));
        ingredientMap.put('B', new ItemQuantifiable(ItemTypeLinker.NULL_OVOID, 8));
        ingredientMap.put('C', new ItemQuantifiable(ItemTypeLinker.REFINED_MITHRIL, 4));
        ingredientMap.put('D', new ItemQuantifiable(ItemTypeLinker.VOIDWALKER_KATANA, 1));
        ingredientMap.put(' ', new ItemQuantifiable(ItemType.AIR, 1));
        List<String> pattern = List.of(
                " A ",
                " B ",
                "CDC");

        return new ShapedRecipe(SkyBlockRecipe.RecipeType.VOIDGLOOM_SERAPH, new SkyBlockItem(ItemTypeLinker.VORPAL_KATANA), ingredientMap, pattern);
    }

    @Override
    public ItemStatistics getStatistics(SkyBlockItem instance) {
        return ItemStatistics.builder()
                .withBase(ItemStatistic.DAMAGE, 155D)
                .withBase(ItemStatistic.STRENGTH, 80D)
                .withBase(ItemStatistic.CRIT_DAMAGE, 25D)
                .withBase(ItemStatistic.INTELLIGENCE, 200D)
                .build();
    }

    @Override
    public ArrayList<String> getLore(SkyBlockPlayer player, SkyBlockItem item) {
        return new ArrayList<>(Arrays.asList(
                "§7Deal §a+250% §7damage to Endermen§7.",
                "§7§7Receive §a9% §7less damage",
                "§7from Endermen§7 when held.",
                "",
                "§6Ability: Soulcry §e§lRIGHT CLICK",
                "§7Gain §c+300⫽ Ferocity §7against",
                "§7Endermen for §a4s",
                "§7§8Soulflow Cost: §31",
                "§8Mana Cost: §3200",
                "§8Cooldown: §a4s"));
    }

    @Override
    public List<GemstoneItemSlot> getGemstoneSlots() {
        return List.of(
                new GemstoneItemSlot(Gemstone.Slots.JASPER, 50000),
                new GemstoneItemSlot(Gemstone.Slots.SAPPHIRE, 100000)
        );
    }

    @Override
    public StandardItemType getStandardItemType() {
        return StandardItemType.SWORD;
    }
}
