package net.swofty.types.generic.minion.actions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import net.swofty.types.generic.item.SkyBlockItem;
import net.swofty.types.generic.minion.IslandMinionData;
import net.swofty.types.generic.minion.MinionAction;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@AllArgsConstructor
@Getter
public class MinionMinePumpkinOrMelonAction extends MinionAction {
    private final Block toMine;

    @Override
    public @NotNull List<SkyBlockItem> onAction(MinionActionEvent event, IslandMinionData.IslandMinion minion, Instance island) {
        return null;
    }

    @Override
    public boolean checkMaterials(IslandMinionData.IslandMinion minion, Instance island) {
        return false;
    }
}
