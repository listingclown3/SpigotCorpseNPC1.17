package me.list.corpsenpc;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;

import net.minecraft.network.protocol.game.PacketPlayOutEntityDestroy;
import net.minecraft.server.network.PlayerConnection;
import net.minecraft.world.entity.EntityPose;
import net.minecraft.world.item.ItemStack;


public final class CorpseNPC extends JavaPlugin implements Listener {

	public DataManager data;
	
    @Override
    public void onEnable() {
    	this.data = new DataManager(this);
    	 getServer().getPluginManager().registerEvents(new Damage(), this);
    	 getServer().getPluginManager().registerEvents(new Join(), this);
    	 getServer().getPluginManager().registerEvents(this, this);
    	 
    }

    @Override
    public void onDisable() {
    	
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (label.equalsIgnoreCase("corpsenpc")) {
			Player player = (Player) sender;
			CorpseEntity.execute(player);
			
		}
    	
    	
    	return false;
	}
    
    @EventHandler
	public void onInteract(RightClickCorpse event) {
		Player p = event.getPlayer();
		if (event.getCorpse().getPose() != EntityPose.c)
			return;
		if (event.getCorpse().getInventory().isEmpty()) 
			return;
		
			
	
	
	Inventory inv = Bukkit.createInventory(null, 54, "Dead Player");
	for (ItemStack item : event.getCorpse().getInventory().getContents())
		inv.addItem(CraftItemStack.asBukkitCopy(item));
	
	event.getPlayer().openInventory(inv);
	for (Player on: Bukkit.getOnlinePlayers()) {
		PlayerConnection player = ((CraftPlayer) on).getHandle().b;
		player.sendPacket(new PacketPlayOutEntityDestroy(event.getCorpse().getId()));
		int amount = 0;
		
		if (this.data.getConfig().contains("players." + p.getName().toString() + ".total")) 
			amount = this.data.getConfig().getInt("players." + p.getUniqueId().toString() + ".total");
		
		this.data.getConfig().set("players." + p.getName().toString() + ".total", (amount + 1));
		data.saveConfig();
	}
	Bukkit.broadcastMessage(event.getCorpse().getName() + " has died!");
		

	}

	
	
	
    
}
