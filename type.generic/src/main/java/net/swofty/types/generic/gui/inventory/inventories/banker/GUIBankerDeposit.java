package net.swofty.types.generic.gui.inventory.inventories.banker;

import net.minestom.server.event.inventory.InventoryCloseEvent;
import net.minestom.server.event.inventory.InventoryPreClickEvent;
import net.minestom.server.inventory.Inventory;
import net.minestom.server.inventory.InventoryType;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.swofty.commons.StringUtility;
import net.swofty.proxyapi.ProxyPlayer;
import net.swofty.types.generic.data.DataHandler;
import net.swofty.types.generic.data.DataMutexService;
import net.swofty.types.generic.data.datapoints.DatapointBankData;
import net.swofty.types.generic.data.mongodb.CoopDatabase;
import net.swofty.types.generic.gui.inventory.ItemStackCreator;
import net.swofty.types.generic.gui.inventory.SkyBlockInventoryGUI;
import net.swofty.types.generic.gui.inventory.item.GUIClickableItem;
import net.swofty.types.generic.gui.inventory.item.GUIQueryItem;
import net.swofty.types.generic.mission.missions.MissionDepositCoinsInBank;
import net.swofty.types.generic.user.SkyBlockPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

public class GUIBankerDeposit extends SkyBlockInventoryGUI {

    public GUIBankerDeposit() {
        super("Bank Deposit", InventoryType.CHEST_4_ROW);
    }

    @Override
    public void setItems(InventoryGUIOpenEvent e) {
        fill(ItemStackCreator.createNamedItemStack(Material.BLACK_STAINED_GLASS_PANE));
        set(GUIClickableItem.getGoBackItem(31, new GUIBanker()));

        set(new GUIClickableItem(11) {
            @Override
            public ItemStack.Builder getItem(SkyBlockPlayer player) {
                return ItemStackCreator.getStack("§aYour whole purse", Material.CHEST, 64,
                        "§8Bank deposit",
                        " ",
                        "§7Current balance: §6" + StringUtility.decimalify(
                                player.getDataHandler().get(DataHandler.Data.BANK_DATA, DatapointBankData.class).getValue().getAmount(), 1
                        ),
                        "§7Amount to deposit: §6" + StringUtility.decimalify(player.getCoins(), 1),
                        " ",
                        "§eClick to deposit coins!"
                );
            }

            @Override
            public void run(InventoryPreClickEvent e, SkyBlockPlayer player) {
                player.closeInventory();
                attemptDeposit(player, player.getCoins());
            }
        });

        set(new GUIClickableItem(13) {
            @Override
            public ItemStack.Builder getItem(SkyBlockPlayer player) {
                return ItemStackCreator.getStack("§aHalf of your purse", Material.CHEST, 32,
                        "§8Bank deposit",
                        " ",
                        "§7Current balance: §6" + StringUtility.decimalify(
                                player.getDataHandler().get(DataHandler.Data.BANK_DATA, DatapointBankData.class).getValue().getAmount(), 1
                        ),
                        "§7Amount to deposit: §6" + StringUtility.decimalify(player.getCoins() / 2, 1),
                        " ",
                        "§eClick to deposit coins!"
                );
            }

            @Override
            public void run(InventoryPreClickEvent e, SkyBlockPlayer player) {
                player.closeInventory();
                attemptDeposit(player, player.getCoins() / 2);
            }
        });

        set(new GUIQueryItem(15) {
            @Override
            public SkyBlockInventoryGUI onQueryFinish(String query, SkyBlockPlayer player) {
                try {
                    double amount = Double.parseDouble(query);
                    if (amount > player.getCoins()) {
                        player.sendMessage("§cYou do not have that many coins to deposit!");
                        return null;
                    }
                    if (amount <= 0) {
                        player.sendMessage("§cYou cannot deposit that amount!");
                        return null;
                    }

                    player.closeInventory();
                    attemptDeposit(player, amount);
                } catch (NumberFormatException ex) {
                    player.sendMessage("§cThat is not a valid number!");
                }
                return null;
            }

            @Override
            public ItemStack.Builder getItem(SkyBlockPlayer player) {
                return ItemStackCreator.getStack("§aCustom amount", Material.OAK_SIGN, 1,
                        "§8Bank deposit",
                        " ",
                        "§7Current balance: §6" + StringUtility.decimalify(
                                player.getDataHandler().get(DataHandler.Data.BANK_DATA, DatapointBankData.class).getValue().getAmount(), 1
                        ),
                        "§7Amount to deposit: §6Custom",
                        " ",
                        "§eClick to deposit coins!"
                );
            }
        });
    }

    @Override
    public void onClose(InventoryCloseEvent e, CloseReason reason) {
        if (e == null) return;
        SkyBlockPlayer player = (SkyBlockPlayer) e.getPlayer();
        player.setBankDelayed(false);
    }

    @Override
    public void suddenlyQuit(Inventory inventory, SkyBlockPlayer player) {
        player.setBankDelayed(false);
    }

    private void attemptDeposit(SkyBlockPlayer player, double amount) {
        if (player.getMissionData().isCurrentlyActive(MissionDepositCoinsInBank.class)) {
            player.getMissionData().endMission(MissionDepositCoinsInBank.class);
        }
        DatapointBankData.BankData bankData = player.getDataHandler().get(DataHandler.Data.BANK_DATA, DatapointBankData.class).getValue();
        if (bankData.getAmount() + amount > bankData.getBalanceLimit()) {
            player.sendMessage("§cYou cannot deposit that much, you would exceed your balance limit of §6" + StringUtility.commaify(
                    bankData.getBalanceLimit()) + " coins§c!");
            return;
        }

        player.sendMessage("§8Depositing coins...");
        player.setCoins(player.getCoins() - amount);
        if (!player.isCoop()) {
            bankData.addAmount(amount);
            bankData.addTransaction(new DatapointBankData.Transaction(
                    System.currentTimeMillis(),
                    amount,
                    player.getUsername()
            ));

            player.sendMessage("§aYou have deposited §6" + StringUtility.decimalify(amount, 1) + " coins§a! You now have §6" +
                    StringUtility.decimalify(bankData.getAmount(), 1)
                    + " coins§a in your account.");
            return;
        }
        CoopDatabase.Coop coop = player.getCoop();
        player.setBankDelayed(true);

        String lockKey = "bank_data:" + player.getSkyBlockIsland().getIslandID().toString();
        DataMutexService mutexService = new DataMutexService();

        mutexService.withSynchronizedData(
                lockKey,
                coop.members(),
                DataHandler.Data.BANK_DATA,
                (DatapointBankData.BankData latestBankData) -> {
                    if (latestBankData.getAmount() + amount > latestBankData.getBalanceLimit()) {
                        player.sendMessage("§cYou cannot deposit that much, you would exceed your balance limit of §6" +
                                StringUtility.commaify(latestBankData.getBalanceLimit()) + " coins§c!");
                        return null; // Return null to indicate failure - no changes made
                    }

                    player.setCoins(player.getCoins() - amount);
                    latestBankData.addAmount(amount);
                    latestBankData.addTransaction(new DatapointBankData.Transaction(
                            System.currentTimeMillis(), amount, player.getUsername()));

                    player.sendMessage("§aYou have deposited §6" + StringUtility.decimalify(amount, 1) +
                            " coins§a! You now have §6" + StringUtility.decimalify(latestBankData.getAmount(), 1) +
                            " coins§a in your account.");

                    return latestBankData; // Return modified data to be propagated to all servers
                },
                () -> {
                    player.sendMessage("§cYou cannot deposit coins as your coop members are currently using the bank.");
                }
        );
    }

    @Override
    public boolean allowHotkeying() {
        return false;
    }

    @Override
    public void onBottomClick(InventoryPreClickEvent e) {
        e.setCancelled(true);
    }
}
