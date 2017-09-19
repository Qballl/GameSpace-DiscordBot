package com.pzg.www.discord.config;

import java.util.List;

public class Config {
	
	private File conf;
	
	public Config(File conf) {
		this.conf = conf;
	}
	
	public void set(String path, String object) {
		List<String> a = conf.readSmallTextFile();
		int lineNum = a.size();
		boolean set = false;
		for (String b : a) {
			if (b.contains(path + ": -")) {
				set = true;
				lineNum = a.indexOf(b);
			}
		}
		if (set) {
			System.out.println("Set");
			a.set(lineNum, path + ": -" + object);
		} else {
			System.out.println("Added");
			a.add(path + ": -" + object);
		}
		conf.writeSmallTextFile(a);
	}
	
	public String getObject(String path) {
		List<String> a = conf.readSmallTextFile();
		String ret = "";
		for (String b : a) {
			if (b.contains(path + ": -")) {
				ret = b.replace(path + ": -", "");
			}
		}
		return ret;
	}
}