package edu.brown.cs.h2r.burlapcraft.action;

import java.util.List;
import java.util.Set;

import net.minecraft.block.Block;
import edu.brown.cs.h2r.burlapcraft.helper.HelperActions;
import edu.brown.cs.h2r.burlapcraft.helper.HelperNameSpace;
import edu.brown.cs.h2r.burlapcraft.stategenerator.StateGenerator;
import burlap.oomdp.core.Domain;
import burlap.oomdp.core.objects.MutableObjectInstance;
import burlap.oomdp.core.objects.ObjectInstance;
import burlap.oomdp.core.states.State;
import burlap.oomdp.singleagent.GroundedAction;
import burlap.oomdp.singleagent.common.SimpleAction.SimpleDeterministicAction;

public class ActionJumpPlaceSimulated extends SimpleDeterministicAction {

	int[][][] map;
	public ActionJumpPlaceSimulated(String name, Domain domain, int[][][] map) {
		super(name, domain);
		this.map= map;
	}

	@Override
	protected State performActionHelper(State s, GroundedAction groundedAction) {
		StateGenerator.validate(s);
		//get agent and current position
		
		ObjectInstance agent = s.getFirstObjectOfClass(HelperNameSpace.CLASSAGENT);
		int curX = agent.getIntValForAttribute(HelperNameSpace.ATX);
		int curY = agent.getIntValForAttribute(HelperNameSpace.ATY);
		int curZ = agent.getIntValForAttribute(HelperNameSpace.ATZ);
		int currentItemID = agent.getIntValForAttribute(HelperNameSpace.ATSELECTEDITEMID);

		//get block objects and their positions
		List<ObjectInstance> blocks = s.getObjectsOfClass(HelperNameSpace.CLASSBLOCK);
		for (ObjectInstance block : blocks) {
			int blockX = block.getIntValForAttribute(HelperNameSpace.ATX);
			int blockY = block.getIntValForAttribute(HelperNameSpace.ATY);
			int blockZ = block.getIntValForAttribute(HelperNameSpace.ATZ);
			if ((curY + 2 < map.length && map[curY + 2][curX][curZ] != 0) ||
					(blockX == curX && blockZ == curZ && blockY == (2 + curY)
					|| curY + 2 >= map.length)) {
				//block is above our head
				StateGenerator.validate(s);
				System.out.println(s);
				System.out.println("failing, no headspace ");
				System.out.println(curY + 2 >= map.length ? "top of map" : map[curY + 2][curX][curZ]);
				System.out.println(block.getName() + " " + blockX + " " + blockY + " " + blockZ);
				return s;
			}
		}
		

		//get inventoryBlocks
		List<ObjectInstance> invBlocks = s.getObjectsOfClass(HelperNameSpace.CLASSINVENTORYBLOCK);
		for (ObjectInstance invBlock : invBlocks) {
			if (invBlock.getIntValForAttribute(HelperNameSpace.ATBTYPE) == currentItemID) {
				return simulatePlaceBlockBelow(s, curX, curY, curZ, invBlock, agent);
			}
		}

		StateGenerator.validate(s);
		//return the state we just modified
		return s;
	}
	
	private State simulatePlaceBlockBelow(State s, int curX, int curY, int curZ, ObjectInstance invBlock, ObjectInstance agent) {
		int blockID = invBlock.getIntValForAttribute(HelperNameSpace.ATBTYPE);
		System.out.println(s);
		s.removeObject(invBlock);
			
		String blockName = invBlock.getName();
		ObjectInstance newBlock = new MutableObjectInstance(domain.getObjectClass(HelperNameSpace.CLASSBLOCK), blockName);
		newBlock.setValue(HelperNameSpace.ATX, curX);
		newBlock.setValue(HelperNameSpace.ATY, curY);
		newBlock.setValue(HelperNameSpace.ATZ, curZ);
		newBlock.setValue(HelperNameSpace.ATBTYPE, blockID);
		s.addObject(newBlock);
		
		int prevY = agent.getIntValForAttribute(HelperNameSpace.ATY);
		
		agent.setValue(HelperNameSpace.ATY, curY + 1);
		//System.out.println(agent.getIntValForAttribute(HelperNameSpace.ATY) + " " + prevY);
		System.out.println(s);
		StateGenerator.validate(s);
		return s;
	}
}
