package org.battleplugins.arena.command;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import org.battleplugins.arena.Arena;
import org.battleplugins.arena.BattleArena;
import org.battleplugins.arena.competition.CompetitionType;
import org.battleplugins.arena.competition.event.EventOptions;
import org.battleplugins.arena.competition.event.EventType;
import org.battleplugins.arena.messages.Messages;
import org.battleplugins.arena.util.InventoryBackup;
import org.battleplugins.arena.util.OptionSelector;
import org.battleplugins.arena.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class BACommandExecutor extends BaseCommandExecutor {

    public BACommandExecutor(String parentCommand) {
        super(parentCommand);
    }

    @ArenaCommand(commands = "backups", description = "Shows backups that a player has saved.", permissionNode = "backups")
    public void backups(CommandSender sender, @Argument(name = "player") String playerName) {
        CompletableFuture.supplyAsync(() -> Bukkit.getServer().getOfflinePlayer(playerName)).thenAcceptAsync(target -> {
            if (target == null) {
                Messages.PLAYER_NOT_ONLINE.send(sender, playerName);
                return;
            }

            // Show backups
            List<InventoryBackup> backups = InventoryBackup.load(target.getUniqueId());
            if (backups.isEmpty()) {
                Messages.NO_BACKUPS.send(sender, target.getName());
                return;
            }

            Messages.HEADER.sendCentered(sender, Messages.INVENTORY_BACKUPS);

            List<OptionSelector.Option> options = backups.stream().map(backup -> new OptionSelector.Option(
                    Messages.BACKUP_INFO.withContext(backup.getFormattedDate()),
                    "/ba restore " + target.getName() + " " + (backups.indexOf(backup) + 1)
            )).toList();
            if (sender instanceof Player player) {
                OptionSelector.sendOptions(player, options, ClickEvent.Action.SUGGEST_COMMAND);
            } else {
                for (int i = 0; i < options.size(); i++) {
                    // Console cannot click messages, so send command instead
                    OptionSelector.Option option = options.get(i);
                    Component message = Component.text("[" + (i + 1) + "] ", Messages.SECONDARY_COLOR)
                            .append(option.message().toComponent().style(Style.style(Messages.PRIMARY_COLOR)))
                            .append(Component.text(" (Run: \"" + option.command() + "\" to restore)", NamedTextColor.WHITE));

                    sender.sendMessage(message);
                }
            }
        }, Bukkit.getScheduler().getMainThreadExecutor(BattleArena.getInstance()));
    }

    @ArenaCommand(commands = "restore", description = "Restores a backup for a player.", permissionNode = "restore")
    public void restore(CommandSender sender, Player target, int backupIndex) {
        backupIndex--;

        // Restore backup
        List<InventoryBackup> backups = InventoryBackup.load(target.getUniqueId());
        if (backupIndex < 0 || backupIndex >= backups.size()) {
            Messages.BACKUP_NOT_FOUND.send(sender);
            return;
        }

        InventoryBackup backup = backups.get(backupIndex);
        if (backup == null) {
            Messages.BACKUP_NOT_FOUND.send(sender);
            return;
        }

        backup.restore(target);
        Messages.BACKUP_RESTORED.send(sender, target.getName());
    }

    @ArenaCommand(commands = "backup", description = "Creates a manual backup of a player's inventory.", permissionNode = "backup")
    public void backup(CommandSender sender, Player target) {
        InventoryBackup.save(new InventoryBackup(target.getUniqueId(), target.getInventory().getContents()));
        Messages.BACKUP_CREATED.send(sender, target.getName());
    }

    @ArenaCommand(commands = "modules", description = "Lists all modules.", permissionNode = "modules")
    public void modules(CommandSender sender) {
        Messages.HEADER.sendCentered(sender, Messages.MODULES);

        // All enabled modules
        BattleArena.getInstance().getModules()
                .stream()
                .sorted(Comparator.comparing(module -> module.module().name()))
                .forEach(module -> Messages.MODULE.send(sender, Messages.wrap(module.module().name()), Messages.ENABLED));

        // All failed modules
        BattleArena.getInstance().getFailedModules()
                .stream()
                .sorted(Comparator.comparing(e -> e.getModule().name()))
                .forEach(exception -> {
                    String moduleName = exception.getModule().name();
                    Component component = Messages.MODULE.withContext(Messages.wrap(moduleName), Messages.DISABLED)
                            .toComponent();

                    List<Component> hoverLines = new ArrayList<>();
                    hoverLines.add(Component.text("Module " + moduleName + " v" + exception.getModule().version() + " failed to load:"));
                    hoverLines.add(Component.empty());

                    for (StackTraceElement element : exception.getStackTrace()) {
                        hoverLines.add(Component.text(element.toString()));
                    }

                    component = component.hoverEvent(Component.join(JoinConfiguration.newlines(), hoverLines));
                    sender.sendMessage(component);
                });
    }

    @ArenaCommand(commands = "start", description = "Starts an event manually.", permissionNode = "start")
    public void event(CommandSender sender, Arena arena) {
        if (arena.getType() != CompetitionType.EVENT) {
            Messages.NOT_EVENT.send(sender);
            return;
        }

        arena.getPlugin().getEventScheduler().startEvent(arena, new EventOptions(
                EventType.MANUAL,
                Duration.ZERO,
                Duration.ZERO,
                Messages.MANUAL_EVENT_MESSAGE.toComponent(arena.getName(), arena.getName().toLowerCase(Locale.ROOT))
        ));
    }

    @ArenaCommand(commands = "stop", description = "Stops an event manually.", permissionNode = "stop")
    public void stop(CommandSender sender, Arena arena) {
        if (arena.getType() != CompetitionType.EVENT) {
            Messages.NOT_EVENT.send(sender);
            return;
        }

        arena.getPlugin().getEventScheduler().stopEvent(arena);
    }

    @ArenaCommand(commands = "stopall", description = "Stops all events manually.", permissionNode = "stopall")
    public void stopAll(CommandSender sender) {
        for (Arena scheduledEvent : BattleArena.getInstance().getEventScheduler().getScheduledEvents()) {
            BattleArena.getInstance().getEventScheduler().stopEvent(scheduledEvent);
        }
    }

    @ArenaCommand(commands = "schedule", description = "Schedules an event to start at the specified time.", permissionNode = "schedule")
    public void schedule(CommandSender sender, Arena arena, Duration interval) {
        if (arena.getType() != CompetitionType.EVENT) {
            Messages.NOT_EVENT.send(sender);
            return;
        }

        arena.getPlugin().getEventScheduler().scheduleEvent(arena, new EventOptions(
                EventType.SCHEDULED,
                interval,
                Duration.ZERO,
                Messages.MANUAL_EVENT_MESSAGE.toComponent(arena.getName(), arena.getName().toLowerCase(Locale.ROOT))
        ), false);
    }

    @ArenaCommand(commands = "debug", description = "Toggles debug mode.", permissionNode = "debug")
    public void debug(CommandSender sender) {
        BattleArena.getInstance().setDebugMode(!BattleArena.getInstance().isDebugMode());
        Messages.DEBUG_MODE_SET_TO.send(sender, Boolean.toString(BattleArena.getInstance().isDebugMode()));
    }

    @ArenaCommand(commands = "reload", description = "Reloads the plugin.", permissionNode = "reload")
    public void reload(CommandSender sender) {
        Messages.STARTING_RELOAD.send(sender);
        long start = System.currentTimeMillis();

        try {
            BattleArena.getInstance().reload();
        } catch (Exception e) {
            Messages.RELOAD_FAILED.send(sender);
            BattleArena.getInstance().error("Failed to reload plugin", e);
            return;
        }

        long end = System.currentTimeMillis();
        Messages.RELOAD_COMPLETE.send(sender, Util.toUnitString(end - start, TimeUnit.MILLISECONDS));
    }

    public void sendHeader(CommandSender sender) {
        Messages.HEADER.sendCentered(sender, "BattleArena");
    }
}
