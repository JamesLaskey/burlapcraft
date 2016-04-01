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

public class ActionPillarParametrizedOptionSimulated extends Option {
	
	private int[][][] map;
	int minPillarHeight = 0;
	int maxPillarHeight = 12;

	public ActionPillarParametrizedOptionSimulated(String name, Domain domain, int[][][] map, 
			int minPillarHeight, int maxPillarHeight) {
		super(name, domain);
		this.map = map;
		this.minPillarHeight = minPillarHeight;
		this.maxPillarHeight = maxPillarHeight;
	}
	
	public ActionPillarParametrizedOptionSimulated(String name, Domain domain, int[][][] map) {
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
		return 1;
	}

	@Override
	public void initiateInStateHelper(State s, GroundedAction groundedAction) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public GroundedAction oneStepActionSelection(State s,
			GroundedAction groundedAction) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ActionProb> getActionDistributionForState(State s,
			GroundedAction groundedAction) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean applicableInState(State s, GroundedAction groundedAction) {
		// TODO Auto-generated method stub
		return false;
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
		for (int i = minPillarHeight; i < maxPillarHeight; i++) {
			GroundedAction a = new SimpleParameterizedGroundedAction(this, 
					new String[]{new Integer(i).toString()});
			actions.add(a);
		}
		return actions;
	}

}

