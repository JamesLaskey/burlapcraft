package com.kcaluru.burlapbot.items;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.RegistryNamespaced;
import net.minecraft.world.World;

import com.kcaluru.burlapbot.BurlapMod;
import com.kcaluru.burlapbot.BurlapWorldGenHandler;
import com.kcaluru.burlapbot.helpers.BurlapAIHelper;
import com.kcaluru.burlapbot.helpers.NameSpace;
import com.kcaluru.burlapbot.solver.DungeonOneSolver;
import com.kcaluru.burlapbot.test.BFSMovement;

import cpw.mods.fml.common.registry.GameData;

public class ItemFinderWand extends Item {
	
	// name of item
	private String name = "finderwand";
	
	// finder dungeon map
	final int [][][] finderMap = {
			{
				{7,7,7,7,7,7,7,7,7,7,7},
				{7,7,7,7,7,7,7,7,7,7,7},
				{7,7,7,7,7,7,7,7,7,7,7},
				{7,7,7,7,7,7,7,7,7,7,7},
				{7,7,7,7,7,7,7,7,7,7,7},
				{7,7,7,7,7,7,7,7,7,7,7},
				{7,7,7,7,7,7,7,7,7,7,7},
				{7,7,7,7,7,7,7,7,7,7,7},
				{7,7,7,7,7,7,7,7,7,7,7},
				{7,7,7,7,7,7,7,7,7,7,7},
				{7,7,7,7,7,7,7,7,7,7,7}
			},
			{
				{7,7,7,7,7,7,7,7,7,7,7},
				{7,0,0,0,0,0,0,0,0,0,7},
				{7,41,0,0,0,7,0,0,0,0,7},
				{7,0,0,0,0,7,0,0,0,0,7},
				{7,0,0,0,0,0,0,0,0,0,7},
				{7,7,7,7,7,7,7,7,0,0,7},
				{7,0,0,0,0,7,0,0,0,0,7},
				{7,0,0,0,0,0,0,0,0,0,7},
				{7,0,0,0,0,0,0,0,0,0,7},
				{7,0,0,0,0,7,0,0,0,0,7},
				{7,7,7,7,7,7,7,7,7,7,7}
			},
			{
				{7,7,7,7,7,7,7,7,7,7,7},
				{7,0,0,0,0,0,0,0,0,0,7},
				{7,7,0,0,0,7,0,0,0,0,7},
				{7,0,0,0,0,7,0,0,0,0,7},
				{7,0,0,0,0,0,0,0,0,0,7},
				{7,7,7,7,7,7,7,7,0,0,7},
				{7,0,0,0,0,7,0,0,0,0,7},
				{7,0,0,0,0,0,0,0,0,0,7},
				{7,0,0,0,0,0,0,0,0,0,7},
				{7,0,0,0,0,7,0,0,0,0,7},
				{7,7,7,7,7,7,7,7,7,7,7}
			},
			{
				{7,7,7,7,7,7,7,7,7,7,7},
				{7,7,7,7,7,7,7,7,7,7,7},
				{7,7,7,7,7,7,7,7,7,7,7},
				{7,7,7,7,7,7,7,7,7,7,7},
				{7,7,7,7,7,7,7,7,7,7,7},
				{7,7,7,7,7,7,7,7,7,7,7},
				{7,7,7,7,7,7,7,7,7,7,7},
				{7,7,7,7,7,7,7,7,7,7,7},
				{7,7,7,7,7,7,7,7,7,7,7},
				{7,7,7,7,7,7,7,7,7,7,7},
				{7,7,7,7,7,7,7,7,7,7,7}
			}
	};
	
	// indicate whether agent is in dungeon or not
	public static boolean finderInside;
	
	public ItemFinderWand() {
		
		// set finderInside to false
		finderInside = false;
		
		// give the item a name
		setUnlocalizedName(BurlapMod.MODID + "_" + name);
		
		// add the item to misc tab
		setCreativeTab(CreativeTabs.tabCombat);
		
		// set texture
		setTextureName(BurlapMod.MODID + ":" + name);
	}
	
	@Override
	public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer player) {
		if(!world.isRemote) {
			if (finderInside) {
				
				int posX = (int) player.posX;
				int posZ = (int) player.posZ;
				
				ArrayList<Block> blockList = new ArrayList<Block>();
				
				DungeonOneSolver solver = new DungeonOneSolver(finderMap, Math.abs(posX - BurlapWorldGenHandler.posX), Math.abs(posZ - BurlapWorldGenHandler.posZ), 2, 1);
				solver.BFS();
	
			}
			else {
				ItemBridgeWand.bridgeInside = false;
				player.setPositionAndUpdate((double) BurlapWorldGenHandler.posX + 8.5, (double) BurlapWorldGenHandler.posY + 41, (double) BurlapWorldGenHandler.posZ + 2.5);
				finderInside = true;
			}
		}
		
		return itemStack;
	}
}
