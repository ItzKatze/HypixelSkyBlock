package net.swofty.types.generic.item.items.enchanted;

import net.swofty.types.generic.item.impl.Enchanted;
import net.swofty.types.generic.item.impl.Sellable;

public class EnchantedEmeraldBlock implements Enchanted, Sellable {
    @Override
    public double getSellValue() {
        return 102400;
    }

}