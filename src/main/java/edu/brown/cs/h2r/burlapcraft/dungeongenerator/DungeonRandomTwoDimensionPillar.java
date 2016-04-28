package edu.brown.cs.h2r.burlapcraft.dungeongenerator;

import java.util.Random;

import edu.brown.cs.h2r.burlapcraft.helper.HelperGeometry.Pose;
import net.minecraft.block.Block;
import net.minecraft.world.World;

public class DungeonRandomTwoDimensionPillar extends Dungeon {

	public DungeonRandomTwoDimensionPillar(String name, Pose _pose, int maxLength, int maxWidth, int maxHeight) {
		super(name, _pose, maxLength, maxWidth, maxHeight, Pose.fromXyz(0.5, 1, 0.5));
	}

	@Override
	protected void generate(World world, int x, int y, int z) {
		Random random = new Random();
		
		int l = getLength() - 3;
		int w = getWidth() - 3;
		int h = getHeight() - 3;
		
		int [][] heights = new int[getLength()][getWidth()];
		
		// generate random heights for hills
		for (int i = 0; i < l; i++) {
			for (int j = 0; j < w; j++) {
				heights[i][j] = random.nextInt(h - 1) + 1;
			}
		}
		heights[0][0] = 1;
		
		for (int i = 0; i < l; i++) {
			for (int j = 0; j < w; j++) {
				for (int k = 0; k < heights[i][j]; k++) {
					world.setBlock(x + i, y + k, z + j, Block.getBlockById(7));
				}
				for (int k = heights[i][j]; k < h; k++) {
					world.setBlock(x + i, y + k, z + j, Block.getBlockById(0));
				}
			}
		}
		
		world.setBlock(x + l, y + h - 2, z + w, Block.getBlockById(41));
	}

}
