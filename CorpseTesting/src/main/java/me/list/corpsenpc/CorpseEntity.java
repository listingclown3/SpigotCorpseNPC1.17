package me.list.corpsenpc;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_17_R1.CraftServer;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_17_R1.scoreboard.CraftScoreboard;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import net.minecraft.core.BlockPosition;
import net.minecraft.network.protocol.game.PacketPlayOutEntity;
import net.minecraft.network.protocol.game.PacketPlayOutEntityDestroy;
import net.minecraft.network.protocol.game.PacketPlayOutEntityMetadata;
import net.minecraft.network.protocol.game.PacketPlayOutNamedEntitySpawn;
import net.minecraft.network.protocol.game.PacketPlayOutPlayerInfo;
import net.minecraft.network.protocol.game.PacketPlayOutScoreboardTeam;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.network.syncher.DataWatcherRegistry;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.network.PlayerConnection;
import net.minecraft.world.entity.EntityPose;
import net.minecraft.world.scores.ScoreboardTeam;
import net.minecraft.world.scores.ScoreboardTeamBase;

public class CorpseEntity {
	
	public static Map<Integer, EntityPlayer> corpseEntities = new HashMap<>();
	
	public static void execute(Player player) {
		
		EntityPlayer craftPlayer = ((CraftPlayer) player).getHandle();
		Property textures = (Property) craftPlayer.getProfile().getProperties().get("textures").toArray()[0];
		GameProfile gameProfile = new GameProfile(UUID.randomUUID(), player.getName());
		gameProfile.getProperties().put("textures", new Property("textures", textures.getValue(), textures.getSignature()));
		
		EntityPlayer corpse = new EntityPlayer(((CraftServer)Bukkit.getServer()).getServer(),
				((CraftWorld)player.getWorld()).getHandle(),
				gameProfile);
				
		corpse.setPosition(player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ());
	
		Arrays.stream(player.getInventory().getContents()).forEach(itemStack -> {
			corpse.getInventory().pickup(CraftItemStack.asNMSCopy(itemStack));
		});
		
		
		
		Location bed = player.getLocation().add(1,0,0);
		corpse.e(new BlockPosition(bed.getX(), bed.getY(), bed.getZ()));
	
		ScoreboardTeam team = new ScoreboardTeam(
				((CraftScoreboard)Bukkit.getScoreboardManager().getMainScoreboard()).getHandle(),
				player.getName());
		
		team.setNameTagVisibility(ScoreboardTeamBase.EnumNameTagVisibility.b);
		
		PacketPlayOutScoreboardTeam score1 = PacketPlayOutScoreboardTeam.a(team);
		PacketPlayOutScoreboardTeam score2 = PacketPlayOutScoreboardTeam.a(team, true);
		PacketPlayOutScoreboardTeam score3 = PacketPlayOutScoreboardTeam.a(team, corpse.getName(), PacketPlayOutScoreboardTeam.a.a);
		
		corpse.setPose(EntityPose.c); // c = when sleeping // d = swimming
		
		DataWatcher watcher = corpse.getDataWatcher();
		
		byte b = 0x01 | 0x02 | 0x4 | 0x8 | 0x10 | 0x20 | 0x40;
		watcher.set(DataWatcherRegistry.a.a(17), b);
		
		PacketPlayOutEntity.PacketPlayOutRelEntityMove move = new PacketPlayOutEntity.PacketPlayOutRelEntityMove(
				corpse.getId(), (byte) 0, (byte) ((player.getLocation().getBlockY() - 1.7 - player.getLocation().getY()) * 32), 
				(byte) 0, false);
		
		for (Player online : Bukkit.getOnlinePlayers()) {
			PlayerConnection connection = ((CraftPlayer)online).getHandle().b;
			
			connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.a, corpse));
			connection.sendPacket(new PacketPlayOutNamedEntitySpawn(corpse));
			
			
			
			connection.sendPacket(score1);
			connection.sendPacket(score2);
			connection.sendPacket(score3);
			
			connection.sendPacket(new PacketPlayOutEntityMetadata(corpse.getId(), watcher, true));
			connection.sendPacket(move);
			
			new BukkitRunnable() {
				public void run() {
					connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.e, corpse));
				}
			}.runTaskAsynchronously(CorpseNPC.getPlugin(CorpseNPC.class));
			
			corpseEntities.put(corpse.getId(), corpse);
		
			
		}
		
		
		
	}

		public static void removeNPC(Player player) {
			GameProfile gameProfile = new GameProfile(UUID.randomUUID(), player.getName());
			EntityPlayer corpse = new EntityPlayer(((CraftServer)Bukkit.getServer()).getServer(),
					((CraftWorld)player.getWorld()).getHandle(),
					gameProfile);
		    PlayerConnection connection = (((CraftPlayer)player).getHandle()).b;
		    connection.sendPacket(new PacketPlayOutEntityDestroy(new int[] { corpse.getId() }));
		  }
		
	}

	



