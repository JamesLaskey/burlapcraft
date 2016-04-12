package edu.brown.cs.h2r.burlapcraft.action;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import burlap.behavior.policy.Policy.ActionProb;
import burlap.behavior.singleagent.options.Option;
import burlap.oomdp.core.Domain;
import burlap.oomdp.core.objects.ObjectInstance;
import burlap.oomdp.core.states.State;
import burlap.oomdp.singleagent.Action;
import burlap.oomdp.singleagent.GroundedAction;
import burlap.oomdp.singleagent.common.SimpleAction.SimpleDeterministicAction;
import edu.brown.cs.h2r.burlapcraft.helper.HelperActions;
import edu.brown.cs.h2r.burlapcraft.helper.HelperNameSpace;
import edu.brown.cs.h2r.burlapcraft.helper.HelperPos;
import edu.brown.cs.h2r.burlapcraft.stategenerator.StateGenerator;

public class ActionRedstoneParameterizedOptionSimulated extends Option {
	
	private int[][][] map;
	private int searchRadius = 1;
	
	public ActionRedstoneParameterizedOptionSimulated(String name, Domain domain, int[][][] map, 
			int searchRadius) {
		super(name, domain);
		this.map = map;
		this.searchRadius = searchRadius;
	}

	@Override
	public boolean isMarkov() {
		return true;
	}

	@Override
	public boolean usesDeterministicTermination() {
		return true;
	}

	@Override
	public boolean usesDeterministicPolicy() {
		return true;
	}

	@Override
	public double probabilityOfTermination(State s,
			GroundedAction groundedAction) {
		int height = Integer.valueOf(groundedAction.getParametersAsString()[0]);
		ObjectInstance agent = s.getFirstObjectOfClass(HelperNameSpace.CLASSAGENT);
		int curY = agent.getIntValForAttribute(HelperNameSpace.ATY);
		int startY = Integer.valueOf(groundedAction.getParametersAsString()[1]);
		int iters = Integer.valueOf(groundedAction.getParametersAsString()[2]);

		if (iters == height) {
			groundedAction.getParametersAsString()[2] = new Integer(0).toString();
			return 1.;
		} else {
			groundedAction.getParametersAsString()[2] = new Integer(iters + 1).toString();
			return 0.;
		}
	}

	@Override
	public void initiateInStateHelper(State s, GroundedAction groundedAction) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public GroundedAction oneStepActionSelection(State s,
			GroundedAction groundedAction) {
		Action jumpPlace = new ActionJumpPlaceSimulated(HelperNameSpace.ACTIONJUMPANDPLACE, domain, map);
		return jumpPlace.getAssociatedGroundedAction();
	}

	@Override
	public List<ActionProb> getActionDistributionForState(State s,
			GroundedAction groundedAction) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean applicableInState(State s, GroundedAction groundedAction) {
		return true;
	}

	@Override
	public boolean isParameterized() {
		return true;
	}

	@Override
	public GroundedAction getAssociatedGroundedAction() {
		// TODO Auto-generated method stub
		return null;
	}

	public static class SimpleParameterizedGroundedAction extends GroundedAction {

		String[] params;
		
		public SimpleParameterizedGroundedAction(Action action, String[] params) {
			super(action);
			this.params = params;
		}

		@Override
		public void initParamsWithStringRep(String[] params) {
			this.params = params;
		}

		@Override
		public String[] getParametersAsString() {
			return params;
		}

		@Override
		public GroundedAction copy() {
			return new SimpleParameterizedGroundedAction(action, params);
		}
	}
	
	@Override
	public List<GroundedAction> getAllApplicableGroundedActions(State s) {
		List<GroundedAction> actions = new ArrayList<GroundedAction>();
		
		ObjectInstance agent = s.getFirstObjectOfClass(HelperNameSpace.CLASSAGENT);
		int currentItemID = agent.getIntValForAttribute(HelperNameSpace.ATSELECTEDITEMID);
		if (currentItemID != 331) { //redstone ID = 331
			return actions;
		}
		//get inventoryBlocks
		int numBlocks = 0;
		List<ObjectInstance> invBlocks = s.getObjectsOfClass(HelperNameSpace.CLASSINVENTORYBLOCK);
		for (ObjectInstance invBlock : invBlocks) {
			if (invBlock.getIntValForAttribute(HelperNameSpace.ATBTYPE) == currentItemID) {
				numBlocks++;
			}
		}
		
		int curX = agent.getIntValForAttribute(HelperNameSpace.ATX);
		int curZ = agent.getIntValForAttribute(HelperNameSpace.ATZ);
		int width = Math.min(map[0].length, curX + searchRadius);
		int length = Math.min(map[0][0].length, curZ + searchRadius);
		for (int i = Math.max(0, curX - searchRadius); i < width; i++) {
			for (int j = Math.max(0, curZ - searchRadius); j < length; j++) {
				if (numBlocks < (Math.abs(curX - i) + Math.abs(curZ - j))) {
					//not enough redstone to complete to the selected target block
					continue;
				}
				GroundedAction a = new SimpleParameterizedGroundedAction(this, 
						new String[]{
							new Integer(i).toString(),  //x spot to redstone to
							new Integer(j).toString(), //z spot to redstone to
							new Integer(curX).toString(), //starting X
							new Integer(curZ).toString(), //starting Z
							new Integer(numBlocks).toString(), //starting amount of redstone
							new Integer(0).toString() //number of iterations attempted
						});
				actions.add(a);
			}
		}
		return actions;
	}
}

