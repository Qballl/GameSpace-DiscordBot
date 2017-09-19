package com.pzg.www.discord.object;

import java.util.ArrayList;
import java.util.List;

import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.DiscordException;

public class Bot {
	
	private IDiscordClient Bot;
	private String prefix;
	
	private List<String> badWords = new ArrayList<String>();
	private List<Command> commands = new ArrayList<Command>();
	
	private List<Long> mutedUsers = new ArrayList<Long>();
	
	private Method sendMethod= new Method();
	
	public Bot(String token, String prefix) {
		this.prefix = prefix;
		Bot = createClient(token, true);
		Bot.getDispatcher().registerListener(this);
		
	}
	
	public IDiscordClient getBot() {
		return Bot;
	}
	
	public String getPrefix() {
		return prefix;
	}
	
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
	
	public void addCommand(Command command) {
		commands.add(command);
	}
	
	public void removeCommand(Command command) {
		commands.remove(command);
	}
	
	public void clearCommands() {
		commands.clear();
	}
	
	public List<Command> getCommands() {
		return commands;
	}
	
	public void addBadWord(String badWord) {
		badWords.add(badWord);
	}
	
	public void removeBadWord(String badWord) {
		badWords.remove(badWord);
	}
	
	public void clearBadWords() {
		badWords.clear();
	}
	
	public List<String> getBadWords() {
		return badWords;
	}
	
	public List<Long> getMutedUsers() {
		return mutedUsers;
	}
	
	public void muteUser(long id) {
		mutedUsers.add(id);
	}
	
	public void unmuteUser(long id) {
		mutedUsers.remove(id);
	}
	
	public void setSendMethod(Method sendMethod) {
		this.sendMethod = sendMethod;
	}
	
	private IDiscordClient createClient(String token, boolean login) {
		ClientBuilder clientBuilder = new ClientBuilder();
		clientBuilder.withToken(token);
		try {
			if (login) {
				return clientBuilder.login();
			} else {
				return clientBuilder.build();
			}
		} catch (DiscordException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@EventSubscriber
	public void onMessage(MessageReceivedEvent event) {
		for (long id : mutedUsers) {
			if (event.getAuthor().getLongID() == id) {
				event.getMessage().delete();
				return;
			}
		}
		for (String badWord : badWords) {
			if (event.getMessage().getContent().toLowerCase().contains(badWord.toLowerCase())) {
				event.getMessage().delete();
				event.getAuthor().getOrCreatePMChannel().sendMessage("Please watch your **langauge**! You used foul words in "+ event.getGuild().getName() + " " + event.getChannel() + ".");
				return;
			}
		}
		if (event.getMessage().getContent().toLowerCase().contains(prefix)) {
			IMessage message = event.getMessage();
			IUser sender = message.getAuthor();
			IChannel channel = message.getChannel();
			IGuild guild = message.getGuild();
			
			String[] command = message.getContent().replaceFirst(prefix, "").split(" ");
			
			
			for (Command com : commands) {
				if (command[0].equalsIgnoreCase(com.getLabel())) {
					if (!channel.isPrivate()) {
						message.delete();
					}
					
					List<String> args = new ArrayList<String>();
					for (String s : command) {
						args.add(s);
					}
					args.remove(0);
					
					if (com.getClass().equals(CommandMethod.class)) {
						((CommandMethod) com).runCommand(sender, channel, guild, command[0], args);
						((CommandMethod) com).runCommand(sender, channel, guild, command[0], args, message);
					}
					
					if (com.getClass().equals(CommandMessage.class)) {
						channel.sendMessage(((CommandMessage) com).getReturnMessage().replace("<user>", sender.mention()));
					}
					
					return;
				}
			}
		}
		sendMethod.onMessage(event);
	}
}