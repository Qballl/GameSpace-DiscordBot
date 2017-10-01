package com.pzg.www.discord.main;

public class Main {
	
	public static GamerSpaceBot gsBot;
	
	public static boolean running;
	
	public static void main(String[] args) {
		running = true;
		
		gsBot = new GamerSpaceBot("MzUxODY3NjIwNTIzMjQ1NTc4.DIY-CA.NqNSrxdF4k8nM4jt5dZraQPuEDA", "!");
		
		while (running) {
			gsBot.loop();
		}
	}
}