package me.pauzen.bukkitcommonpluginapi.messages;

import org.bukkit.ChatColor;

public enum WarningMessage implements Message {
    
    EXAMPLE("Example warning message."),
    ;
    
    WarningMessage(String warningMessage) {
        this.warningMessage = warningMessage;
    }
    
    String warningMessage;
    
    @Override
    public String getPrefix() {
        return ChatColor.YELLOW + "Warning: " + ChatColor.GOLD;
    }

    @Override
    public String getRawMessage() {
        return warningMessage;
    }
}
