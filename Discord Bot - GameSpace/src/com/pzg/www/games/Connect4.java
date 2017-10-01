package com.pzg.www.games;

import com.pzg.www.discord.config.Config;
import com.pzg.www.discord.config.File;
import com.pzg.www.discord.main.Main;

import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;

public class Connect4 {
	
	String emptySlot;
	String blueCoin;
	String redCoin;
	
	String board[][];
	
	Config config;
	
	public Connect4(String emptySlot, String blueCoin, String redCoin) {
		this.emptySlot = emptySlot;
		this.blueCoin = blueCoin;
		
		config = new Config(new File("Config.conf"));
		
		int width = 7;
		int height = 6;
		for (int w = 0; w < width; w++) {
			for (int h = 0; h < height; h++) {
				board[w][h] = emptySlot;
			}
		}
		for (IGuild guild : Main.gsBot.bot.getBot().getGuilds()) {
			for (IChannel channel : guild.getChannels()) {
				if (channel.getName().equalsIgnoreCase("connect-4") || channel.getName().equalsIgnoreCase("connect-four")) {
					
					channel.getMessageByID(Long.parseLong("")).edit("");
					
				}
			}
		}
		
	}
	
	
	
}