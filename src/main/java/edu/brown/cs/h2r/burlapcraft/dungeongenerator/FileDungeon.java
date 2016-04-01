package edu.brown.cs.h2r.burlapcraft.dungeongenerator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import cern.colt.Arrays;
import edu.brown.cs.h2r.burlapcraft.helper.HelperGeometry.Pose;
import net.minecraft.block.Block;
import net.minecraft.world.World;

public abstract class FileDungeon extends Dungeon {
	
	protected static final String DIRECTORY = "dungeons/"; 
	
	protected static class DungeonMap {
		private int [][][] map;
		
		public DungeonMap(int length, int width, int height) {
			map = new int[height][length][width];
		}
		
		public int get(int x, int y, int z) {
			return map[y][z][x];
		}
		
		public void set(int block, int x, int y, int z) {
			map[y][z][x] = block;
		}
	}
	
	protected String filename;
	// whenever you add a block to the world from an initial pose
	// with an offset dx, dy, dz, call map.set(blockid, dx, dy, dz)
	protected DungeonMap map;
	
	public FileDungeon(String filename, Pose _pose) throws IOException {
		super(filename.split("\\.")[0], _pose,
				Integer.parseInt(filename.split("\\.")[1].split("x")[2]),
				Integer.parseInt(filename.split("\\.")[1].split("x")[0]),
				Integer.parseInt(filename.split("\\.")[1].split("x")[1]),
				null);

		this.filename = filename;
		
		map = new DungeonMap(getLength(), getWidth(), getHeight());
	}
	
	public void write() throws IOException {
		
		StringBuilder s = new StringBuilder();
		for (int i = 0; i < getHeight(); i++) {
			for (int j = 0; j < getLength(); j++) {
				for (int k = 0; k < getLength(); k++) {
					s.append(map.get(k, i, j));
					s.append(" ");
				}
				s.append("\n");
			}
			s.append("\n");
		}
		
		PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(DIRECTORY + filename)));
		out.write(s.toString());
		out.flush();
		out.close();
	}
}
