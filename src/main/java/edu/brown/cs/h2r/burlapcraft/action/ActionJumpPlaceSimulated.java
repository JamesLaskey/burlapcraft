package edu.brown.cs.h2r.burlapcraft.action;

import net.minecraft.block.Block;
import edu.brown.cs.h2r.burlapcraft.helper.HelperActions;
import edu.brown.cs.h2r.burlapcraft.helper.HelperNameSpace;
import edu.brown.cs.h2r.burlapcraft.stategenerator.StateGenerator;
import burlap.oomdp.core.Domain;
import burlap.oomdp.core.objects.ObjectInstance;
import burlap.oomdp.core.states.State;
import burlap.oomdp.singleagent.GroundedAction;
import burlap.oomdp.singleagent.common.SimpleAction.SimpleDeterministicAction;

public class ActionJumpPlaceSimulated extends SimpleDeterministicAction {

	public ActionJumpPlaceSimulated(String name, Domain domain) {
		super(name, domain);
	}

	@Override
	protected State performActionHelper(State s, GroundedAction groundedAction) {
		StateGenerator.validate(s);
		//get agent and current position
		ObjectInstance agent = s.getFirstObjectOfClass(HelperNameSpace.CLASSAGENT);
		int curX = agent.getIntValForAttribute(HelperNameSpace.ATX);
		int curY = agent.getIntValForAttribute(HelperNameSpace.ATY);
		int curZ = agent.getIntValForAttribute(HelperNameSpace.ATZ);
		
		String blockKey = curX + "," + curY + "," + curZ;
		String blockName = StateGenerator.blockNameMap.get(blockKey);
		
		ObjectInstance block = s.getObject(blockName);
		int blockId = agent.getIntValForAttribute(HelperNameSpace.ATSELECTEDITEMID);
		if (blockId != -1) {
			//Agent has a block in hand to place
			block.setValue(HelperNameSpace.ATBTYPE, blockId);
		
			agent.setValue(HelperNameSpace.ATY, curY + 1);
		}
		StateGenerator.validate(s);
		//return the state we just modified
		System.out.println("\nActionJumpPlaceSimulated performActionHelper\n");
		return s;
	}

}
