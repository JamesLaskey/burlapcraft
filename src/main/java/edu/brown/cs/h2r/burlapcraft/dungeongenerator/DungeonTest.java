package edu.brown.cs.h2r.burlapcraft.dungeongenerator;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import edu.brown.cs.h2r.burlapcraft.BurlapCraft;
import edu.brown.cs.h2r.burlapcraft.helper.HelperGeometry.Pose;

public class DungeonTest extends Dungeon {
	
	public DungeonTest(Pose pose) {
		super("test", pose, 4, 5, 4, Pose.fromXyz(1, 5, 3));
		
	}

    private void generatePlane(World world, int x, int y, int z, 
            int xLen, int yLen, int zLen, Block block) {
        for (int i = 0; i < xLen; i++) {
            for (int j = 0; j < yLen; j++) {
                for (int k = 0; k < zLen; k++) {
                    world.setBlock(x + i, y + j, z + k, block);
                }
            }
        }
    }

    private void generateRoom(World world, int x, int y, int z, 
            int xLen, int yLen, int zLen, int doorX, int doorY, 
            Block block) {
        //make floor
        generatePlane(world, x, y, z, xLen, yLen, 1, block);
        //make walls
        generatePlane(world, x, y, z, xLen, 1, zLen, block);
        generatePlane(world, x, y, z, 1, yLen, zLen, block);
        generatePlane(world, x, y + yLen, z, xLen, 1, zLen, block);
        generatePlane(world, x + xLen, y, z, 1, yLen, zLen, block);
        //make door
        for (int i = 0; i < zLen; i++) {
            world.setBlock(doorX, doorY, z + i, Block.getBlockById(0));
        }
    }

	@Override
	protected void generate(World world, int x, int y, int z) {
		System.out.println("Making test dungeon at " + x + "," + y + "," + z);
	    generateRoom(world, x, y, z, 10, 10, 5, x + 3, y, Block.getBlockById(7));
	}
	

}
