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
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;

public class BlockInputStage<E extends EditorContext<E>> implements WizardStage<E> {
    private final Message chatMessage;
    private final Function<E, Consumer<Position>> inputConsumer;

    public BlockInputStage(Message chatMessage, Function<E, Consumer<Position>> inputConsumer) {
        this.chatMessage = chatMessage;
        this.inputConsumer = inputConsumer;
    }

    @Override
    public void enter(E context) {
        Player player = context.getPlayer();

        if (this.chatMessage != null) {
            context.inform(this.chatMessage);
        }

        BlockInputHandler handler = new BlockInputHandler(player, inputConsumer.apply(context), context);

        Bukkit.getPluginManager().registerEvents(handler, BattleArena.getInstance());
        context.bind(handler);
    }

    private static class BlockInputHandler implements Listener {
        private static final Map<UUID, Long> LAST_INPUT = new HashMap<>();
        private final Player player;
        private final Consumer<Position> positionConsumer;
        private final EditorContext<?> context;

        private Location position;

        public BlockInputHandler(Player player, Consumer<Position> positionConsumer, EditorContext<?> context) {
            this.player = player;
            this.positionConsumer = positionConsumer;
            this.context = context;
        }

        @EventHandler
        public void onInteract(PlayerInteractEvent event) {
            if (!player.equals(event.getPlayer())) {
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
            position = clickedLoc;

            player.sendMessage(Component.text("方塊位置已設定: ", NamedTextColor.GREEN)
                    .append(Component.text(String.format("(%d, %d, %d)",
                            clickedLoc.getBlockX(),
                            clickedLoc.getBlockY(),
                            clickedLoc.getBlockZ()), NamedTextColor.YELLOW)));

            if (position != null) {
                player.sendMessage(Component.text("方塊位置都已設定！點擊副手鍵 'F' 完成選擇", NamedTextColor.GOLD));
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

            if (message.equalsIgnoreCase("cancel")) {
                Bukkit.getScheduler().runTask(BattleArena.getInstance(), this::cancel);
            }
        }

        @EventHandler
        public void onSwapHand(PlayerSwapHandItemsEvent event) {
            if (!player.equals(event.getPlayer())) {
                return;
            }

            Bukkit.getScheduler().runTask(BattleArena.getInstance(), this::complete);
            event.setCancelled(true);
        }

        @SuppressWarnings("UnstableApiUsage")
        private void complete() {
            if (position == null) {
                player.sendMessage(Component.text("請先設定方塊位置！", NamedTextColor.RED));
                return;
            }

            Position pos = Position.block(position.getBlockX(), position.getBlockY(), position.getBlockZ());

            positionConsumer.accept(pos);

            HandlerList.unregisterAll(this);
            context.unbind(this);
            context.advanceStage();
        }

        private void cancel() {
            HandlerList.unregisterAll(this);
            context.unbind(this);
            player.sendMessage(Component.text("已取消方塊選擇", NamedTextColor.YELLOW));
        }
    }
}
