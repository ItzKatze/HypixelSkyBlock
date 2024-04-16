package net.swofty.types.generic.item.set.sets;

import net.minestom.server.potion.Potion;
import net.minestom.server.potion.PotionEffect;
import net.swofty.types.generic.event.value.SkyBlockValueEvent;
import net.swofty.types.generic.event.value.ValueUpdateEvent;
import net.swofty.types.generic.event.value.events.MiningValueUpdateEvent;
import net.swofty.types.generic.item.set.impl.ArmorSet;
import net.swofty.types.generic.item.set.impl.SetEvents;
import net.swofty.types.generic.user.SkyBlockPlayer;

public class MinerOutfitSet implements ArmorSet, SetEvents {

    @Override
    public String getName() {
        return "Haste";
    }

    @Override
    public String getDescription() {
        return "§7Grants the wearer with §apermanent Haste II §7while worn.";
    }

    @Override
    public void setPutOn(SkyBlockPlayer player) {
        player.addEffect(new Potion(PotionEffect.HASTE, (byte) 2 , 1000000));
    }

    @Override
    public void setTakeOff(SkyBlockPlayer player) {
        player.removeEffect(PotionEffect.HASTE);
    }
}