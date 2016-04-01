package edu.brown.cs.h2r.burlapcraft.action;

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
		int curY = agent.getIntValForAttribute(HelperNameSpace.ATY);
		agent.setValue(HelperNameSpace.ATY, curY + 1);
		StateGenerator.validate(s);
		//return the state we just modified
		System.out.println("\nActionJumpPlaceSimulated performActionHelper\n");
		return s;
	}

}
