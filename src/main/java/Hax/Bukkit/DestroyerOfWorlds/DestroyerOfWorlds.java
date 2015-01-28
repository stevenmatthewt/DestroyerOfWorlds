package Hax.Bukkit.DestroyerOfWorlds;
 
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;
 
public final class DestroyerOfWorlds extends JavaPlugin {
	ScoreboardManager manager;
	Scoreboard board;
	Team teamNether;
	Team teamFrost;
	Objective objective;
	
	ArrayList<Player> alivePlayers;
	ArrayList<Player> deadPlayers;
	HashMap<String, String> lastDamagedBy;
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("begin")) {
	    	deadPlayers = new ArrayList<Player>();
	    	alivePlayers = new ArrayList<Player>(getServer().getOnlinePlayers());
	    	
	    	for (Player p : alivePlayers) {
	    		p.setGameMode(GameMode.SURVIVAL);
	    	}
	    	
			setScoreBoards();
			return true;
		}
		return false;
	}
	
    @Override
    public void onEnable() {
        getLogger().info("DestroyerOfWorlds has been started!");
        
        manager = Bukkit.getScoreboardManager();
        board = manager.getNewScoreboard();
        teamNether = board.registerNewTeam("Nether");
        teamFrost = board.registerNewTeam("Frost");
        
        teamNether.setAllowFriendlyFire(false);
        teamFrost.setAllowFriendlyFire(true);
        
        objective = board.registerNewObjective("kills", "kills");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        
        lastDamagedBy = new HashMap();
        
        new EventListener(this);
    }
 
    @Override
    public void onDisable() {
        getLogger().info("DestroyerOfWorlds has been stopped!");
    }
    
    public void setLastDamagedBy(final String damaged, String damager) {
    	lastDamagedBy.put(damaged, damager);
    	getServer().getScheduler().scheduleSyncDelayedTask(this,
			new Runnable() {
				@Override
				public void run() {
					lastDamagedBy.put(damaged, null);
				}
			}, 300);
    }
    
    public String getLastDamagedBy(String damaged) {
    	return lastDamagedBy.get(damaged);
    }
    
    public void playerDied(Player dead) {
    	dead.setGameMode(GameMode.SPECTATOR);
    	String deadName = dead.getName();
    	alivePlayers.remove(dead);
    	deadPlayers.add(dead);
    	if (lastDamagedBy.get(deadName) == null) {
    		return;
    	}
    	Player killer = getServer().getPlayer(lastDamagedBy.get(deadName));
    	if (killer == null) {
    		return;
    	}
    	if(!killer.equals(null)) {
    		Score score = objective.getScore(killer);
    		score.setScore(score.getScore() + 1);
    		
    		lastDamagedBy.put(deadName, null);
    	}
    }
    
    public void setScoreBoards() {
    	for (Player p : getServer().getOnlinePlayers()) {
    		p.setScoreboard(board);
    		
    		Score score = objective.getScore(p);
    		score.setScore(0);
    	}
    }
    
    public void checkGameOver() {
    	if (alivePlayers.size() == 1) {
    		alivePlayers.get(0).sendMessage("You winned the game!!!");
    	}
    }
}