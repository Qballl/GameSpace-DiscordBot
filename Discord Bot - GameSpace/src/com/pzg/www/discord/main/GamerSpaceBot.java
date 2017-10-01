package com.pzg.www.discord.main;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.pzg.www.discord.object.Bot;
import com.pzg.www.discord.object.CommandMethod;
import com.pzg.www.discord.object.Method;
import com.pzg.www.discord.rss.Feed;
import com.pzg.www.discord.rss.FeedMessage;
import com.pzg.www.discord.rss.RSSFeedParser;
import com.pzg.www.games.Connect4;

import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.impl.events.guild.member.UserJoinEvent;
import sx.blah.discord.handle.impl.events.guild.member.UserLeaveEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Permissions;

public class GamerSpaceBot {
	
	public Bot bot;
	
	boolean connect4 = false;
	HashMap<IUser, Boolean> c4Players = new HashMap<IUser, Boolean>();
	Connect4 c4Game;
	
	long latest;
	
	public GamerSpaceBot(String token, String prefix) {
//		c4Game = new Connect4("", "", "");
		
		bot = new Bot(token, prefix);
		registerBot();
		
		bot.addCommand(new CommandMethod("updateusers", Permissions.ADMINISTRATOR.toString(), new Method() {
			@Override
			public void method(IUser user, IChannel chan, IGuild guild, String label, List<String> args) {
				for (IChannel channel : guild.getChannels()) {
					if (channel.getName().equalsIgnoreCase("info")) {
						channel.changeTopic("Current users: " + guild.getUsers().size());
					}
				}
			}
		}));
		
		bot.addCommand(new CommandMethod("mute", Permissions.SEND_MESSAGES.toString(), new Method() {
			@Override
			public void method(IUser user, IChannel channel, IGuild guild, String label, List<String> args) {
				boolean isStaff = false;
				for (IRole role : user.getRolesForGuild(guild)) {
					if (role.getName().equalsIgnoreCase("staff")) {
						isStaff = true;
					}
				}
				if (isStaff) {
					IChannel punishments = null;
					for (IChannel channels : guild.getChannels()) {
						if (channels.getName().equalsIgnoreCase("punishments")) {
							punishments = channels;
						}
					}
					if (args.size() > 1) {
						String reason = "";
						for (int i = 1; i < args.size(); i++) {
							if (i == 1) {
								reason = args.get(i);
							} else {
								reason += " " + args.get(i);
							}
						}
						IUser muted = guild.getUserByID(Long.parseLong(args.get(0).replace("<", "").replace("@", "").replace(">", "")));
						if (muted != null) {
							bot.muteUser(muted.getLongID());
							muted.getOrCreatePMChannel().sendMessage(Embeds.warnEmbed(user.getName(), user.getAvatarURL(), "**Muted**: " + muted.getName() + "\n**Reason**:  " + reason));
							punishments.sendMessage(Embeds.logEmbed(user.getName(), user.getAvatarURL(), "**Muted**: " + muted.getName() + "\n**Reason**:  " + reason));
						}
					}
				}
			}
		}));
		
		bot.addCommand(new CommandMethod("unmute", Permissions.SEND_MESSAGES.toString(), new Method() {
			@Override
			public void method(IUser user, IChannel channel, IGuild guild, String label, List<String> args) {
				boolean isStaff = false;
				for (IRole role : user.getRolesForGuild(guild)) {
					if (role.getName().equalsIgnoreCase("staff")) {
						isStaff = true;
					}
				}
				if (isStaff) {
					IChannel punishments = null;
					for (IChannel channels : guild.getChannels()) {
						if (channels.getName().equalsIgnoreCase("punishments")) {
							punishments = channels;
						}
					}
					if (args.size() == 1) {
						IUser muted = guild.getUserByID(Long.parseLong(args.get(0).replace("<", "").replace("@", "").replace(">", "")));
						if (muted != null) {
							bot.unmuteUser(muted.getLongID());
							muted.getOrCreatePMChannel().sendMessage(Embeds.logEmbed(muted.getName(), muted.getAvatarURL(), "You have been unmuted!"));
							punishments.sendMessage(Embeds.logEmbed(user.getName(), user.getAvatarURL(), "Unmuted " + muted.getName() + "."));
						}
					}
				}
			}
		}));
		
		bot.addCommand(new CommandMethod("warn", Permissions.SEND_MESSAGES.toString(), new Method() {
			@Override
			public void method(IUser user, IChannel channel, IGuild guild, String label, List<String> args) {
				boolean isStaff = false;
				for (IRole role : user.getRolesForGuild(guild)) {
					if (role.getName().equalsIgnoreCase("staff")) {
						isStaff = true;
					}
				}
				if (isStaff) {
					IChannel punishments = null;
					for (IChannel channels : guild.getChannels()) {
						if (channels.getName().equalsIgnoreCase("punishments")) {
							punishments = channels;
						}
					}
					if (args.size() > 1) {
						String reason = "";
						for (int i = 1; i < args.size(); i++) {
							if (i == 1) {
								reason = args.get(i);
							} else {
								reason += " " + args.get(i);
							}
						}
						IUser muted = guild.getUserByID(Long.parseLong(args.get(0).replace("<", "").replace("@", "").replace(">", "")));
						if (muted != null) {
							muted.getOrCreatePMChannel().sendMessage(Embeds.warnEmbed(user.getName(), user.getAvatarURL(), "You have been warned for " + reason + ""));
							punishments.sendMessage(Embeds.logEmbed(user.getName(), user.getAvatarURL(), "**Warned**: " + muted.getName() + "\n**Reason**:  " + reason));
						}
					}
				}
			}
		}));
		
		bot.addCommand(new CommandMethod("kick", Permissions.SEND_MESSAGES.toString(), new Method() {
			@Override
			public void method(IUser user, IChannel channel, IGuild guild, String label, List<String> args) {
				boolean isStaff = false;
				for (IRole role : user.getRolesForGuild(guild)) {
					if (role.getName().equalsIgnoreCase("staff")) {
						isStaff = true;
					}
				}
				if (isStaff) {
					IChannel punishments = null;
					for (IChannel channels : guild.getChannels()) {
						if (channels.getName().equalsIgnoreCase("punishments")) {
							punishments = channels;
						}
					}
					if (args.size() > 1) {
						String reason = "";
						for (int i = 1; i < args.size(); i++) {
							if (i == 1) {
								reason = args.get(i);
							} else {
								reason += " " + args.get(i);
							}
						}
						IUser muted = guild.getUserByID(Long.parseLong(args.get(0).replace("<", "").replace("@", "").replace(">", "")));
						if (muted != null) {
							muted.getOrCreatePMChannel().sendMessage(Embeds.warnEmbed(user.getName(), user.getAvatarURL(), "You have been kicked from Gamer Space.\n**Reason**: " + reason + ""));
							punishments.sendMessage(Embeds.logEmbed(user.getName(), user.getAvatarURL(), "**Kicked**: " + muted.getName() + "\n**Reason**:  " + reason));
							guild.kickUser(muted);
						}
					}
				}
			}
		}));
		
		bot.addCommand(new CommandMethod("ban", Permissions.SEND_MESSAGES.toString(), new Method() {
			@Override
			public void method(IUser user, IChannel channel, IGuild guild, String label, List<String> args) {
				boolean isStaff = false;
				for (IRole role : user.getRolesForGuild(guild)) {
					if (role.getName().equalsIgnoreCase("staff")) {
						isStaff = true;
					}
				}
				if (isStaff) {
					IChannel punishments = null;
					for (IChannel channels : guild.getChannels()) {
						if (channels.getName().equalsIgnoreCase("punishments")) {
							punishments = channels;
						}
					}
					if (args.size() > 1) {
						String reason = "";
						for (int i = 1; i < args.size(); i++) {
							if (i == 1) {
								reason = args.get(i);
							} else {
								reason += " " + args.get(i);
							}
						}
						IUser muted = guild.getUserByID(Long.parseLong(args.get(0).replace("<", "").replace("@", "").replace(">", "")));
						if (muted != null) {
							muted.getOrCreatePMChannel().sendMessage(Embeds.warnEmbed(user.getName(), user.getAvatarURL(), "You have been banned from Gamer Space.\n**Reason**: " + reason + ""));
							punishments.sendMessage(Embeds.logEmbed(user.getName(), user.getAvatarURL(), "**Banned**: " + muted.getName() + "\n**Reason**:  " + reason));
							guild.banUser(muted, reason);
						}
					}
				}
			}
		}));
		
		bot.addCommand(new CommandMethod("unban", Permissions.SEND_MESSAGES.toString(), new Method() {
			@Override
			public void method(IUser user, IChannel channel, IGuild guild, String label, List<String> args) {
				boolean isStaff = false;
				for (IRole role : user.getRolesForGuild(guild)) {
					if (role.getName().equalsIgnoreCase("staff")) {
						isStaff = true;
					}
				}
				if (isStaff) {
					IChannel punishments = null;
					for (IChannel channels : guild.getChannels()) {
						if (channels.getName().equalsIgnoreCase("punishments")) {
							punishments = channels;
						}
					}
					if (args.size() == 1) {
						IUser unbanned = null;
						for (IUser banned : guild.getBannedUsers()) {
							if (banned.getName().equalsIgnoreCase(args.get(0))) {
								unbanned = banned;
							}
						}
						if (unbanned != null) {
							guild.pardonUser(unbanned.getLongID());
							punishments.sendMessage(Embeds.logEmbed(user.getName(), user.getAvatarURL(), "**Unbanned**: " + unbanned.getName()));
						} else {
							
						}
					}
				}
			}
		}));
		
		bot.addCommand(new CommandMethod("c4", Permissions.SEND_MESSAGES.toString(), new Method() {
			@Override
			public void method(IUser user, IChannel channel, IGuild guild, String label, List<String> args) {
				
			}
		}));
		
		bot.setSendMethod(new Method() {
			@Override
			public void onMessage(MessageReceivedEvent event) {
				if (event.getAuthor().getName().equalsIgnoreCase("Mee6") || event.getAuthor().getName().equalsIgnoreCase("Tatsumaki")) {
					if (event.getChannel().getName().equalsIgnoreCase("counting") || event.getChannel().getName().equalsIgnoreCase("looking-for-players") || event.getChannel().getName().equalsIgnoreCase("memes") || event.getChannel().getName().equalsIgnoreCase("screenshots") || event.getChannel().getName().equalsIgnoreCase("roasted")) {
						event.getMessage().delete();
					}
				}
				if (event.getMessage().getAttachments().isEmpty()) {
					if (event.getChannel().getName().equalsIgnoreCase("memes") || event.getChannel().getName().equalsIgnoreCase("screenshots")) {
						event.getMessage().delete();
					}
				}
				if (event.getChannel().getName().equalsIgnoreCase("counting")) {
					if (event.getChannel().getMessageHistory(2).get(1).getAuthor().equals(event.getAuthor())) {
						for (IRole role : event.getAuthor().getRolesForGuild(event.getGuild())) {
							if (role.getName().equalsIgnoreCase("staff")) {
								return;
							}
						}
						event.getMessage().delete();
					}
				}
			}
		});
	}
	
	public void loop() {
		RSSFeedParser parser = new RSSFeedParser("http://tjplaysnow.ddns.net/rss-feed.xml");
		Feed feed = parser.readFeed();
		
		FeedMessage message = feed.getMessages().get(feed.getMessages().size() - 1);
		
		for (IGuild guild : bot.getBot().getGuilds()) {
			for (IChannel channel : guild.getChannels()) {
				if (channel.getName().equalsIgnoreCase("bot-news")) {
					String update = "Bot news: " + message.getTitle() + "\n"
							+ "```"
							+ message.getDescription()
							+ "\nAuthor: " + message.getAuthor()
							+ "```" + message.getLink();
					if (!channel.getMessageHistory(1).get(0).getContent().equalsIgnoreCase(update)) {
						channel.sendMessage(update);
					}
				}
			}
		}
		
		try {
			Thread.sleep(TimeUnit.MINUTES.toMillis(1));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void registerBot() {
		for (IGuild guild : bot.getBot().getGuilds()) {
			for (IChannel channel : guild.getChannels()) {
				System.out.println(channel.getName() + ": " + channel.getLongID());
			}
		}
		bot.getBot().getDispatcher().registerListener(this);
	}
	
	@EventSubscriber
	public void userJoin(UserJoinEvent event) {
		for (IChannel channel : event.getGuild().getChannels()) {
			if (channel.getName().equalsIgnoreCase("info")) {
				channel.changeTopic("Current users: " + event.getGuild().getUsers().size());
			}
		}
	}
	
	@EventSubscriber
	public void userLeave(UserLeaveEvent event) {
		for (IChannel channel : event.getGuild().getChannels()) {
			if (channel.getName().equalsIgnoreCase("info")) {
				channel.changeTopic("Current users: " + event.getGuild().getUsers().size());
			}
		}
	}
}