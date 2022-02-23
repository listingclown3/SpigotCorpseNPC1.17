package me.list.corpsenpc;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class Damage implements Listener {

	
	@EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if ((player.getHealth() - event.getDamage()) <= 0) {
                // cancel damage event
                event.setCancelled(true);
 
                // Spawn corpse
                CorpseEntity.execute(player);
                // Change player stats
                player.setHealth(20.0);
                player.getInventory().clear();
                player.setGameMode(GameMode.SPECTATOR);
 
            }
            
            
        }
}
}
