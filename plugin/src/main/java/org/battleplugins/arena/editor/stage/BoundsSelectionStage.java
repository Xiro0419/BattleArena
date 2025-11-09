package org.battleplugins.arena.editor.stage;

import io.papermc.paper.event.player.AsyncChatEvent;
import io.papermc.paper.math.Position;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.battleplugins.arena.BattleArena;
import org.battleplugins.arena.editor.EditorContext;
import org.battleplugins.arena.editor.WizardStage;
import org.battleplugins.arena.messages.Message;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class BoundsSelectionStage<E extends EditorContext<E>> implements WizardStage<E> {
    private final Message chatMessage;
    private final Function<E, BiConsumer<Position, Position>> inputConsumer;

    public BoundsSelectionStage(Message chatMessage, Function<E, BiConsumer<Position, Position>> inputConsumer) {
        this.chatMessage = chatMessage;
        this.inputConsumer = inputConsumer;
    }

    @Override
    public void enter(E context) {
        Player player = context.getPlayer();

        ItemStack selectionTool = createSelectionTool();
        player.getInventory().addItem(selectionTool);

        if (this.chatMessage != null) {
            context.inform(this.chatMessage);
        }

        BoundsSelectionHandler handler = new BoundsSelectionHandler(player, inputConsumer.apply(context), context);

        Bukkit.getPluginManager().registerEvents(handler, BattleArena.getInstance());
        context.bind(handler);
    }

    private ItemStack createSelectionTool() {
        ItemStack tool = new ItemStack(Material.STICK);
        ItemMeta meta = tool.getItemMeta();

        meta.displayName(Component.text("區域選擇工具", NamedTextColor.GOLD));
        meta.lore(java.util.List.of(
                Component.text("左鍵: 設定最小位置 (MIN)", NamedTextColor.YELLOW),
                Component.text("右鍵: 設定最大位置 (MAX)", NamedTextColor.YELLOW),
                Component.text("完成後輸入 'done'", NamedTextColor.GREEN)
        ));

        tool.setItemMeta(meta);
        return tool;
    }

    private static class BoundsSelectionHandler implements Listener {
        private static final Map<UUID, Long> LAST_INPUT = new HashMap<>();
        private final Player player;
        private final BiConsumer<Position, Position> boundsConsumer;
        private final EditorContext<?> context;

        private Location minPos;
        private Location maxPos;

        public BoundsSelectionHandler(Player player, BiConsumer<Position, Position> boundsConsumer, EditorContext<?> context) {
            this.player = player;
            this.boundsConsumer = boundsConsumer;
            this.context = context;
        }

        @EventHandler
        public void onInteract(PlayerInteractEvent event) {
            if (!player.equals(event.getPlayer())) {
                return;
            }

            ItemStack item = event.getItem();
            if (!isSelectionTool(item)) {
                return;
            }

            if (event.getAction() != Action.LEFT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
                return;
            }

            if (event.getClickedBlock() == null) {
                return;
            }

            if (LAST_INPUT.containsKey(player.getUniqueId()) &&
                    System.currentTimeMillis() - LAST_INPUT.get(player.getUniqueId()) < 500) {
                return;
            }

            LAST_INPUT.put(player.getUniqueId(), System.currentTimeMillis());

            Location clickedLoc = event.getClickedBlock().getLocation();

            if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
                minPos = clickedLoc;
                player.sendMessage(Component.text("最小位置已設定: ", NamedTextColor.GREEN)
                        .append(Component.text(String.format("(%d, %d, %d)",
                                clickedLoc.getBlockX(),
                                clickedLoc.getBlockY(),
                                clickedLoc.getBlockZ()), NamedTextColor.YELLOW)));
            } else if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                maxPos = clickedLoc;
                player.sendMessage(Component.text("最大位置已設定: ", NamedTextColor.GREEN)
                        .append(Component.text(String.format("(%d, %d, %d)",
                                clickedLoc.getBlockX(),
                                clickedLoc.getBlockY(),
                                clickedLoc.getBlockZ()), NamedTextColor.YELLOW)));
            }

            if (minPos != null && maxPos != null) {
                player.sendMessage(Component.text("兩個位置都已設定！輸入 'done' 完成選擇", NamedTextColor.GOLD));
            }

            event.setCancelled(true);
        }

        @EventHandler
        public void onChat(AsyncChatEvent event) {
            if (!player.equals(event.getPlayer())) {
                return;
            }

            event.setCancelled(true);
            String message = PlainTextComponentSerializer.plainText().serialize(event.originalMessage());

            if (message.equalsIgnoreCase("done")) {
                Bukkit.getScheduler().runTask(BattleArena.getInstance(), this::complete);
            } else if (message.equalsIgnoreCase("cancel")) {
                Bukkit.getScheduler().runTask(BattleArena.getInstance(), this::cancel);
            }
        }

        @SuppressWarnings("UnstableApiUsage")
        private void complete() {
            if (minPos == null || maxPos == null) {
                player.sendMessage(Component.text("請先設定最小和最大位置！", NamedTextColor.RED));
                return;
            }

            removeSelectionTool();

            Position min = Position.block(minPos.getBlockX(), minPos.getBlockY(), minPos.getBlockZ());
            Position max = Position.block(maxPos.getBlockX(), maxPos.getBlockY(), maxPos.getBlockZ());

            boundsConsumer.accept(min, max);

            HandlerList.unregisterAll(this);
            context.unbind(this);
            context.advanceStage();
        }

        private void cancel() {
            removeSelectionTool();
            HandlerList.unregisterAll(this);
            context.unbind(this);
            player.sendMessage(Component.text("已取消區域選擇", NamedTextColor.YELLOW));
        }

        private void removeSelectionTool() {
            for (int i = 0; i < player.getInventory().getSize(); i++) {
                ItemStack item = player.getInventory().getItem(i);
                if (isSelectionTool(item)) {
                    player.getInventory().setItem(i, null);
                    break;
                }
            }
        }

        private boolean isSelectionTool(ItemStack item) {
            if (item == null || item.getType() != Material.STICK) {
                return false;
            }

            ItemMeta meta = item.getItemMeta();
            if (meta == null || !meta.hasDisplayName()) {
                return false;
            }

            Component displayName = meta.displayName();
            return Component.text("區域選擇工具", NamedTextColor.GOLD).equals(displayName);
        }
    }
}