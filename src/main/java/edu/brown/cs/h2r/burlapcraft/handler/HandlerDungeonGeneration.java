package edu.brown.cs.h2r.burlapcraft.handler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import cpw.mods.fml.common.IWorldGenerator;
import edu.brown.cs.h2r.burlapcraft.BurlapCraft;
import edu.brown.cs.h2r.burlapcraft.dungeongenerator.Dungeon;
import edu.brown.cs.h2r.burlapcraft.dungeongenerator.DungeonCleanUp;
import edu.brown.cs.h2r.burlapcraft.dungeongenerator.DungeonFinder;
import edu.brown.cs.h2r.burlapcraft.dungeongenerator.DungeonFourRooms;
import edu.brown.cs.h2r.burlapcraft.dungeongenerator.DungeonGrid;
import edu.brown.cs.h2r.burlapcraft.dungeongenerator.DungeonMaze0;
import edu.brown.cs.h2r.burlapcraft.dungeongenerator.DungeonMaze1;
import edu.brown.cs.h2r.burlapcraft.dungeongenerator.DungeonRandomOneDimensionPillar;
import edu.brown.cs.h2r.burlapcraft.dungeongenerator.DungeonRandomTwoDimensionPillar;
import edu.brown.cs.h2r.burlapcraft.dungeongenerator.DungeonSmallBridge;
import edu.brown.cs.h2r.burlapcraft.dungeongenerator.DungeonTest;
import edu.brown.cs.h2r.burlapcraft.dungeongenerator.DungeonTinyBridge;
import edu.brown.cs.h2r.burlapcraft.dungeongenerator.FileDungeon;
import edu.brown.cs.h2r.burlapcraft.dungeongenerator.ReadDungeon;
import edu.brown.cs.h2r.burlapcraft.helper.HelperGeometry.Pose;
import edu.brown.cs.h2r.burlapcraft.helper.HelperPos;

public class HandlerDungeonGeneration implements IWorldGenerator {
	
	public static Pose playerSpawnPose;
	private static Minecraft mc = Minecraft.getMinecraft();
	public static boolean dungeonsCreated = false;
	public static boolean currentlyGeneratingDungeons = false;

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world,
			IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
		// TODO Auto-generated method stub

		switch (world.provider.dimensionId) {

		case -1:
			generateNether(world, random, chunkX * 16, chunkZ * 16);

		case 0:
			generateSurface(world, random, chunkX * 16, chunkZ * 16);

		case 1:
			generateEnd(world, random, chunkX * 16, chunkZ * 16);

		}

	}

	private void generateEnd(World world, Random random, int i, int j) {

	}

	private void generateNether(World world, Random random, int i, int j) {

	}

	private void generateSurface(World world, Random random, int i, int j) {
		if(mc.thePlayer != null && !dungeonsCreated) {
			doCreateDungeons(world);
		}
	}
	
	public static void doCreateDungeons(World world) {
		if (currentlyGeneratingDungeons) {
			return;
		} else {
			currentlyGeneratingDungeons = true;
		}
		ChunkCoordinates coordinates = world.getSpawnPoint();
		
		//playerSpawnPos = getPlayerPosition();
		playerSpawnPose = Pose.fromXyz(coordinates.posX, 30, coordinates.posZ);
		int height = 50;
		List<Pose> poses = new ArrayList<Pose>();
		Pose testPose = Pose.fromXyz(playerSpawnPose.getX() - 20, playerSpawnPose.getY() + height, playerSpawnPose.getZ());
		
		List<Pose> poses2d = new ArrayList<Pose>();
		
		int xIncOffset = 40;
		int numDungeons = 30;
		
		int numDungeons2d = 1;
		for (int i = 0; i < numDungeons; i++) {
			poses.add(Pose.fromXyz(playerSpawnPose.getX() + (i * xIncOffset), 
					playerSpawnPose.getY() + height, playerSpawnPose.getZ()));
		}
		
		for (int i = numDungeons; i < numDungeons + numDungeons2d; i++) {
			poses2d.add(Pose.fromXyz(playerSpawnPose.getX() + (i * xIncOffset), 
					playerSpawnPose.getY() + height, playerSpawnPose.getZ()));
		}
		
		int n = 1;
		for (Pose pose : poses) {
			String name = "pillar" + new Integer(n++).toString();
			BurlapCraft.registerDungeon(new DungeonRandomOneDimensionPillar(name, pose, 15, 20));
		}
		
		n = 1;
		for (Pose pose : poses2d) {
			String name = "2dpillar" + new Integer(n++).toString();
			//BurlapCraft.registerDungeon(new DungeonRandomTwoDimensionPillar(name, pose, (n * 2) + 4, (n * 2) + 4, n > 10 ? 40 : 15));
		}
		
		// @note the reason why it's not making more than one dungeon is because the poses are the same.
		// One's overwriting the next
		
		for (Dungeon d : BurlapCraft.dungeons) {
			d.regenerate(world);
		}
		
		dungeonsCreated = true;
		currentlyGeneratingDungeons = false;
	}
	
	private static HelperPos getPlayerPosition() {
	  
		int x = MathHelper.floor_double(mc.thePlayer.posX);
		int y = MathHelper.floor_double(mc.thePlayer.boundingBox.minY + 0.05D);
		int z = MathHelper.floor_double(mc.thePlayer.posZ);
		
		return new HelperPos(x, y, z);
    
	}

}
