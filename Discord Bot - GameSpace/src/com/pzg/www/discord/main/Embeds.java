package com.pzg.www.discord.main;

import java.util.Calendar;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.util.EmbedBuilder;

public class Embeds {
	
	public static EmbedObject logEmbed(String userName, String userLogo, String logInfo) {
		Calendar cal = Calendar.getInstance();
		EmbedBuilder eb = new EmbedBuilder();
		
		eb.withAuthorName(userName);
		eb.withThumbnail(userLogo);

		eb.withColor(9, 252, 26);
		
		eb.appendField("**__What happened:__**", logInfo, true);
		
		String minute = "" + cal.get(Calendar.MINUTE);
		if (minute.length() == 1)
			minute = "0" + minute;
		
		eb.withFooterText(cal.get(Calendar.HOUR) + ":" + minute + " " + cal.get(Calendar.DAY_OF_MONTH) + "/" + cal.get(Calendar.MONTH) + "/" + cal.get(Calendar.YEAR));
		
		return eb.build();
	}
	
	public static EmbedObject warnEmbed(String userName, String userLogo, String warnInfo) {
		Calendar cal = Calendar.getInstance();
		EmbedBuilder eb = new EmbedBuilder();
		
		eb.withAuthorName(userName);
		eb.withThumbnail(userLogo);

		eb.withColor(252, 236, 10);
		
		eb.appendField("**__What happened:__**", warnInfo, true);
		
		String minute = "" + cal.get(Calendar.MINUTE);
		if (minute.length() == 1)
			minute = "0" + minute;
		
		eb.withFooterText(cal.get(Calendar.HOUR) + ":" + minute + " " + cal.get(Calendar.DAY_OF_MONTH) + "/" + cal.get(Calendar.MONTH) + "/" + cal.get(Calendar.YEAR));
		
		return eb.build();
	}
	
	public static EmbedObject errorEmbed(String userName, String userLogo, String errorInfo) {
		Calendar cal = Calendar.getInstance();
		EmbedBuilder eb = new EmbedBuilder();
		
		eb.withAuthorName(userName);
		eb.withThumbnail(userLogo);

		eb.withColor(252, 26, 10);
		
		eb.appendField("**__What happened:__**", errorInfo, true);
		
		String minute = "" + cal.get(Calendar.MINUTE);
		if (minute.length() == 1)
			minute = "0" + minute;
		
		eb.withFooterText(cal.get(Calendar.HOUR) + ":" + minute + " " + cal.get(Calendar.DAY_OF_MONTH) + "/" + cal.get(Calendar.MONTH) + "/" + cal.get(Calendar.YEAR));
		
		return eb.build();
	}
	
	public static EmbedObject commissionEmbed(String userName, String userLogo, String service, String description) {
		Calendar cal = Calendar.getInstance();
		EmbedBuilder eb = new EmbedBuilder();
		
		eb.withAuthorName(userName);
		eb.withThumbnail(userLogo);

		eb.withColor(9, 252, 26);
		
		eb.appendField("**__Commission for " + service + ":__**", description, true);
		
		String minute = "" + cal.get(Calendar.MINUTE);
		if (minute.length() == 1)
			minute = "0" + minute;
		
		eb.withFooterText(cal.get(Calendar.HOUR) + ":" + minute + " " + cal.get(Calendar.DAY_OF_MONTH) + "/" + cal.get(Calendar.MONTH) + "/" + cal.get(Calendar.YEAR));
		
		return eb.build();
	}
}