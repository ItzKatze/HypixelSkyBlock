package net.swofty.types.generic.entity.mob;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityCreature;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.ai.GoalSelector;
import net.minestom.server.entity.ai.TargetSelector;
import net.minestom.server.entity.attribute.Attribute;
import net.minestom.server.entity.damage.Damage;
import net.minestom.server.instance.Instance;
import net.minestom.server.timer.Scheduler;
import net.minestom.server.timer.TaskSchedule;
import net.swofty.commons.item.ItemType;
import net.swofty.commons.statistics.ItemStatistic;
import net.swofty.commons.statistics.ItemStatistics;
import net.swofty.types.generic.SkyBlockConst;
import net.swofty.types.generic.SkyBlockGenericLoader;
import net.swofty.types.generic.entity.DroppedItemEntityImpl;
import net.swofty.types.generic.entity.mob.impl.RegionPopulator;
import net.swofty.types.generic.event.SkyBlockEventHandler;
import net.swofty.types.generic.event.custom.PlayerKilledSkyBlockMobEvent;
import net.swofty.types.generic.item.SkyBlockItem;
import net.swofty.types.generic.item.components.ArmorComponent;
import net.swofty.types.generic.loottable.LootAffector;
import net.swofty.types.generic.loottable.OtherLoot;
import net.swofty.types.generic.loottable.SkyBlockLootTable;
import net.swofty.types.generic.region.RegionType;
import net.swofty.types.generic.region.SkyBlockRegion;
import net.swofty.types.generic.skill.SkillCategories;
import net.swofty.types.generic.user.SkyBlockPlayer;
import net.swofty.types.generic.utility.MathUtility;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Setter
public abstract class SkyBlockMob extends EntityCreature {
    @Getter
    private static List<SkyBlockMob> mobs = new ArrayList<>();
    @Getter
    private long lastAttack = System.currentTimeMillis();
    @Getter
    private boolean hasBeenDamaged = false;

    public SkyBlockMob(EntityType entityType) {
        super(entityType);
        init();
    }

    private void init() {
        this.setCustomNameVisible(true);

        this.getAttribute(Attribute.MAX_HEALTH)
                .setBaseValue(getBaseStatistics().getOverall(ItemStatistic.HEALTH).floatValue());
        this.getAttribute(Attribute.MOVEMENT_SPEED)
                .setBaseValue((float) ((getBaseStatistics().getOverall(ItemStatistic.SPEED).floatValue() / 1000) * 2.5));
        this.setHealth(getBaseStatistics().getOverall(ItemStatistic.HEALTH).floatValue());

        this.setCustomName(Component.text(
                "§8[§7Lv" + getLevel() + "§8] §c" + getDisplayName()
                        + " §a" + Math.round(getHealth())
                        + "§f/§a"
                        + Math.round(getBaseStatistics().getOverall(ItemStatistic.HEALTH).floatValue())
        ));

        setAutoViewable(true);
        setAutoViewEntities(true);
        this.addAIGroup(getGoalSelectors(), getTargetSelectors());
    }

    @Override
    public void spawn() {
        super.spawn();
        mobs.add(this);
    }

    public abstract String getDisplayName();
    public abstract Integer getLevel();
    public abstract List<GoalSelector> getGoalSelectors();
    public abstract List<TargetSelector> getTargetSelectors();
    public abstract ItemStatistics getBaseStatistics();
    public abstract @Nullable SkyBlockLootTable getLootTable();
    public abstract SkillCategories getSkillCategory();
    public abstract long damageCooldown();
    public abstract OtherLoot getOtherLoot();

    public ItemStatistics getStatistics() {
        ItemStatistics statistics = getBaseStatistics().clone();
        ItemStatistics toSubtract = ItemStatistics.builder()
            .withBase(ItemStatistic.HEALTH, (double) getHealth())
            .build();

        return statistics.sub(toSubtract);
    }

    @Override
    public boolean damage(@NotNull Damage damage) {
        boolean toReturn = super.damage(damage);

        setHasBeenDamaged(true);

        Entity sourcePoint = damage.getSource();
        if (sourcePoint != null) {
            takeKnockback(0.4f,
                    Math.sin(sourcePoint.getPosition().yaw() * Math.PI / 180),
                    -Math.cos(sourcePoint.getPosition().yaw() * Math.PI / 180));
        }

        this.setCustomName(Component.text(
                "§8[§7Lv" + getLevel() + "§8] §c" + getDisplayName()
                        + " §a" + Math.round(getHealth())
                        + "§f/§a"
                        + Math.round(this.getAttributeValue(Attribute.MAX_HEALTH))
        ));

        return toReturn;
    }

    @Override
    public void kill() {
        super.kill();
        mobs.remove(this);

        if (!(getLastDamageSource().getAttacker() instanceof SkyBlockPlayer)) return;
        SkyBlockPlayer player = (SkyBlockPlayer) getLastDamageSource().getAttacker();

        SkyBlockEventHandler.callSkyBlockEvent(new PlayerKilledSkyBlockMobEvent(player, this));

        player.getSkills().increase(player, getSkillCategory(), (double) getOtherLoot().getSkillXPAmount());
        player.playSound(Sound.sound(Key.key("entity." + getEntityType().name().toLowerCase().replace("minecraft:", "") + ".death"), Sound.Source.PLAYER, 1f, 1f), Sound.Emitter.self());

        if (getLootTable() == null) return;
        if (getLastDamageSource() == null) return;
        if (getLastDamageSource().getAttacker() == null) return;

        Map<ItemType, SkyBlockLootTable.LootRecord> drops = new HashMap<>();

        for (SkyBlockLootTable.LootRecord record : getLootTable().getLootTable()) {
            ItemType itemType = record.getItemType();

            SkyBlockItem item = new SkyBlockItem(itemType);
            List<LootAffector> affectors = new ArrayList<>();

            affectors.add(LootAffector.MAGIC_FIND);
            if (item.hasComponent(ArmorComponent.class)) {
                affectors.add(LootAffector.ENCHANTMENT_LUCK);
            } else {
                affectors.add(LootAffector.ENCHANTMENT_LOOTING);
            }

            double adjustedChance = record.getChancePercent();

            for (LootAffector affector : affectors) {
                adjustedChance = affector.getAffector().apply(player, adjustedChance, this);
            }

            if (Math.random() * 100 < adjustedChance && record.getShouldCalculate().apply(player)) {
                drops.put(itemType, record);
            }
        }

        for (ItemType itemType : drops.keySet()) {
            SkyBlockLootTable.LootRecord record = drops.get(itemType);

            if (SkyBlockLootTable.LootRecord.isNone(record)) continue;

            SkyBlockItem item = new SkyBlockItem(itemType, record.getAmount());
            ItemType droppedItemLinker = item.getAttributeHandler().getPotentialType();

            if (player.canInsertItemIntoSacks(droppedItemLinker, record.getAmount())) {
                player.getSackItems().increase(droppedItemLinker, record.getAmount());
            } else if (player.getSkyBlockExperience().getLevel().asInt() >= 6) {
                player.addAndUpdateItem(item);
            } else {
                DroppedItemEntityImpl droppedItem = new DroppedItemEntityImpl(item, player);
                droppedItem.setInstance(getInstance(), getPosition().add(0, 0.5, 0));
            }
        }
    }

    public static void runRegionPopulators(Scheduler scheduler) {
        scheduler.submitTask(() -> {
            if (SkyBlockGenericLoader.getLoadedPlayers().isEmpty()) return TaskSchedule.seconds(10);

            MobRegistry.getMobsToRegionPopulate().forEach(mobRegistry -> {
                RegionPopulator regionPopulator = (RegionPopulator) mobRegistry.getMobCache();

                regionPopulator.getPopulators().forEach(populator -> {
                    RegionType regionType = populator.regionType();
                    int minimumAmountToPopulate = populator.minimumAmountToPopulate();

                    int amountInRegion = 0;

                    for (SkyBlockMob mob : SkyBlockRegion.getMobsInRegion(regionType)) {
                        if (!MobRegistry.getFromMob(mob).equals(mobRegistry)) {
                            continue;
                        }

                        amountInRegion++;
                    }

                    if (amountInRegion < minimumAmountToPopulate) {
                        for (int i = 0; i < minimumAmountToPopulate - amountInRegion; i++)
                            RegionPopulator.populateRegion(mobRegistry, populator);
                    }
                });
            });

            return TaskSchedule.seconds(5);
        });
    }

    @Override
    public void tick(long time) {
        Instance instance = getInstance();
        Pos position = getPosition();

        if (instance == null) {
            return;
        }

        if (!instance.isChunkLoaded(position)) {
            instance.loadChunk(position).join();
        }

        try {
            super.tick(time);
        } catch (Exception e) {
            // Suppress odd warnings
        }
    }

    public static @NonNull List<SkyBlockMob> getMobFromFuzzyPosition(Pos position) {
        List<SkyBlockMob> mobs = new ArrayList<>();
        for (SkyBlockMob mob : getMobs()) {
            if (MathUtility.isWithinSameBlock(mob.getPosition(), position)) {
                mobs.add(mob);
            }
        }
        return mobs;
    }
}
