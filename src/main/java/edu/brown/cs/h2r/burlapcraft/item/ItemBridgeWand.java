package edu.brown.cs.h2r.burlapcraft.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import burlap.oomdp.singleagent.explorer.TerminalExplorer;
import edu.brown.cs.h2r.burlapcraft.BurlapCraft;
import edu.brown.cs.h2r.burlapcraft.domaingenerator.DomainGeneratorReal;
import edu.brown.cs.h2r.burlapcraft.domaingenerator.DomainGeneratorSimulated;
import edu.brown.cs.h2r.burlapcraft.handler.HandlerDungeonGeneration;
import edu.brown.cs.h2r.burlapcraft.helper.HelperActions;
import edu.brown.cs.h2r.burlapcraft.solver.SolverLearningBridge;
import edu.brown.cs.h2r.burlapcraft.solver.SolverPlanningTinyBridge;
import edu.brown.cs.h2r.burlapcraft.solver.SolverPlanningFinder;
import edu.brown.cs.h2r.burlapcraft.stategenerator.StateGenerator;

public class ItemBridgeWand extends Item {

	// name of item
	private String name = "bridgewand";
	
	// start x, y and z of agent within dungeon
	private double dungeonX = 1.5; 
	private double dungeonY = 2;
	private double dungeonZ = 4;
	
	
	// indicate whether agent is in dungeon or not
	public static boolean bridgeInside;
	
	public ItemBridgeWand() {
		
		// set brideInside to false
		bridgeInside = false;
		
		// give the item a name
		setUnlocalizedName(BurlapCraft.MODID + "_" + name);
		
		// add the item to misc tab
		setCreativeTab(CreativeTabs.tabCombat);
		
		// set texture
		setTextureName(BurlapCraft.MODID + ":" + name);
	}
	
	@Override
	public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer player)
    {
		if (!world.isRemote) {
			HelperActions.destroyBlock();
		}
		
        return itemStack;
    }
}
