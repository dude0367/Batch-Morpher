package com.knight.batchmorpher;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class BatcherMorpher {

	public static ArrayList<Morpher> morphers;

	public static void main(String args[]) {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String path = "";//"C:\\Users\\New User\\Documents\\School\\Sophomore\\Java\\pictures"; 
		int frames = 30;
		int pause = 30;
		try {
			System.out.println("Enter the path of the folder (of folders...)");
			path = br.readLine();
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
			morphers.add(new Morpher(f.getAbsolutePath(), frames, pause));
		}
		System.out.println("Path set to: " + path + ", there are " + morphers.size() + " folders. Press enter to continue...");
		try {
			br.readLine();
		} catch (Exception e) {
		}
		for(Morpher m : morphers) {
			m.morph();
			m.save();
		}
		System.out.println("Done.");
	}

}
