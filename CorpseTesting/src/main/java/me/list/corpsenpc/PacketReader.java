package me.list.corpsenpc;

import java.lang.reflect.Field;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import net.minecraft.network.protocol.game.PacketPlayInUseEntity;

public class PacketReader {
	
	private final Player player;
	private int count = 0;
	
	public PacketReader(Player player) {
		this.player = player;
		
		
	}
	
	public boolean inject() {
		CraftPlayer nmsPlayer = (CraftPlayer) player;
		Channel channel = nmsPlayer.getHandle().b.a.k;
		
		if (channel.pipeline().get("PacketInjector") != null)
			
		return false;
		
		channel.pipeline().addAfter("decoder", "PacketInjector", new MessageToMessageDecoder<PacketPlayInUseEntity>() {
			@Override
			protected void decode(ChannelHandlerContext channelHandlerContext, PacketPlayInUseEntity packetPlayInUseEntity, java.util.List<Object> list)
					throws Exception {
				list.add(packetPlayInUseEntity);
				read(packetPlayInUseEntity);
				
			}
		});
		return false;
		
		
	}
	
	public boolean uninject() {
		CraftPlayer nmsPlayer = (CraftPlayer) player;
		Channel channel = nmsPlayer.getHandle().b.a.k;
		
		if (channel.pipeline().get("PacketInjector") != null)
			channel.pipeline().remove("PacketInjector");		
		return false;
	}
	

	private void read(PacketPlayInUseEntity packetPlayInUseEntity) {
		// runs for each action, 4 actions
		count++;
		if (count == 4) {
			count = 0;
			int entityID = (int) getValue(packetPlayInUseEntity, "a");
			// call event
			if (!CorpseEntity.corpseEntities.containsKey(entityID))
				return;
			new BukkitRunnable() {

				@Override
				public void run() {
					Bukkit.getPluginManager().callEvent(new RightClickCorpse(player, CorpseEntity.corpseEntities.get(entityID)));
					
				}
				
				
			}.runTask(CorpseNPC.getPlugin(CorpseNPC.class));
		}
	}
	private Object getValue(Object instance, String name) {
	Object result = null;
	try {
		Field field = instance.getClass().getDeclaredField(name);
		field.setAccessible(true);
		result = field.get(instance);
		field.setAccessible(false);
	} catch(Exception e) {
		e.printStackTrace();
	}
	return result;
	}
}
