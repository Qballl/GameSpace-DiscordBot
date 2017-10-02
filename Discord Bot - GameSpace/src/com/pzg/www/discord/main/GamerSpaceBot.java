package com.pzg.www.discord.main;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.pzg.www.discord.config.Config;
import com.pzg.www.discord.config.File;
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
import sx.blah.discord.util.EmbedBuilder;

public class GamerSpaceBot {
	
	public Bot bot;
	
	boolean connect4 = false;
	HashMap<IUser, Boolean> c4Players = new HashMap<IUser, Boolean>();
	Connect4 c4Game;
	
	Config config;
	
	long latest;
	
	public GamerSpaceBot(String token, String prefix) {
		bot = new Bot(token, prefix);
		registerBot();
		
		config = new Config(new File("Config.conf"));
		
		bot.addCommand(new CommandMethod("rssfeed", Permissions.ADMINISTRATOR.toString(), new Method() {
			@Override
			public void method(IUser user, IChannel channel, IGuild guild, String label, List<String> args) {
				if (args.size() >= 3) {
					for (IChannel channels : guild.getChannels()) {
						if (channels.mention().equalsIgnoreCase(args.get(0))) {
							try {
								new URL(args.get(1));
								config.set("RSSFeeds", config.getObject("RSSFeeds") + ", " + args.get(0));
								config.set("RSSFeed." + args.get(0) + ".Link", args.get(1));
								String name = "";
								for (int i = 2; i < args.size(); i++) {
									if (i == 1) {
										name = args.get(i);
									} else {
										name += " " + args.get(i);
									}
								}
								config.set("RSSFeed." + args.get(0) + ".Name", name);
							} catch (MalformedURLException e) {
								channel.sendMessage("Invalid URL exception!\nFor: " + args.get(1));
							}
						}
					}
				}
			}
		}));
		
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
		for (IGuild guild : bot.getBot().getGuilds()) {
			for (IChannel channel : guild.getChannels()) {
				for (String channelName : config.getObject("RSSFeeds").split(", ")) {
					if (channel.mention().equalsIgnoreCase(channelName)) {
						String url = config.getObject("RSSFeed." + channelName + ".Link");
						String newsName = config.getObject("RSSFeed." + channelName + ".Name");
						updateParse(url, channelName, newsName);
					}
				}
			}
		}
		try {
			Thread.sleep(TimeUnit.SECONDS.toMillis(5));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void updateParse(String url, String channelName, String newsName) {
		RSSFeedParser gspotParser = new RSSFeedParser(url);
		Feed feed = gspotParser.readFeed();
		FeedMessage message = feed.getMessages().get(0);
		for (IGuild guild : bot.getBot().getGuilds()) {
			for (IChannel channel : guild.getChannels()) {
				if (channel.mention().equalsIgnoreCase(channelName)) {
					
					EmbedBuilder eb = new EmbedBuilder();
					
					eb.withAuthorName(newsName + ": " + message.getTitle());
					eb.withAuthorUrl(message.getLink());
					
					String description = message.getDescription();

					description = description.replace("<p>", "");
					description = description.replace("</p>", "");
					description = description.replace("<strong>", "**");
					description = description.replace("</strong>", "**");
					
					if (description.length() >= 2048) {
						description = description.subSequence(0, 2047).toString();
					}
					
					eb.withDescription(description);
					eb.withColor(50, 255, 50);
					eb.withFooterText(message.getAuthor());
					
					String update = message.getTitle() + "" + message.getLink();
					
					if (!config.getObject("RSSFeed." + channelName + ".Update").equalsIgnoreCase(update)) {
						System.out.println("Updating " + newsName);
						config.set("RSSFeed." + channelName + ".Update", update);
						channel.sendMessage(eb.build());
					}
				}
			}
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