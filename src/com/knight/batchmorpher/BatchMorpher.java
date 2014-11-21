package com.knight.batchmorpher;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class BatchMorpher {

	public static ArrayList<Morpher> morphers;

	public static void main(String args[]) {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String path = "Z:\\java\\pictures"; //"";
		int frames = 30;
		int pause = 30;
		int width = 640; //128
		int height = 800; //160
		try {
			System.out.println("Enter the path of the folder (of folders...)");
			path = "Z:\\Aurasma project\\2015 Output";//br.readLine();
			System.out.println("Enter your desired width (leave blank for default)");
			String in = "";
			in = br.readLine();
			if(!in.equals("")) width = Integer.valueOf(in);
			System.out.println("Enter your desired height (leave blank for default)");
			in = br.readLine();
			if(!in.equals("")) height = Integer.valueOf(in);
			System.out.println("Enter number of frames per transition (30/second)");
			frames = Integer.valueOf(br.readLine());
			System.out.println("Enter number of frames to pause on each picture (30/second)");
			pause = Integer.valueOf(br.readLine());
		} catch (Exception e) {
			e.printStackTrace();
		}
		File folderOfFolders = new File(path);
		//System.out.println(folderOfFolders.listFiles()[0].getAbsolutePath());
		morphers = new ArrayList<Morpher>();
		for(File f : folderOfFolders.listFiles()) {
			if(width != 0 && height != 0) {
				morphers.add(new Morpher(f.getAbsolutePath(), frames, pause, width, height));
			} else {
				morphers.add(new Morpher(f.getAbsolutePath(), frames, pause));
			}
		}
		System.out.println("Path set to: " + path + ", there are " + morphers.size() + " folders. Press enter to continue...");
		try {
			br.readLine();
		} catch (Exception e) {}
		for(Morpher m : morphers) {
			m.morph();
			m.save();
		}
		System.out.println("Done.");
	}

}
