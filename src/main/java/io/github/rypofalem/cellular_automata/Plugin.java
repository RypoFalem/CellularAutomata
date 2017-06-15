package io.github.rypofalem.cellular_automata;


import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

public class Plugin extends JavaPlugin implements CommandExecutor {
	AutomatonTask task;

	@Override
	public void onEnable(){
		Bukkit.getPluginCommand("cell").setExecutor(this);
		task = null;
		System.out.println("" + Math.floorMod(-1 , 16));
	}

	@Override
	public void onDisable(){
		if(task != null ) task.cancel();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
		if(!(sender instanceof Player)){
			sender.sendMessage("Only Players can use this command.");
			return true;
		}
		Player player = (Player)sender;

		long rule;
		if(args != null && args.length > 0){
			try{
				rule = Long.valueOf(args[0]);
			} catch(NumberFormatException nfe){
				player.sendMessage("The first argument must be a number.");
				return true;
			}
		} else{
			player.sendMessage("I will pick a number for you.");
			Random rand = new Random();
			rule = rand.nextLong();
		}
		if(task != null){
			task.cancel();
		}
		task = new AutomatonTask(new Location(player.getWorld(), 0, 100, 0), rule);
		task.runTaskTimer(this, 0, 10);
		player.sendMessage("Beginning rule " + rule);

		return true;
	}


	class AutomatonTask extends BukkitRunnable {
		CellularAutomaton autonoma;
		Location zero;
		long rule;
		int iterations = 0;
		int size = 32;

		AutomatonTask(Location location, long rule){
			zero = location;
			this.rule = rule;
			boolean[][][] state = new boolean[size][size][size];
			state[size/2 - 1][size/2 - 1][size/2 - 1] = true;
			autonoma = new CellularAutomaton(state, rule);
		}


		@Override
		public void run() {
			if(iterations%100 == 0)
				Bukkit.broadcastMessage("Running iteration " + iterations);
			long startTime = System.nanoTime();
			boolean[][][] state = autonoma.iterate();
			float iterateTime = (System.nanoTime() - startTime) /1000000f;
			startTime = System.nanoTime();
			for(int x = 0; x<size; x++){
				for(int y = 0; y<size; y++){
					for(int z = 0; z<size; z++){
						Material type = state[x][y][z] ? Material.GLASS : Material.AIR;
						zero.getWorld()
								.getBlockAt(zero.getBlockX() + x, zero.getBlockY() + y, zero.getBlockZ()+z)
								.setType(type);
					}
				}
			}
			Bukkit.broadcastMessage(String.format("%2.3fms to iterate, %2.3fms to place blocks",
					iterateTime,
					(System.nanoTime() - startTime) / 1000000f));
			iterations++;
		}
	}
}
