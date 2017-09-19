package com.pzg.www.discord.config;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class File {

	Path file;

	public File(String path) {
		String loc = this.getClass().getProtectionDomain().getCodeSource().getLocation().toString().replace("consolebot.jar", "").replace("\\", "/").replace("file:", "") + path;
		loc = loc.replace("/C:", "");
		loc = loc.replace("/D:", "");
		loc = loc.replace("/G:", "");
		loc = loc.replace("/F:", "");
		loc = loc.replace("/E:", "");
		loc = loc.replace("/./", "/");
		System.out.println(loc);
		file = Paths.get(loc);
		try {
			Files.createFile(file);
		} catch (FileAlreadyExistsException e) {
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	final static Charset ENCODING = StandardCharsets.UTF_8;

	//For smaller files

	/**
	   Note: the javadoc of Files.readAllLines says it's intended for small
	   files. But its implementation uses buffering, so it's likely good 
	   even for fairly large files.
	 */  
	public List<String> readSmallTextFile() {
		try {
			return Files.readAllLines(file, ENCODING);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	void writeSmallTextFile(List<String> aLines) {
		try {
			Files.write(file, aLines, ENCODING);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}