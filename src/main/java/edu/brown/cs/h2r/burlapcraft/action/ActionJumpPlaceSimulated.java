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
		
		if (map[curY + 2][curX][curZ] != -1) {
			//check if there is headspace
			StateGenerator.validate(s);
			return s;
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
		System.out.println("\nActionJumpPlaceSimulated performActionHelper\n");
		return s;
	}
	
	private State simulatePlaceBlockBelow(State s, int curX, int curY, int curZ, ObjectInstance invBlock, ObjectInstance agent) {
		boolean anchorsPresent = false;
		ObjectInstance replaceBlock = null;
		if (map[curY - 1][curX][curZ] > 0) {
			//block below feet
			anchorsPresent = true;
		}
		
		if (anchorsPresent) {
			int blockID = invBlock.getIntValForAttribute(HelperNameSpace.ATBTYPE);
			
			Set<String> blockNames = invBlock.getAllRelationalTargets(HelperNameSpace.ATBLOCKNAMES);
			String blockName = blockNames.iterator().next();
			if (blockNames.size() == 1) {
				s.removeObject(invBlock);
				agent.setValue(HelperNameSpace.ATSELECTEDITEMID, -1);
			}
			else {
				blockNames.remove(blockName);
			}
			
			ObjectInstance newBlock = new MutableObjectInstance(domain.getObjectClass(HelperNameSpace.CLASSBLOCK), blockName);
			newBlock.setValue(HelperNameSpace.ATX, curX);
			newBlock.setValue(HelperNameSpace.ATY, curY - 1);
			newBlock.setValue(HelperNameSpace.ATZ, curZ + 1);
			newBlock.setValue(HelperNameSpace.ATBTYPE, blockID);
			s.addObject(newBlock);
			
			agent.setValue(HelperNameSpace.ATY, curY + 1);
		}
		StateGenerator.validate(s);
		return s;
	}
}
