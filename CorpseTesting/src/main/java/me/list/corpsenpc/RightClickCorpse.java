package me.list.corpsenpc;


import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import net.minecraft.server.level.EntityPlayer;

public class RightClickCorpse extends Event {
 
    private final Player player;
    private final EntityPlayer corpse;
 
    private static final HandlerList HANDLERS = new HandlerList();
 
    public RightClickCorpse(Player player, EntityPlayer corpse) {
        this.player = player;
        this.corpse = corpse;
    }
 
    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }
 
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
 
    public Player getPlayer() {
        return player;
    }
 
    public EntityPlayer getCorpse() {
        return corpse;
    }
}
