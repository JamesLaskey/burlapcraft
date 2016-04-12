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

public class ActionPillarParameterizedOptionSimulated extends Option {
	
	private int[][][] map;
	private int minPillarHeight = 1;
	private int maxPillarHeight = 12;

	public ActionPillarParameterizedOptionSimulated(String name, Domain domain, int[][][] map, 
			int minPillarHeight, int maxPillarHeight) {
		super(name, domain);
		this.map = map;
		this.minPillarHeight = minPillarHeight;
		this.maxPillarHeight = maxPillarHeight;
	}
	
	public ActionPillarParameterizedOptionSimulated(String name, Domain domain, int[][][] map) {
		super(name, domain);
		this.map = map;
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
		
//		if (curY >= (startY + height)) {
//			return 1.;
//		} else {
//			return 0.;
//		}
	}

	@Override
	public void initiateInStateHelper(State s, GroundedAction groundedAction) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public GroundedAction oneStepActionSelection(State s,
			GroundedAction groundedAction) {
		Action jumpPlace = new ActionJumpPlaceSimulated(HelperNameSpace.ACTIONJUMPANDPLACE, domain, map);
		GroundedAction a = jumpPlace.getAssociatedGroundedAction();
		a.getParametersAsString()[0] = groundedAction.getParametersAsString()[2];
		return a;
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
	
	private GroundedAction getHeuristicBestAction(State s) {
		int heuristicHeight = 0;
		ObjectInstance agent = s.getFirstObjectOfClass(HelperNameSpace.CLASSAGENT);
		int curY = agent.getIntValForAttribute(HelperNameSpace.ATY);
		int curX = agent.getIntValForAttribute(HelperNameSpace.ATX);
		int curZ = agent.getIntValForAttribute(HelperNameSpace.ATZ);
		int rotDir = agent.getIntValForAttribute(HelperNameSpace.ATROTDIR);
		
		int xdelta = 0;
		int zdelta = 0;
		if(rotDir == 0){
			zdelta = 1;
		}
		else if(rotDir == 1){
			xdelta = -1;
		}
		else if(rotDir == 2){
			zdelta = -1;
		}
		else{
			xdelta = 1;
		}
		
		int nx = curX + xdelta;
		int nz = curZ + zdelta;
		
		if (nx < 0 || nx > map[0].length || nz < 0 || nz > map[0][0].length) {
			nx = curX;
			nz = curZ;
		}
		
		for (int i = curY; i < map.length; i++) {
			if (map[i][nx][nz] >= 1) {
				heuristicHeight = i;
			}
		}
		
		GroundedAction a = new SimpleParameterizedGroundedAction(this, 
				new String[]{
					new Integer(heuristicHeight).toString(),  //height of pillar
					new Integer(curY).toString(), //current height
					new Integer(0).toString() //number of iterations attempted
				});
		return a;
	}
	
	@Override
	public List<GroundedAction> getAllApplicableGroundedActions(State s) {
		List<GroundedAction> actions = new ArrayList<GroundedAction>();
		
		ObjectInstance agent = s.getFirstObjectOfClass(HelperNameSpace.CLASSAGENT);
		int currentItemID = agent.getIntValForAttribute(HelperNameSpace.ATSELECTEDITEMID);
//		if (currentItemID != 4) { //cobblestone ID = 4
//			return actions;
//		}
		//get inventoryBlocks
		int numBlocks = 0;
		List<ObjectInstance> invBlocks = s.getObjectsOfClass(HelperNameSpace.CLASSINVENTORYBLOCK);
		for (ObjectInstance invBlock : invBlocks) {
			if (invBlock.getIntValForAttribute(HelperNameSpace.ATBTYPE) == currentItemID) {
				numBlocks++;
			}
		}
		
		int curY = agent.getIntValForAttribute(HelperNameSpace.ATY);
		for (int i = minPillarHeight; i < Math.min(maxPillarHeight, numBlocks); i++) {
			GroundedAction a = new SimpleParameterizedGroundedAction(this, 
					new String[]{
						new Integer(i).toString(),  //height of pillar
						new Integer(curY).toString(), //current height
						new Integer(0).toString() //number of iterations attempted
					});
			actions.add(a);
		}
		
		//actions.add(getHeuristicBestAction(s));
		return actions;
	}

	public int getMinPillarHeight() {
		return minPillarHeight;
	}
}

