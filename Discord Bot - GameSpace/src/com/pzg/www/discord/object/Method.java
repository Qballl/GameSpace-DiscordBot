package com.pzg.www.discord.object;

import java.util.List;

import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

public class Method {
	public void method(IUser user, IChannel channel, IGuild guild, String label, List<String> args, IMessage message) {}
	public void method(IUser user, IChannel channel, IGuild guild, String label, List<String> args) {}
	public void onMessage(MessageReceivedEvent event) {}
}