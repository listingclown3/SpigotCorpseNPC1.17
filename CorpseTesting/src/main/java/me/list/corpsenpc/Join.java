package me.list.corpsenpc;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class Join implements Listener {
	
	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		PacketReader reader = new PacketReader((event.getPlayer()));
		try {
			reader.inject();
			System.out.println("Successfully Injected " + event.getPlayer().getName());
			new BukkitRunnable() {

				@Override
				public void run() {
					CorpseEntity.execute(player);
					
				}
				
				
			}.runTaskLater(CorpseNPC.getPlugin(CorpseNPC.class), 5);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		PacketReader reader = new PacketReader((event.getPlayer()));
		try {
			reader.uninject();
			System.out.println("Successfully Uninjected " + event.getPlayer().getName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		
	}

}
