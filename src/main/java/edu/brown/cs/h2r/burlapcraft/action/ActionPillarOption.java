package edu.brown.cs.h2r.burlapcraft.action;

import burlap.oomdp.singleagent.GroundedAction;
import burlap.oomdp.singleagent.environment.Environment;
import edu.brown.cs.h2r.burlapcraft.helper.HelperActions;

public class ActionPillarOption implements ActionController {

	protected int delayMS;
	protected Environment environment;
	protected int height = 3;
	
	public ActionPillarOption(int delayMS, Environment e) {
		this.delayMS = delayMS;
		this.environment = e;
	}
	
	@Override
	public int executeAction(GroundedAction ga) {
		
		System.out.println("Pillar");
		for (int i = 0; i < height; i++) {
			HelperActions.jump();
			HelperActions.placeBlock();
		}
		
		return this.delayMS;
	}

}
