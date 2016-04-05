package edu.brown.cs.h2r.burlapcraft.dungeongenerator;

import java.util.Random;

import edu.brown.cs.h2r.burlapcraft.helper.HelperGeometry.Pose;
import net.minecraft.block.Block;
import net.minecraft.world.World;
import scala.actors.threadpool.Arrays;

public class DungeonRandomOneDimensionPillar extends Dungeon {

	public DungeonRandomOneDimensionPillar(String name, Pose _pose, int maxLength, int maxHeight) {
		super(name, _pose, maxLength, maxLength, maxHeight, Pose.fromXyz(2.5, 1, 1.5));
	}

	@Override
	protected void generate(World world, int x, int y, int z) {
		Random r = new Random();
		
		int l = getLength() - 3;
		int h = getHeight() - 3;
		
		int[] heights = new int[getLength() - 1];
		
		heights[0] = 1;
		
		// generate random heights for cliffs
		for (int i = 1; i < l; i++) {
			heights[i] = r.nextInt(h - 1) + 1;
		}
		
		// have cliffs be ascending
		Arrays.sort(heights);
		
		// set blocks for various cliff heights, fill with air for remainder of height
		for (int i = 0; i < l; i++) {
			int height = heights[i];
			for (int j = 0; j < height; j++) {
				world.setBlock(x + i, y + j, z, Block.getBlockById(7));
				world.setBlock(x + i, y + j, z + 1, Block.getBlockById(7));
				world.setBlock(x + i, y + j, z + 2, Block.getBlockById(7));
			}
			for (int j = height; j < h; j++) {
				world.setBlock(x + i, y + j, z, Block.getBlockById(0));
				world.setBlock(x + i, y + j, z + 1, Block.getBlockById(0));
				world.setBlock(x + i, y + j, z + 2, Block.getBlockById(0));
			}
		}
		// set goal block as topmost, farthest part of dungeon
		world.setBlock(x + l, y + h, z + 1, Block.getBlockById(41));
	}
	
	
	
}
