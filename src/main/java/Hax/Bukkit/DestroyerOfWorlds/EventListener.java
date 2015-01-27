package Hax.Bukkit.DestroyerOfWorlds;

import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerLoginEvent;

public class EventListener implements Listener {
	DestroyerOfWorlds plugin;
	
	public EventListener(DestroyerOfWorlds plugin) {
		this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}
	
    @EventHandler
    public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent event) {
    	if (!event.getEntityType().equals(EntityType.PLAYER)) {
    		return;
    	}
    	
    	if (!event.getDamager().getType().equals(EntityType.PLAYER)) {
    		return;
    	}
    	
    	plugin.setLastDamagedBy(event.getEntity().getName(), event.getDamager().getName());
    }
    
    @EventHandler
    public void onEntityDeathEvent(EntityDeathEvent event) {
    	plugin.giveKill(event.getEntity().getName());
    }
}
