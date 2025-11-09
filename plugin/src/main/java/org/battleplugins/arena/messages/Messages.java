package org.battleplugins.arena.messages;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.StyleBuilderApplicable;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;

public final class Messages {
    public static final TextColor PRIMARY_COLOR = NamedTextColor.YELLOW;
    public static final TextColor SECONDARY_COLOR = NamedTextColor.GOLD;
    public static final TextColor ERROR_COLOR = NamedTextColor.RED;
    public static final TextColor SUCCESS_COLOR = NamedTextColor.GREEN;

    static final TagResolver RESOLVER = TagResolver.builder()
            .tag("primary", Tag.styling(PRIMARY_COLOR))
            .tag("secondary", Tag.styling(SECONDARY_COLOR))
            .tag("error", Tag.styling(ERROR_COLOR))
            .tag("success", Tag.styling(SUCCESS_COLOR))
            .build();

    static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();

    // Misc
    public static final Message HEADER = message("header",
            Component.text("------------------").color(NamedTextColor.GRAY)
                    .append(Component.text("[").color(SECONDARY_COLOR))
                    .append(Component.text(" "))
                    .append(Component.text("{}").color(PRIMARY_COLOR).decorate(TextDecoration.BOLD))
                    .append(Component.text(" "))
                    .append(Component.text("]").color(SECONDARY_COLOR))
                    .append(Component.text("------------------")).color(NamedTextColor.GRAY)
    );

    public static final Message PREFIX = message("prefix", "<secondary>[</secondary><primary>BattleArena</primary><secondary>]</secondary>");

    // Command messages
    public static final Message MUST_BE_PLAYER = error("command-must-be-player", "你必須是玩家才能使用此指令！");
    public static final Message COMMAND_USAGE = error("command-usage", "語法錯誤！正確用法：{}");
    public static final Message ARENA_DOES_NOT_EXIST = error("command-arena-does-not-exist", "名為 {} 的競技場不存在！");
    public static final Message NO_PERMISSION = error("command-no-permission", "你沒有權限執行此指令！");
    public static final Message UNKNOWN_ERROR = error("command-unknown-error", "執行指令時發生未知錯誤！請聯絡伺服器管理員！");
    public static final Message AN_ERROR_OCCURRED = error("command-an-error-occurred", "執行指令時發生錯誤：{}！");
    public static final Message PLAYER_NOT_ONLINE = error("command-player-not-found", "無法找到玩家 <secondary>{}</secondary>！");
    public static final Message INVALID_TYPE = error("command-invalid-type", "指定的類型 <secondary>{}</secondary> 不是有效的 {}！");
    public static final Message INVALID_POSITION = error("command-invalid-position", "指定的位置 (<secondary>{}</secondary>) 無效！預期格式：<secondary><x>,<y>,<z></secondary>。");
    public static final Message CLICK_TO_PREPARE = info("command-click-to-prepare", "點擊以準備：<secondary>{}</secondary>");

    // Arena messages
    public static final Message NO_OPEN_ARENAS = error("arena-no-open-arenas", "目前沒有開放的競技場！");
    public static final Message NO_MAPS_FOR_ARENA = error("arena-no-open-maps-for-arena", "此競技場沒有開放的地圖！");
    public static final Message NO_RUNNING_COMPETITIONS = error("arena-no-running-competitions", "目前沒有正在進行的比賽。");
    public static final Message NO_ARENA_WITH_NAME = error("arena-arena-with-name", "沒有該名稱的競技場！");
    public static final Message ARENA_FULL = error("arena-full", "此競技場已滿！");
    public static final Message ARENA_NOT_JOINABLE = error("arena-not-joinable", "此競技場目前不可加入！");
    public static final Message ARENA_NOT_SPECTATABLE = error("arena-not-spectatable", "此競技場無法旁觀！");
    public static final Message ARENA_ERROR = error("arena-error", "加入競技場時發生錯誤：{}！");
    public static final Message ALREADY_IN_ARENA = error("arena-already-in-arena", "你已經在競技場中！");
    public static final Message NOT_IN_ARENA = error("arena-not-in-arena", "你目前不在任何競技場中！");
    public static final Message ARENA_JOINED = info("arena-joined", "你已加入 <secondary>{}</secondary>！");
    public static final Message ARENA_KICKED = success("arena-kicked", "你已將 <secondary>{}</secondary> 踢出競技場！");
    public static final Message ARENA_CANNOT_KICK_SELF = error("arena-cannot-kick-self", "你不能將自己踢出競技場！請使用 <secondary>/{} leave</secondary> 離開競技場。");
    public static final Message ARENA_KICKED_PLAYER = error("arena-kicked-player", "你已被踢出競技場！");
    public static final Message ARENA_SPECTATE = info("arena-spectate", "你現在正在旁觀 <secondary>{}</secondary>！");
    public static final Message ARENA_LEFT = info("arena-left", "你已離開 <secondary>{}</secondary>！");
    public static final Message ARENA_REMOVED = success("arena-removed", "競技場 <secondary>{}</secondary> 已被移除！");
    public static final Message NOT_EVENT = error("arena-not-event", "指定的競技場不是活動類型！");
    public static final Message MANUAL_EVENT_MESSAGE = info("arena-manual-event-message", "%prefix% {} 活動即將開始！輸入 <secondary>/{} join</secondary> 參加！");
    public static final Message PHASE = info("arena-phase", "階段：<secondary>{}</secondary>。");
    public static final Message PLAYERS = info("arena-players", "玩家：<secondary>{}/{}</secondary>。");
    public static final Message ADVANCED_PHASE = info("arena-advanced-phase", "已進入下一階段：<secondary>{}</secondary>！");
    public static final Message NO_PHASES = error("arena-no-phases", "沒有可前進的階段！");
    public static final Message NO_TEAM_WITH_NAME = error("arena-no-team-with-name", "沒有名為 <secondary>{}</secondary> 的隊伍！");
    public static final Message TEAM_FULL = error("arena-team-full", "隊伍 {} 已滿！");
    public static final Message TEAM_JOINED = info("arena-team-joined", "你已加入 {} 隊！");
    public static final Message TEAM_LEFT = info("arena-team-left", "你已離開 {} 隊！");
    public static final Message ALREADY_ON_THIS_TEAM = error("arena-already-on-this-team", "你已經在 {} 隊中！");
    public static final Message NOT_ON_TEAM = error("arena-not-on-team", "你目前不在任何隊伍中！");
    public static final Message NO_TEAMS = error("arena-no-teams", "此競技場中沒有任何隊伍！");
    public static final Message CANNOT_JOIN_TEAM_SOLO = error("arena-cannot-join-team-solo", "你無法在單人競技場中加入隊伍！");
    public static final Message TEAM_SELECTION_NOT_AVAILABLE = error("arena-team-selection-not-available", "目前無法選擇隊伍！");
    public static final Message CANNOT_JOIN_ARENA_NOT_PARTY_LEADER = error("arena-cannot-join-arena-not-party-leader", "你必須是隊伍領隊才能加入競技場！");
    public static final Message CANNOT_JOIN_ARENA_MEMBER_IN_ARENA = error("arena-cannot-join-arena-member-in-arena", "當隊伍成員已在競技場中時，你無法加入新的競技場！");

    public static final Message ARENA_STARTS_IN = info("arena-starts-in", "{} 將在 <secondary>{}</secondary> 後開始！");
    public static final Message ARENA_START_CANCELLED = error("arena-starts-cancelled", "倒數已取消，因為人數不足以開始比賽！");

    public static final Message FIGHT = message("arena-fight", "<dark_red> O---[{==========> <yellow>戰鬥開始</yellow> <==========}]---O </dark_red>");

    // Editor wizard messages
    public static final Message ENTERED_WIZARD = info("editor-entered", """
                
                你已進入編輯精靈。請依照提示的步驟操作以繼續編輯流程。
                
                隨時可輸入「cancel」以退出。
                """
    );
    public static final Message ERROR_OCCURRED_APPLYING_CHANGES = error("editor-error-occurred-applying-changes", "套用變更時發生錯誤。請查看主控台以獲取更多資訊！");
    public static final Message ERROR_ALREADY_IN_EDITOR = error("editor-error-already-in-editor", "你已在編輯精靈中！輸入「cancel」以退出。");

    public static final Message MAP_CREATE_NAME = info("editor-map-create-name", "請輸入地圖名稱！輸入「cancel」以取消。");
    public static final Message MAP_EXISTS = error("editor-map-exists", "已有相同名稱的地圖存在！");

    public static final Message MAP_SET_TYPE = info("editor-map-set-type", """
                請輸入地圖類型。可用選項：<secondary>static, dynamic</secondary>。
    
                <gray>-</gray> <secondary>static</secondary>：地圖位置固定（如生存世界），同時只能支援一場比賽。
                <gray>-</gray> <secondary>dynamic</secondary>：地圖會即時生成於獨立世界中，可同時支援多場比賽。需要安裝 WorldEdit！
                """
    );
    public static final Message MAP_SET_BOUNDS = info("editor-map-set-positions", """
                    <green>請使用選擇工具設定地圖邊界：</green>
                    <yellow>• 左鍵點擊方塊設定最小位置 (MIN)</yellow>
                    <yellow>• 右鍵點擊方塊設定最大位置 (MAX)</yellow>
                    <gold>• 完成後在聊天輸入 'done'</gold>
                    """
    );
    public static final Message MAP_SET_WAITROOM_SPAWN = info("editor-map-set-waitroom-spawn", "輸入「waitroom」以設定等待室重生點，或輸入「cancel」以取消。");
    public static final Message MAP_SET_SPECTATOR_SPAWN = info("editor-map-set-spectator-spawn", "輸入「spectator」以設定觀戰者重生點，或輸入「cancel」以取消。");
    public static final Message MAP_ADD_TEAM_SPAWN = info("editor-map-add-team-spawn", "輸入「spawn」以新增隊伍重生點。完成後輸入「done」。若想清除某隊所有重生點並重新設定，輸入「clear」。");
    public static final Message MAP_ADD_TEAM_SPAWN_TEAM = info("editor-map-add-team-spawn-team", "請輸入要新增重生點的隊伍名稱。輸入「cancel」以取消。");
    public static final Message MAP_CLEAR_TEAM_SPAWN_TEAM = info("editor-map-clear-team-spawn-team", "請輸入要清除所有重生點的隊伍名稱。輸入「cancel」以取消。");
    public static final Message MAP_ADD_TEAM_SPAWN_ADDED = success("editor-map-add-team-spawn-added", "已為隊伍 <secondary>{}</secondary> 新增一個重生點！");
    public static final Message MAP_MISSING_TEAM_SPAWNS = error("editor-map-missing-team-spawns", "你必須為每個隊伍新增至少一個重生點！缺少重生點的隊伍：<secondary>{}</secondary>");
    public static final Message MAP_ADD_TEAM_SPAWN_CLEARED = success("editor-map-add-team-spawn-cleared", "已清除隊伍 <secondary>{}</secondary> 的所有重生點！");
    public static final Message MAP_CREATED = success("editor-map-created", "成功建立地圖 <secondary>{}</secondary> 給 {}！");
    public static final Message MAP_EDITED = success("editor-map-edited", "成功編輯地圖 <secondary>{}</secondary>！");
    public static final Message MAP_FAILED_TO_SAVE = error("editor-map-failed-to-save", "儲存地圖 <secondary>{}</secondary> 時發生錯誤！請查看主控台以獲取錯誤資訊。");
    public static final Message WIZARD_CLOSED = success("editor-wizard-closed", "編輯精靈已關閉！");

    // Util strings
    public static final Message MILLISECONDS = message("util-milliseconds", "毫秒");
    public static final Message MILLISECOND = message("util-millisecond", "毫秒");
    public static final Message SECONDS = message("util-seconds", "秒");
    public static final Message SECOND = message("util-second", "秒");
    public static final Message MINUTES = message("util-minutes", "分鐘");
    public static final Message MINUTE = message("util-minute", "分鐘");
    public static final Message HOURS = message("util-hours", "小時");
    public static final Message HOUR = message("util-hour", "小時");
    public static final Message DAYS = message("util-days", "天");
    public static final Message DAY = message("util-day", "天");
    public static final Message ENABLED = message("util-enabled", "啟用", NamedTextColor.GREEN);
    public static final Message DISABLED = message("util-disabled", "停用", NamedTextColor.RED);

    public static final Message DEBUG_MODE_SET_TO = success("util-debug-mode-set-to", "除錯模式已設定為 <secondary>{}</secondary>！");
    public static final Message CLICK_TO_SELECT = info("util-click-to-select", "點擊以選擇！");
    public static final Message INVALID_INPUT = error("util-invalid-input", "輸入無效！可用選項：<secondary>{}</secondary>");
    public static final Message INVALID_INVENTORY_CANCELLING = error("util-invalid-inventory-cancelling", "操作了非自身的物品欄... 已取消物品選擇！");
    public static final Message VALID_TEAMS = info("util-valid-teams", "有效隊伍：<secondary>{}</secondary>");
    public static final Message INVALID_TEAM_VALID_TEAMS = error("util-invalid-team-valid-teams", "無效的隊伍！有效隊伍：<secondary>{}</secondary>。");
    public static final Message INVENTORY_BACKUPS = message("util-inventory-backups", "備份");
    public static final Message NO_BACKUPS = error("util-no-backups", "找不到玩家 <secondary>{}</secondary> 的任何備份！");
    public static final Message BACKUP_NOT_FOUND = error("util-backup-not-found", "找不到指定索引的備份！");
    public static final Message BACKUP_RESTORED = success("util-backup-restored", "成功還原玩家 <secondary>{}</secondary> 的備份！");
    public static final Message BACKUP_CREATED = success("util-backup-created", "成功建立玩家 <secondary>{}</secondary> 的備份！");
    public static final Message BACKUP_INFO = message("util-backup-info", "備份 <secondary>{}</secondary>");
    public static final Message MODULES = message("util-modules", "模組");
    public static final Message MODULE = message("util-module", "<gray>-</gray> <secondary>{}：</secondary> {}");
    public static final Message STARTING_RELOAD = info("util-starting-reload", "正在重新載入 BattleArena...");
    public static final Message RELOAD_COMPLETE = success("util-reload-complete", "重新載入完成，耗時 <secondary>{}</secondary>！");
    public static final Message RELOAD_FAILED = error("util-reload-failed", "重新載入失敗！請查看主控台以獲取更多資訊。");

    static void init() {
        // no-op
    }

    public static Message wrap(String defaultText) {
        return new Message("unregistered", MINI_MESSAGE.deserialize(defaultText, RESOLVER));
    }

    public static Message wrap(Component defaultComponent) {
        return new Message("unregistered", defaultComponent);
    }

    public static Message info(String translationKey, String defaultText) {
        return message(translationKey, MINI_MESSAGE.deserialize(defaultText, RESOLVER).color(PRIMARY_COLOR));
    }

    public static Message error(String translationKey, String defaultText) {
        return message(translationKey, MINI_MESSAGE.deserialize(defaultText, RESOLVER).color(ERROR_COLOR));
    }

    public static Message success(String translationKey, String defaultText) {
        return message(translationKey, MINI_MESSAGE.deserialize(defaultText, RESOLVER).color(SUCCESS_COLOR));
    }

    public static Message message(String translationKey, String text) {
        return message(translationKey, MINI_MESSAGE.deserialize(text, RESOLVER));
    }

    public static Message message(String translationKey, String text, StyleBuilderApplicable... styles) {
        return message(translationKey, Component.text(text, Style.style(styles)));
    }

    public static Message message(String translationKey, String text, Style style) {
        return message(translationKey, Component.text(text, style));
    }

    public static Message message(String translationKey, Component text) {
        return MessageLoader.register(Message.of(translationKey, text));
    }
}
