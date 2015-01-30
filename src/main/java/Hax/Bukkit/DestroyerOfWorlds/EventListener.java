package Hax.Bukkit.DestroyerOfWorlds;

import org.bukkit.GameMode;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class EventListener implements Listener {
	DestroyerOfWorlds plugin;
	
	public EventListener(DestroyerOfWorlds plugin) {
		this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}
	
    @EventHandler
    public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent event) {
    	final Player hurt;
    	Player damager;
    	if (event.getEntityType().equals(EntityType.PLAYER)) {
    		hurt = ((Player) event.getEntity());
    		
    		final double knockbackFactor = hurt.getMaxHealth()/hurt.getHealth();
        	plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                @Override
                public void run() {
                    Vector knockback = hurt.getVelocity().multiply(knockbackFactor);
                    hurt.setVelocity(knockback);
                }
            }, 3);
    		
    		if (event.getDamager().getType().equals(EntityType.PLAYER)) {
        		damager = (Player) event.getDamager();
        	} else if (event.getDamager() instanceof Projectile) {
        		if (((Projectile) event.getDamager()).getShooter() instanceof Player) {
        			damager = (Player) ((Projectile) event.getDamager()).getShooter();
        		} else {
        			return;
        		}
        	} else {
        		return;
        	}
    		
    		plugin.setLastDamagedBy(hurt.getName(), damager.getName());
    	}
    }
    
    @EventHandler
    public void onDamage(EntityDamageEvent event) {
    	if (event.getEntityType().equals(EntityType.PLAYER)) {
    		Player player = ((Player) event.getEntity());
    		if (player.getHealth() - event.getDamage() <= 0) {
	    		player.setGameMode(GameMode.SPECTATOR);
	    		for (ItemStack stack : player.getInventory()) {
	    			if (stack != null) {
	    				player.getWorld().dropItemNaturally(player.getLocation(), stack);
	    			}
	    		}
	    		player.getInventory().clear();
	    		plugin.playerDied((Player) event.getEntity());
	    		plugin.checkGameOver();
	    		event.setDamage(0);
    		}
    	}
    }
}
